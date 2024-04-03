package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;

import java.util.List;
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
	public Dimensions preferredSize(List<? extends ResizableElement> children) {
		return this.size(children, true);
	}

	@Override
	public Dimensions minimumSize(List<? extends ResizableElement> children) {
		return this.size(children, false);
	}

	/**
	 * Calculate the theoretical size of this element, given its children.
	 * @param children the children of this element.
	 * @param preferred whether to calculate the preferred size. If this is false, the minimum size will instead be
	 *                  calculated.
	 * @return the theoretical size of this element.
	 */
	private Dimensions size(List<? extends ResizableElement> children, boolean preferred) {
		int width = 0;
		int height = 0;

		// for size calculation, NEGATIVE and POSITIVE variations can be treated the same
		switch (this.getStyle().get(PRIMARY_AXIS)) {
		case NEGATIVE_X:
		case POSITIVE_X:
			// elements flow in x direction (cumulative)
			// elements stretch in y direction (max)
			for (ResizableElement child : children) {
				Dimensions size = preferred ? child.getPreferredSize() : child.getMinimumSize();

				// x
				width += size.getWidth();
				// y
				int childHeight = size.getHeight();

				if (childHeight > height) {
					height = childHeight;
				}
			}
			break;
		case NEGATIVE_Y:
		case POSITIVE_Y:
			// elements flow in y direction (cumulative)
			// elements stretch in x direction (max)
			for (ResizableElement child : children) {
				Dimensions size = preferred ? child.getPreferredSize() : child.getMinimumSize();

				// x
				int childWidth = size.getWidth();

				if (childWidth > width) {
					width = childWidth;
				}
				// y
				height += size.getHeight();
			}
			break;
		}

		return new Dimensions(width, height);
	}

	@Override
	public void resize(Region region, Dimensions preferredSize, List<? extends ResizableElement> children) {
		// The code will be written as if doing all operations on the X axis.
		// However, if we are actually doing actions on the Y axis, we want to flip.
		final Axis2D primaryAxis = this.getStyle().get(PRIMARY_AXIS);

		switch (primaryAxis) {
		case NEGATIVE_Y:
		case POSITIVE_Y:
			// perform flipped operation
			this.resize(
					new Region(region.getY(), region.getX(), region.getHeight(), region.getWidth()), // x,w <-> y,h
					new Dimensions(preferredSize.getHeight(), preferredSize.getWidth()), // w <-> h
					children.stream().map(AxisFlipAdapter::new).collect(Collectors.toList()),
					primaryAxis == Axis2D.NEGATIVE_Y,
					true);
			break;
		case NEGATIVE_X:
		case POSITIVE_X:
			// perform normal operation
			this.resize(region, preferredSize, children, primaryAxis == Axis2D.NEGATIVE_X, false);
			break;
		}
	}

	/**
	 * Resize child elements of this div.
	 * @param region the region which this div has been allocated.
	 * @param children the children of this div.
	 * @param reverse whether to order elements from right to left, instead of left to right.
	 * @param flipMargins whether to flip margins (see: {@link Div#flipMargins(Margins)})
	 */
	private void resize(Region region, Dimensions preferredSize, List<? extends ResizableElement> children, boolean reverse, boolean flipMargins) {
		// This method is written for a div with components flowing in the X direction.

		// 1. Resize

		// we will hereinafter use 'length' to refer to length of the primary axis,
		// and 'depth' to refer to the length of the secondary axis.
		int preferredLength = preferredSize.getWidth();
		int actualLength = region.getWidth();

		int preferredDepth = preferredSize.getHeight();
		int actualDepth = region.getHeight();
		// TODO add margins and padding

		// if any dimension is smaller than preferred, we downscale
		if (actualLength < preferredLength || actualDepth < preferredDepth) {
			// calculate the largest difference (this is the amount we need to downscale)
			// TODO consider elements that don't care about being deformed
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
	 * Flip margins from real Y-flowing environment to transformed X-flowing environment, for use in flipped environments.
	 * @return the new margins.
	 */
	private static Margins flipMargins(Margins old) {
		return new Margins(old.right, old.bottom, old.left, old.top); // top right bottom left
	}

	/**
	 * Flips the axis of operations. Y <-> X.
	 */
	private static class AxisFlipAdapter implements ResizableElement {
		public AxisFlipAdapter(ResizableElement wrapped) {
			this.wrapped = wrapped;
			// flip dimensions
			this.maximumSize = new Dimensions(wrapped.getMaximumSize().getHeight(), wrapped.getMaximumSize().getWidth());
			this.preferredSize = new Dimensions(wrapped.getPreferredSize().getHeight(), wrapped.getPreferredSize().getWidth());
			this.minimumSize = new Dimensions(wrapped.getMinimumSize().getHeight(), wrapped.getMinimumSize().getWidth());
		}

		private final ResizableElement wrapped;
		private final Dimensions maximumSize;
		private final Dimensions preferredSize;
		private final Dimensions minimumSize;

		@Override
		public Dimensions getMaximumSize() {
			return this.maximumSize;
		}

		@Override
		public Dimensions getPreferredSize() {
			return this.preferredSize;
		}

		@Override
		public Dimensions getMinimumSize() {
			return this.minimumSize;
		}

		@Override
		public Component getComponent() {
			return this.wrapped.getComponent();
		}

		@Override
		public void setRenderRegion(Region region) {
			this.wrapped.setRenderRegion(new Region(
					region.getY(),      // x
					region.getX(),      // y
					region.getHeight(), // width
					region.getWidth()   // height
			));
		}
	}
}
