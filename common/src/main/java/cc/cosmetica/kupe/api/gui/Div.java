package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.maths.Axis2D;

import java.util.List;

public class Div extends Component<Div.DivStylesheet> {
	protected Div(List<Component<?>> children) {
		super(DivStylesheet.DEFAULT);

		this.children = children;
	}

	private final List<Component<?>> children;

	@Override
	public List<Component<?>> build() {
		return this.children;
	}

	public static class DivStylesheet extends Stylesheet {
		/**
		 * The primary axis of this Div. Components will be laid out in this direction.
		 * @return the primary axis of this
		 */
		Axis2D primaryAxis() {
			return Axis2D.POSITIVE_Y;
		}

		/**
		 * The alignment of components in this div along the primary axis.
		 * @return the alignment of components along the primary axis.
		 */
		Align justifyContent() {
			return Align.START;
		}

		/**
		 * The alignment of components in this div along the secondary axis.
		 * @return the alignment of components along the secondary axis.
		 */
		Align alignItems() {
			return Align.CENTRE;
		}

		public static DivStylesheet DEFAULT = new DivStylesheet();
	}
}
