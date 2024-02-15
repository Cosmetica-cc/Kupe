package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;

import java.util.List;
import java.util.Map;

/**
 * A GUI component. Whenever relevant states are changed, the tree of components is updated via calling build().
 *
 * All nodes in the GUI tree are components, from the base to the top. Base nodes return an empty list for build().
 */
public abstract class Component {
	protected Component() {
	}

	/**
	 * Get a list of children of this component.
	 * Will be called again if states acquired by this component or a parent component change.
	 * @return the list of children of this component
	 */
	public abstract List<Component> build();

	/**
	 * Get the margins of this component. These are the boundaries around the component.
	 * @return the margins of this component.
	 */
	public Margins getMargins() {
		return Margins.NONE;
	}

	/**
	 * Get the padding of this component. These are the boundaries within this component.
	 * @return the padding of this component.
	 */
	public Margins getPadding() {
		return Margins.NONE;
	}

	/**
	 * Get the preferred size of this component. This does not include padding or margins.
	 * This should be in line with how the resize method works.
	 * @param children the list of children of this component.
	 * @return the preferred size of this component.
	 */
	public Dimensions preferredSize(List<Component> children) {
		return Dimensions.NONE;
	}

	/**
	 * Get the minimum size of this component. This does not include padding or margins.
	 * This should be in line with how the resize method works.
	 * @param children the list of children of this component.
	 * @return the preferred size of this component.
	 */
	public Dimensions minimumSize(List<Component> children) {
		return Dimensions.NONE;
	}

	public void resize(Dimensions dimensions, List<Component> children) {
		// By default, lay out children in a column from top to bottom. Extra space is left unfilled.

		// 1. Figure out the combined size of all children
		// 2. Provide actual dimensions to each child. If too much space, shrink children.
	}

	public void render(Canvas canvas) {
		// By default, do nothing
	}
}
