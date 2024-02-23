package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;

/**
 * 30 years late to the party.
 */
public class Stylesheet {
	/**
	 * Get the margins of this component. These are the boundaries around the component.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the margins of this component.
	 */
	public Margins getMargins(int vw, int vh) {
		return Margins.NONE;
	}

	/**
	 * Get the padding of this component. These are the boundaries within this component.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the padding of this component.
	 */
	public Margins getPadding(int vw, int vh) {
		return Margins.NONE;
	}

	/**
	 * Get the maximum size of this component. This does not include padding or margins.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the maximum size of this component.
	 */
	public Dimensions maximumSize(int vw, int vh) {
		return Dimensions.NONE;
	}

	/**
	 * Get the preferred size of this component. This does not include padding or margins.
	 * This overrides the preferred size due to the component's children.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the preferred size of this component.
	 */
	public Dimensions preferredSize(int vw, int vh) {
		return Dimensions.NONE;
	}

	/**
	 * Get the minimum size of this component. This does not include padding or margins.
	 * The actual minimum size of the component is the maximum, per-axis, of this and the minimum size due to its
	 * children.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the minimum size of this component.
	 */
	public Dimensions minimumSize(int vw, int vh) {
		return Dimensions.NONE;
	}

	/**
	 * Get the amount this object flexes in collections. That is, when objects are being made to fill the space of the
	 * collection, allocate this proportion to this component.
 	 * @return the amount this object flexes in collections. If 0, this component will not flex.
	 */
	public int flex() {
		return 0;
	}

	public static final Stylesheet DEFAULT = new Stylesheet();
}
