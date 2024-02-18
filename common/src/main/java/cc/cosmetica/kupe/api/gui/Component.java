package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.maths.Dimensions;

import java.util.List;

/**
 * A GUI component. Whenever relevant states are changed, the tree of components is updated via calling build().
 *
 * All nodes in the GUI tree are components, from the base to the top. Base nodes return an empty list for build().
 */
public abstract class Component {
	protected Component() {
	}

	protected Stylesheet stylesheet = Stylesheet.DEFAULT;

	/**
	 * Set the style sheet of this component.
	 * @param stylesheet the stylesheet to apply to this component.
	 * @return
	 */
	public Component withStyle(Stylesheet stylesheet) {
		this.stylesheet = stylesheet;
		return this;
	}

	/**
	 * Get a list of children of this component.
	 * Will be called again if states acquired by this component or a parent component change.
	 * @return the list of children of this component
	 */
	public abstract List<Component> build();

	public void resize(Dimensions dimensions, List<Component> children) {
		// By default, lay out children in a column from top to bottom. Extra space is left unfilled.

		// 1. Figure out the combined size of all children
		// 2. Provide actual dimensions to each child. If too much space, shrink children.
	}

	public void render(Canvas canvas) {
		// By default, do nothing
	}
}
