package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Region;

import java.util.List;

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
	public void resize(Region region, List<? extends ResizableElement> children) {
		super.resize(region, children);
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
}
