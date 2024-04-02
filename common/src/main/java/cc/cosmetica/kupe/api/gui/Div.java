package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Axis2D;

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
