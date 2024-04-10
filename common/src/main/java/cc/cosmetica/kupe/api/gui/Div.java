package cc.cosmetica.kupe.api.gui;

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
import java.util.stream.Collectors;

public class Div extends Component {
	protected Div(List<Component> children) {
		this.children = children;
	}

	private final List<Component> children;

	@Override
	public List<Component> build() {
		return this.children;
	}

	// Div Layout Logic

	@Override
	public Dimensions minimumSize(List<? extends SizedElement> children, int vw, int vh) {
		int width = 0;
		int height = 0;

		// for size calculation, NEGATIVE and POSITIVE variations can be treated the same
		switch (this.getStyle().get(PRIMARY_AXIS)) {
		case NEGATIVE_X:
		case POSITIVE_X:
			// elements flow in x direction (cumulative)
			// elements stretch in y direction (max)
			for (SizedElement child : children) {
				Dimensions size = new Dimensions(
						child.getWidth().orElse(child.getMinimumSize().getWidth()),
						child.getHeight().orElse(child.getMinimumSize().getHeight())
				);

				Margins margins = child.getMargins();
				Margins padding = child.getPadding();

				// x
				// direct width
				width += size.getWidth();
				// padding and margin
				width += margins.left + padding.left + padding.right + margins.right;

				// y
				// direct height
				int childHeight = size.getHeight();
				// padding and margin
				childHeight += margins.bottom + padding.bottom + padding.top + margins.top;

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
				Dimensions size = new Dimensions(
						child.getWidth().orElse(child.getMinimumSize().getWidth()),
						child.getHeight().orElse(child.getMinimumSize().getHeight())
				);

				Margins margins = child.getMargins();
				Margins padding = child.getPadding();

				// x
				// direct width
				int childWidth = size.getWidth();
				// padding and margin
				childWidth += margins.left + padding.left + padding.right + margins.right;

				if (childWidth > width) {
					width = childWidth;
				}

				// y
				// direct height
				height += size.getHeight();
				// padding and margin
				height += margins.bottom + padding.bottom + padding.top + margins.top;
			}
			break;
		}

		return new Dimensions(width, height);
	}

	@Override
	public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children) {
		// The code will be written as if doing all operations on the X axis.
		// However, if we are actually doing actions on the Y axis, we want to flip.
		final Axis2D primaryAxis = this.getStyle().get(PRIMARY_AXIS);

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
		List<? extends ResizableElement> toBeResized = new ArrayList<>(children);

		for (ResizableElement element: toBeResized) {
			OptionalInt eWidth = element.getWidth();

			if (eWidth.isPresent()) {
				// subtract this width from available
				availableWidth -= eWidth.getAsInt();
				widths.put(element, eWidth.getAsInt());
			}
		}

		// only elements in toBeResized are dynamically sized now

		// 1.3: dynamically sized (flexing) allocation
		// only distribute remaining width if it is available!
		if (availableWidth != 0) {
			// distribute based on the flex of the remaining components
			int totalFlex = toBeResized.stream()
					.mapToInt(e -> e.getComponent().getStyle().get(CommonProperties.FLEX))
					.sum();

			boolean repeat = true;

			// distribute available space as well as possible
			// we do multiple takes while max/min bounds are still capping elements, to distribute freed up / taken away space
			while (repeat) {
				repeat = false;
				Iterator<? extends ResizableElement> iterator = toBeResized.iterator();
				final int allocatingSpace = availableWidth;

				Style.Property<Integer> flexProperty = allocatingSpace < 0 ? CommonProperties.FLEX_SHRINK : CommonProperties.FLEX;

				while (iterator.hasNext()) {
					ResizableElement element = iterator.next();
					double flexProportion = (double) element.getComponent().getStyle().get(flexProperty) / totalFlex;
					int width = widths.getOrDefault(element, 0) + (int) Math.floor(allocatingSpace * flexProportion);
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

					availableWidth -= width;
					widths.put(element, width);
				}
			}

			if (availableWidth < 0) {
				// TODO use a logger
				System.err.println("Warning! Div overflow by " + -availableWidth);
			}

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
				// intrinsic size is not separated from minimum size currently
				switch (element.getComponent().getStyle().get(CommonProperties.ALIGN_SELF).orElse(alignItems)) {
				case START:
				case CENTRE:
				case END:
					// intrinsic size (min size)
					heights.put(element, element.getMinimumSize().getHeight());
					break;
				case STRETCH_START:
				case STRETCH_CENTRE:
				case STRETCH_END:
					// as much space as possible (if stretch)
					Margins margins = element.getMargins();
					Margins padding = element.getPadding();
					int theoreticalSpace = region.getHeight() - margins.top - padding.top - padding.bottom - margins.bottom;

					if (theoreticalSpace < element.getMinimumSize().getHeight()) {
						// TODO flood those margins and padding outside of the div (depending on alignment?) if overflows
						// perhaps if align start, just care about top margins. if end just care about end
						heights.put(element, element.getMinimumSize().getHeight());
					} else {
						// if it's over maximum, this is where the alignment type will really be important later
						heights.put(element, Math.min(theoreticalSpace, element.getMaximumSize().getHeight()));
					}

					break;
				}
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

			// place child
			final int width = widths.getInt(element);
			final int height = heights.getInt(element);
			final Align align = element.getComponent().getStyle().get(CommonProperties.ALIGN_SELF).orElse(alignItems);

			int space = region.getHeight() - height;
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

			element.setRenderRegion(new Region((int)x, y, width, height));

			// offset child width
			x += width;

			// post-child space
			switch (justifyContent) {
			case SPACE_AROUND:
				x += 2.0 * availableWidth / children.size();
				break;
			case SPACE_BETWEEN:
				x += availableWidth / (children.size() - 1.0);
				break;
			}
		}
	}

	/**
	 * The primary axis of this Div. Components will be laid out in this direction.
	 */
	public static final Style.Property<Axis2D> PRIMARY_AXIS = new Style.Property<>(Axis2D.POSITIVE_Y);

	/**
	 * The alignment of components in this div along the primary axis.
	 * The primary axis is the direction in which components are laid out.
	 */
	public static final Style.Property<Justify> JUSTIFY_CONTENT = new Style.Property<>(Justify.START);

	/**
	 * The alignment of components in this div along the secondary axis.
	 * The secondary axis is the direction perpendicular to the direction components are laid out.
	 */
	public static final Style.Property<Align> ALIGN_ITEMS = new Style.Property<>(Align.STRETCH_START);

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
		private final Dimensions minimumSize, maximumSize;
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
