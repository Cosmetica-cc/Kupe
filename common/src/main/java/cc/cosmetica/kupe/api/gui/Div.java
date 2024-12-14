/*
 * Copyright 2024 Cosmetica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.util.ReverseIterator;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A section of the page that lays out multiple elements in a row or column.
 * All divs, if the sum of the components exceed the allocated size, will scroll.
 */
public class Div extends Component {
	public Div(Component... children) {
		this.children = Arrays.asList(children);
	}

	private final List<Component> children;
	private boolean overflow = false;
	private float maxScroll = 0;
	private float scrollPercent;

	@Override
	public List<Component> build() {
		return this.children;
	}

	// Div Layout Logic

	@Override
	public Dimensions minimumSize(List<? extends SizedElement> children, Margins padding, int vw, int vh) {
		return this.getStyle().get(FIXED_CONTAINER) ? this.size(children, padding, SizedElement::getMinimumSize) : Dimensions.NONE;
	}

	@Override
	public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
		return this.size(children, padding, SizedElement::getPreferredSize);
	}

	/**
	 * Calculate the theoretical size of the div, given its children.
	 * @param children the children of the div.
	 * @return the theoretical size of the div, given its children.
	 */
	private Dimensions size(List<? extends SizedElement> children, Margins thisPadding, Function<SizedElement, Dimensions> dimensionGetter) {
		int width = 0;
		int height = 0;

		// for size calculation, NEGATIVE and POSITIVE variations can be treated the same
		switch (this.getStyle().get(FLOW_DIRECTION)) {
		case NEGATIVE_X:
		case POSITIVE_X:
			// elements flow in x direction (cumulative)
			// elements stretch in y direction (max)
			for (SizedElement child : children) {
				Dimensions size = dimensionGetter.apply(child);

				Margins margins = child.getMargins();

				// x
				// direct width
				width += size.getWidth();
				// padding and margin
				width += margins.left + margins.right;

				// y
				// direct height
				int childHeight = size.getHeight();
				// padding and margin
				childHeight += margins.bottom + margins.top;

				if (childHeight > height) {
					height = childHeight;
				}
			}
			break;
		case NEGATIVE_Y:
		case POSITIVE_Y:
			// elements flow in y direction (cumulative)
			// elements stretch in x direction (max)
			for (SizedElement child : children) {
				Dimensions size = dimensionGetter.apply(child);

				Margins margins = child.getMargins();

				// x
				// direct width
				int childWidth = size.getWidth();
				// padding and margin
				childWidth += margins.left + margins.right;

				if (childWidth > width) {
					width = childWidth;
				}

				// y
				// direct height
				height += size.getHeight();
				// padding and margin
				height += margins.bottom + margins.top;
			}
			break;
		}

		return new Dimensions(width + thisPadding.horizontal(), height + thisPadding.vertical());
	}

	@Override
	public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children, Context context) {
		// The code will be written as if doing all operations on the X axis.
		// However, if we are actually doing actions on the Y axis, we want to flip.
		final Axis2D primaryAxis = this.getStyle().get(FLOW_DIRECTION);

		switch (primaryAxis) {
		case NEGATIVE_Y:
		case POSITIVE_Y:
			// perform rotate operation
			// when rotating, we need to consider what corresponds with what carefully
			// Luckily, this is only really important when setting the real positions. See: AxisRotationAdapter.
			// We will rotate around the origin (x, y), so we will keep (x, y) as our origin
			// Height and width are absolute values, so we can just flip them.

			this.resize(
					new Region(region.getX(), region.getY(), region.getHeight(), region.getWidth()), // w <-> h
					children.stream().map(element -> new AxisFlipAdapter(region, element)).collect(Collectors.toList()),
					primaryAxis == Axis2D.NEGATIVE_Y);
			break;
		case NEGATIVE_X:
		case POSITIVE_X:
			// perform normal operation
			this.resize(region, children, primaryAxis == Axis2D.NEGATIVE_X);
			break;
		}
	}

	/**
	 * Resize child elements of this div.
	 * @param region the region which this div has been allocated.
	 * @param children the children of this div.
	 * @param reverse whether to order elements from right to left, instead of left to right.
	 */
	private void resize(Region region, List<? extends ResizableElement> children, boolean reverse) {
		// This method is written for a div with components flowing in the X direction.
		// width will be primary axis, height will be secondary axis
		//System.out.println("Resizing " + this + " to region "+  region);

		// 1. Resize

		final Object2IntMap<ResizableElement> widths = new Object2IntArrayMap<>();
		final Object2IntMap<ResizableElement> heights = new Object2IntArrayMap<>();

		int availableWidth = region.getWidth();

		// 1.1, remove margins from available width
		for (ResizableElement element : children) {
			Margins margins = element.getMargins();
			Margins padding = element.getPadding();

			availableWidth -= margins.left + padding.left + padding.right + margins.right;
		}

		// 1.2 calculate fixed widths first
		// Additionally, for flex-0 elements, use their intrinsic width.
		List<? extends ResizableElement> toBeResized = new ArrayList<>(children);

		for (ResizableElement element: toBeResized) {
			OptionalInt eWidth = element.getWidth();

			if (eWidth.isPresent()) {
				// subtract this width from available
				availableWidth -= eWidth.getAsInt();
				widths.put(element, eWidth.getAsInt());
			} else if (element.getComponent().getStyle().get(CommonProperties.FLEX) == 0) {
				availableWidth -= element.getPreferredSize().getWidth();
				widths.put(element, element.getPreferredSize().getWidth());
			}
		}

		// only elements in toBeResized are dynamically sized now

		// 1.3: dynamically sized (flexing) allocation
		// only distribute remaining width if it is available!
		if (availableWidth != 0) {
			// distribute based on the flex of the remaining components
			boolean repeat = true;

			// distribute available space as well as possible
			// we do multiple takes while max/min bounds are still capping elements, to distribute freed up / taken away space
			while (repeat) {
				repeat = false;
				Iterator<? extends ResizableElement> iterator = toBeResized.iterator();
				final int allocatingSpace = availableWidth;

				Style.Property<Integer> flexProperty;
				int totalFlex;

				if (allocatingSpace < 0) {
					flexProperty = CommonProperties.FLEX_SHRINK;

					totalFlex = toBeResized.stream()
							.mapToInt(e -> e.getComponent().getStyle().get(CommonProperties.FLEX_SHRINK))
							.sum();
				} else {
					flexProperty = CommonProperties.FLEX;

					totalFlex = toBeResized.stream()
							.mapToInt(e -> e.getComponent().getStyle().get(CommonProperties.FLEX))
							.sum();
				}

				if (totalFlex > 0) while (iterator.hasNext()) {
					ResizableElement element = iterator.next();
					double flexProportion = (double) element.getComponent().getStyle().get(flexProperty) / totalFlex;
					int dWidth = (int) Math.floor(allocatingSpace * flexProportion);
					int width = widths.getOrDefault(element, 0) + dWidth;
					// we floor the space we are allocating, as we preferably want to be left over with extra space
					// rather than overflowing

					int minWidth = element.getMinimumSize().getWidth();
					int maxWidth = element.getMaximumSize().getWidth();

					// account for min and max width
					if (width < minWidth) {
						width = minWidth;
						iterator.remove();
						repeat = true;
					} else if (width > maxWidth) {
						width = maxWidth;
						iterator.remove();
						repeat = true;
					}

					availableWidth -= dWidth; // record change in width to available space
					widths.put(element, width);
				}
			}

			if (availableWidth < 0) {
				// TODO use a logger
				System.err.println("Warning! Div overflow by " + -availableWidth);
			}

			// remove non flexing elements before checking if there's leftover space
			// As this is a non-issue if all components are non-flexing or have hit max size
			toBeResized.removeIf(resizableElement -> resizableElement.getComponent().getStyle().get(CommonProperties.FLEX) == 0);

			// available width is now just any extra leftover space. The drops at the bottom of the bucket.
			// allocate it to remaining, dynamically sized elements that can accept it
			if (!toBeResized.isEmpty() && availableWidth > 0) {
				if (availableWidth > toBeResized.size()) {
					System.err.println("Warning! Remaining width to distribute is more than the number of elements that can accept it. Likely issue in child resizing.");
				}

				for (ResizableElement element : toBeResized) {
					int width = widths.getInt(element);

					if (width < element.getMaximumSize().getWidth()) {
						widths.put(element, width + 1);
						availableWidth -= 1;

						if (availableWidth == 0) {
							break;
						}
					}
				}
			}
		} // TODO remove elements if they go outside the size(?) (sed future Scroll region element)

		// 1.4 Calculate heights.
		final Align alignItems = this.getStyle().get(ALIGN_ITEMS);

		for (ResizableElement element : children) {
			// fixed heights
			if (element.getHeight().isPresent()) {
				heights.put(element, element.getHeight().getAsInt());
			}
			else {
				// otherwise allocate based on alignment stretch type: intrinsic or stretch.
				// respect max and min sizes.
				switch (element.getComponent().getStyle().get(CommonProperties.ALIGN_SELF).orElse(alignItems)) {
				case START:
				case CENTRE:
				case END:
					// preferred intrinsic size, capped to the height of this container available for the component
					// TODO some way to adapt instrinsic height after squishing width?
					int availableHeight = region.getHeight() - element.getMargins().vertical() - element.getPadding().vertical();
					heights.put(element, Math.min(availableHeight, element.getPreferredSize().getHeight()));
					break;
				case STRETCH_START:
				case STRETCH_CENTRE:
				case STRETCH_END:
					// as much space as possible (if stretch)
					Margins margins = element.getMargins();
					Margins padding = element.getPadding();
					int theoreticalSpace = region.getHeight() - margins.top - padding.top - padding.bottom - margins.bottom;

					if (theoreticalSpace < element.getMinimumSize().getHeight()) {
						// perhaps if align start, just care about top margins. if end just care about end
						heights.put(element, element.getMinimumSize().getHeight());
					} else {
						// if it's over maximum, this is where the alignment type will really be important later
						heights.put(element, Math.min(theoreticalSpace, element.getMaximumSize().getHeight()));
					}

					break;
				}
			}

			// constrain heights to height of div
			int paddingMarginSecondary = element.getMargins().vertical() + element.getPadding().vertical();
			int totalOccupiedHeight = heights.getInt(element) + paddingMarginSecondary;

			if (totalOccupiedHeight > region.getHeight()) {
				int constrainedHeight = region.getHeight() - paddingMarginSecondary;

				// but not at the expense of the element's minimum height
				heights.put(element,
						Math.max(
								constrainedHeight,
								element.getMinimumSize().getHeight()
						)
				);
			}

		}

		// 2. Calculate Start Position (along main axis)
		Justify justifyContent = this.getStyle().get(JUSTIFY_CONTENT);

		// starting positions for placing elements
		double x = region.getX();

		// availableWidth contains the space remaining. this is distributed by justify content.
		// if it would overflow, space around and space between don't really make sense anymore
		if (availableWidth < 0 && (justifyContent == Justify.SPACE_AROUND || justifyContent == Justify.SPACE_BETWEEN)) {
			justifyContent = Justify.CENTRE;
		}

		// 3. Place Elements
		// this is relatively straightforward after the first two steps are done
		// Reverse + START/END is the same as flipping list iteration order and END/START
		// The other types are symmetrical so we can just flip the list iteration.
		Iterator<? extends ResizableElement> childIterator = reverse ? new ReverseIterator<>(children) : children.iterator();

		if (reverse) {
			if (justifyContent == Justify.START) {
				justifyContent = Justify.END;
			} else if (justifyContent == Justify.END) {
				justifyContent = Justify.START;
			}
		}

		//System.out.println(availableWidth);

		// initial space
		switch (justifyContent) {
		case END:
			x += availableWidth;
			break;
		case CENTRE:
			// may as well do integer division for speed since this is the only space block, and it will round down anyway
			// the only slight difference will be when available width is negative
			x += (double) (availableWidth / 2);
			break;
		case SPACE_AROUND:
			x += (double) availableWidth / children.size();
			break;
		}
		// other justify contents don't have initial space

		while (childIterator.hasNext()) {
			ResizableElement element = childIterator.next();
			final Margins margins = element.getMargins();
			final Margins padding = element.getPadding();

			// add left padding and margin
			x += margins.left + padding.left;

			// place child

			final int width = widths.getInt(element);
			final int height = heights.getInt(element);
			final Align align = element.getComponent().getStyle().get(CommonProperties.ALIGN_SELF).orElse(alignItems);

			int space = region.getHeight() - height - margins.top - padding.top - padding.bottom - margins.bottom;
			int y = region.getY();

			// align at start, middle, or end on secondary axis
			switch (align) {
			case START:
			case STRETCH_START:
				// no offset
				break;
			case CENTRE:
			case STRETCH_CENTRE:
				y += space/2;
				break;
			case END:
			case STRETCH_END:
				y += space;
				break;
			}

			// account for padding and margins offsetting y start
			y += margins.top + padding.top;

			element.setRenderRegion(new Region((int)x, y, width, height));

			// offset child width and right margin/padding
			x += width + padding.right + margins.right;

			// post-child space
			switch (justifyContent) {
			case SPACE_AROUND:
				x += 2.0 * availableWidth / children.size();
				break;
			case SPACE_BETWEEN:
				if (childIterator.hasNext())
					x += availableWidth / (children.size() - 1.0);
				break;
			}
		}

		// set overflow flag
		this.overflow = (int)x > region.getEndX();// TODO should we have int cast?
		this.maxScroll = (float)x - region.getEndX();
		this.grabbed = false;
//		if (this.overflow) {
//			System.out.println(super.toString() + " Overflow = " + region.getEndX() + " < " + x);
//		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Div ");

		if (this.getStyle() != null) {
			sb.append("flow=").append(this.getStyle().get(FLOW_DIRECTION));
			sb.append(" justify=").append(this.getStyle().get(JUSTIFY_CONTENT));
			sb.append(" alignItems=").append(this.getStyle().get(ALIGN_ITEMS)).append(' ');
		}

		sb.append('{');
		for (Component child : this.children) {
			sb.append('\n').append(child.toString());
		}
		sb.append('}');
		return sb.toString();
	}

	@Override
	public boolean mouseScrolled(double x, double y, double delta) {
		// scroll% = amount Scrolled / maxScroll
		// add to exising scroll%
		float newScroll = this.scrollPercent - (float) ((delta * PX_PER_SCROLL) / this.maxScroll);
		// clamp
		if (newScroll > 1) newScroll = 1;
		else if (newScroll < 0) newScroll = 0;

		boolean scrollChanged = this.scrollPercent != newScroll;
		this.scrollPercent = newScroll;
		return scrollChanged;
	}

	private boolean grabbed;
	private float grabOffset;
	private float scrollbarTopY, scrollbarLeftX, scrollbarSize; // passed from render to click

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		if (overflow) {
			if (this.isVerticalFlow()) {
				if (
						y >= this.scrollbarTopY && y < (this.scrollbarTopY + this.scrollbarSize)
						&& x >= this.scrollbarLeftX && x < (this.scrollbarLeftX + DEFAULT_SCROLLBAR_THICKNESS)
				) {
					this.grabbed = true;
					this.grabOffset = (float) y - this.scrollbarTopY;
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean mouseReleased(double x, double y, int button) {
		if (this.grabbed) {
			this.grabbed = false;
			return true;
		}

		return false;
	}

	@Override
	public void paintBackground(Canvas canvas, Region region, Margins padding) {
		super.paintBackground(canvas, region, padding);

		if (overflow) {
			// stencil
			canvas.useScissor(region);
		}
	}

	@Override
	public void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
		if (overflow) {
			// scrollbar
			if (this.isVerticalFlow()) {
				// height of the div's 'view'. But not of all its contents.
				final float divVH = region.getHeight();

				float pageCover = divVH / (divVH + this.maxScroll);
				int scrollbarSize = (int)Math.max(10, pageCover * divVH);// minimum height of 10 pixels

				// update scroll position for drag
				if (this.grabbed) {
					this.scrollPercent = (mouseY - this.grabOffset - region.getY()) / (divVH - scrollbarSize);
					// clamp
					if (this.scrollPercent > 1) this.scrollPercent = 1;
					else if (this.scrollPercent < 0) this.scrollPercent = 0;
				}

				float scrollbarTopY = region.getY() + this.scrollPercent * (divVH - scrollbarSize);
				// pass to click
				this.scrollbarLeftX = region.getEndX() - DEFAULT_SCROLLBAR_THICKNESS;
				this.scrollbarSize = scrollbarSize;
				this.scrollbarTopY = scrollbarTopY;

				canvas.drawRect(
						region.getEndX() - DEFAULT_SCROLLBAR_THICKNESS, region.getY(),
						DEFAULT_SCROLLBAR_THICKNESS, region.getHeight(),
						50.0f, 0, 0, 0
				);
				canvas.drawRect(
						region.getEndX() - DEFAULT_SCROLLBAR_THICKNESS, (int)scrollbarTopY,
						DEFAULT_SCROLLBAR_THICKNESS, scrollbarSize,
						50.0f, 0.5f, 0.5f, 0.5f
				);

				final float scrollBarColour = 192.0f/255.0f;
				canvas.drawRect(
						region.getEndX() - DEFAULT_SCROLLBAR_THICKNESS, (int)scrollbarTopY,
						DEFAULT_SCROLLBAR_THICKNESS - 1, scrollbarSize - 1,
						50.0f, scrollBarColour, scrollBarColour, scrollBarColour
				);
			}

			//shift contents by scroll amount
			if (this.isVerticalFlow()) {
				canvas.scroll(0, -this.scrollPercent * this.maxScroll);
			} else {
				canvas.scroll(-this.scrollPercent * this.maxScroll, 0);
			}
		}
	}

	public boolean isVerticalFlow() {
		return this.getStyle().get(FLOW_DIRECTION) == Axis2D.POSITIVE_Y ||
				this.getStyle().get(FLOW_DIRECTION) == Axis2D.NEGATIVE_Y;
	}

	/**
	 * The primary axis of this Div. Components will be laid out in this direction.
	 */
	public static final Style.Property<Axis2D> FLOW_DIRECTION = new Style.Property<>("flowDirection", Axis2D.POSITIVE_Y, false);

	/**
	 * The alignment of components in this div along the primary axis.
	 * The primary axis is the direction in which components are laid out.
	 */
	public static final Style.Property<Justify> JUSTIFY_CONTENT = new Style.Property<>("justifyContent", Justify.START, false);

	/**
	 * The alignment of components in this div along the secondary axis.
	 * The secondary axis is the direction perpendicular to the direction components are laid out.
	 */
	public static final Style.Property<Align> ALIGN_ITEMS = new Style.Property<>("alignItems", Align.STRETCH_START, false);

	/**
	 * Whether this div must expand to fit all components. If this is false, the div will ignore the minimum size of
	 * its children when determining its own minimum size. This promotes overflowing and scrolling behaviour.
	 */
	public static final Style.Property<Boolean> FIXED_CONTAINER = new Style.Property<>("fixedContainer", true, false);

	private static final int DEFAULT_SCROLLBAR_THICKNESS = 6;
	private static final int PX_PER_SCROLL = 10;

	/**
	 * Flips the axis of operations. Y <-> X. Essentially mirrors along a line from top left corner down and right, 45 degrees.
	 * Transforms from a Y-flowing environment to an X-flowing environment.
	 * When regions are set back, it transforms them from the X-flowing environment to a Y-flowing environment.
	 */
	private static class AxisFlipAdapter implements ResizableElement {
		public AxisFlipAdapter(Region parentRegion, ResizableElement wrapped) {
			this.wrapped = wrapped;
			// flip dimensions
			this.maximumSize = new Dimensions(wrapped.getMaximumSize().getHeight(), wrapped.getMaximumSize().getWidth());
			this.minimumSize = new Dimensions(wrapped.getMinimumSize().getHeight(), wrapped.getMinimumSize().getWidth());
			this.intrinsicSize = new Dimensions(wrapped.getIntrinsicSize().getHeight(), wrapped.getIntrinsicSize().getWidth());
			this.width = wrapped.getHeight();
			this.height = wrapped.getWidth();
			// rotate margins
			this.margins = flipMargins(wrapped.getMargins());
			this.padding = flipMargins(wrapped.getPadding());
			// Region in real space
			// Top left corner is the pivot point, which is the same position in both regions.
			this.parentRegion = parentRegion;
		}

		private final ResizableElement wrapped;
		private final Dimensions minimumSize, maximumSize, intrinsicSize;
		private final OptionalInt width, height;
		private final Margins margins;
		private final Margins padding;
		private final Region parentRegion;

		@Override
		public Dimensions getMaximumSize() {
			return this.maximumSize;
		}

		@Override
		public Dimensions getMinimumSize() {
			return this.minimumSize;
		}

		@Override
		public Dimensions getIntrinsicSize() {
			return this.intrinsicSize;
		}

		@Override
		public OptionalInt getWidth() {
			return width;
		}

		@Override
		public OptionalInt getHeight() {
			return height;
		}

		@Override
		public Margins getMargins() {
			return margins;
		}

		@Override
		public Margins getPadding() {
			return padding;
		}

		@Override
		public Component getComponent() {
			return this.wrapped.getComponent();
		}

		@Override
		public void setRenderRegion(Region region) {
			// Because we're mirroring as described above, the corner of the element which is top left stays the same
			// Its new coordinates will simply be offset (dy, dx) from the top left corner of the region.
			int dx = region.getX() - this.parentRegion.getX();
			int dy = region.getY() - this.parentRegion.getY();

			int x = this.parentRegion.getX() + dy;
			int y = this.parentRegion.getY() + dx;

			// Width and height simply flip.
			this.wrapped.setRenderRegion(new Region(
					x,
					y,
					region.getHeight(), // width
					region.getWidth()   // height
			));
		}

		/**
		 * Flip margins from real Y-flowing environment to transformed X-flowing environment, for use in flipped environments.
		 * @return the new margins.
		 */
		private static Margins flipMargins(Margins old) {
			// left <-> top
			// bottom <-> right
			return new Margins(old.left, old.bottom, old.right, old.top); // top right bottom left
		}
	}
}
