package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	public Dimensions preferredSize(List<? extends SizedElement> children, int vw, int vh) {
		return this.size(children, true);
	}

	@Override
	public Dimensions minimumSize(List<? extends SizedElement> children, int vw, int vh) {
		return this.size(children, false);
	}

	/**
	 * Calculate the theoretical size of this element, given its children.
	 * @param children the children of this element.
	 * @param preferred whether to calculate the preferred size. If this is false, the minimum size will instead be
	 *                  calculated.
	 * @return the theoretical size of this element.
	 */
	private Dimensions size(List<? extends SizedElement> children, boolean preferred) {
		int width = 0;
		int height = 0;

		// for size calculation, NEGATIVE and POSITIVE variations can be treated the same
		switch (this.getStyle().get(PRIMARY_AXIS)) {
		case NEGATIVE_X:
		case POSITIVE_X:
			// elements flow in x direction (cumulative)
			// elements stretch in y direction (max)
			for (SizedElement child : children) {
				Dimensions size = preferred ? child.getPreferredSize() : child.getMinimumSize();

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
				Dimensions size = preferred ? child.getPreferredSize() : child.getMinimumSize();

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
		final Dimensions inheritedSize = sizedElement.getInheritedSize();

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
					new Dimensions(inheritedSize.getHeight(), inheritedSize.getWidth()), // w <-> h
					children.stream().map(element -> new AxisRotationAdapter(region, element)).collect(Collectors.toList()),
					primaryAxis == Axis2D.NEGATIVE_Y);
			break;
		case NEGATIVE_X:
		case POSITIVE_X:
			// perform normal operation
			this.resize(region, inheritedSize, children, primaryAxis == Axis2D.NEGATIVE_X);
			break;
		}
	}

	/**
	 * Resize child elements of this div.
	 * @param region the region which this div has been allocated.
	 * @param inheritedSize the inherited preferred size based on children.
	 * @param children the children of this div.
	 * @param reverse whether to order elements from right to left, instead of left to right.
	 */
	private void resize(Region region, Dimensions inheritedSize, List<? extends ResizableElement> children, boolean reverse) {
		// This method is written for a div with components flowing in the X direction.
		// width will be primary axis, height will be secondary axis

		// 1. Resize

		final Object2IntMap<ResizableElement> widths = new Object2IntArrayMap<>();
		final Object2IntMap<ResizableElement> heights = new Object2IntArrayMap<>();

		int preferredLength = inheritedSize.getWidth();
		int actualLength = region.getWidth();

		// 1.1, Find the difference in preferred and actual width. This is the difference we have to compensate.
		int difference = actualLength - preferredLength;

		// if difference > 0, we have more space available
		// if difference < 0, we have less space available
		if (difference < 0) {
			// shrink elements equally. overcorrect rather than undercorrect.
			int shrinkAmount = (int) Math.floor((double)difference / children.size());

			// first, allocate lengths with this naive guess
			for (ResizableElement element : children) {
				widths.put(element, element.getPreferredSize().getWidth() - shrinkAmount);
			}

			// ensure no elements go below their minimum length
			int extraSpace = difference - shrinkAmount * children.size(); // unused space. should be at least 0
			List<ResizableElement> shrinkableElements = new ArrayList<>(children);

			// distribute extra space
			// do one round first so we can catch elements that are below minimum size initially.
			do {
				// find who is below minimum size
				for (int i = shrinkableElements.size() - 1; i >= 0; i--) {
					ResizableElement element = shrinkableElements.get(i);

					Dimensions minimum = element.getMinimumSize();

					if (widths.getInt(element) < minimum.getWidth()) {
						// set width to minimum width
						extraSpace += minimum.getWidth() - widths.getInt(element);
						widths.put(element, minimum.getWidth());
						shrinkableElements.remove(element);
					}
				}
			} while (!shrinkableElements.isEmpty() && extraSpace < 0);

			// try hand out any extra space by giving 1 bit of extra space to any element which wouldnt exceed its max
			// size.
			// if extra space is above zero it should be minimally above zero.
		}

		// 2. Calculate Start Position
		// this depends on the flow direction, and justify content

		// starting positions for placing elements
		int startX = reverse ? region.getEndX() : region.getX();
		int startY = reverse ? region.getEndY() : region.getY();

		// TODO use justify content to change start position

		// 3. Place Elements
		// this is relatively straightforward after the first two steps are done
		//
	}

	/**
	 * The primary axis of this Div. Components will be laid out in this direction.
	 */
	public static final Style.Property<Axis2D> PRIMARY_AXIS = new Style.Property<>(Axis2D.POSITIVE_Y);

	/**
	 * The alignment of components in this div along the primary axis.
	 * The primary axis is the direction in which components are laid out.
	 */
	public static final Style.Property<Align> JUSTIFY_CONTENT = new Style.Property<>(Align.START);

	/**
	 * The alignment of components in this div along the secondary axis.
	 * The secondary axis is the direction perpendicular to the direction components are laid out.
	 */
	public static final Style.Property<Align> ALIGN_ITEMS = new Style.Property<>(Align.START);

	/**
	 * Flips the axis of operations. Y <-> X.
	 * Transforms from a Y-flowing environment to an X-flowing environment.
	 * When regions are set back, it transforms them from the X-flowing environment to a Y-flowing environment.
	 */
	private static class AxisRotationAdapter implements ResizableElement {
		public AxisRotationAdapter(Region parentRegion, ResizableElement wrapped) {
			this.wrapped = wrapped;
			// flip dimensions
			this.maximumSize = new Dimensions(wrapped.getMaximumSize().getHeight(), wrapped.getMaximumSize().getWidth());
			this.preferredSize = new Dimensions(wrapped.getPreferredSize().getHeight(), wrapped.getPreferredSize().getWidth());
			this.inheritedSize = new Dimensions(wrapped.getInheritedSize().getHeight(), wrapped.getInheritedSize().getWidth());
			this.minimumSize = new Dimensions(wrapped.getMinimumSize().getHeight(), wrapped.getMinimumSize().getWidth());
			// rotate margins
			this.margins = rotateMargins(wrapped.getMargins());
			this.padding = rotateMargins(wrapped.getPadding());
			// Region in real space
			// Top left corner is the pivot point, which is the same position in both regions.
			this.parentRegion = parentRegion;
		}

		private final ResizableElement wrapped;
		private final Dimensions preferredSize, inheritedSize, minimumSize, maximumSize;
		private final Margins margins;
		private final Margins padding;
		private final Region parentRegion;

		@Override
		public Dimensions getMaximumSize() {
			return this.maximumSize;
		}

		@Override
		public Dimensions getPreferredSize() {
			return this.preferredSize;
		}

		@Override
		public Dimensions getInheritedSize() {
			return this.inheritedSize;
		}

		@Override
		public Dimensions getMinimumSize() {
			return this.minimumSize;
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
			// when rotating back, we need to consider what corresponds with what more carefully
			// Going from transformed to real, we do a clockwise rotation.
			// ↓y  →x , rotating back corresponds to
			// ←-x ↓y
			// So when considering the x position we need to be careful.

			// We have a shared origin (x0, y0)
			// a position (xt, yt) in transformed space can be written (x0 + dxt, y0 + dyt)
			// Because of direction changes, the real space correspondance is dy=dxt, dx=-dyt
			// However, the origin in the transformed space is also really in a different corner, so x0 => x0 + WIDTH (this goes along with the dx=-dyt)
			// so (xt, yt) = (x0 + dxt, y0 + dyt) => (x0 + WIDTH - dyt, y0 + dxt)

			// However, that is just for mapping points. We actually need to map a region.
			// Luckily, if we can map the true top left corner, we are all good.
			// The true top left corner will be, in the transformed region, the bottom left corner.
			int dxt = region.getX();
			int dyt = region.getEndY(); // bottom left corner in transformed region

			int x = this.parentRegion.getEndX() - dyt;
			int y = this.parentRegion.getY() + dxt;

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
		private static Margins rotateMargins(Margins old) {
			return new Margins(old.right, old.bottom, old.left, old.top); // top right bottom left
		}
	}
}
