package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;

import java.util.List;

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
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the preferred size of this component.
	 */
	public Dimensions preferredSize(int vw, int vh) {
		return Dimensions.NONE;
	}

	/**
	 * Get the minimum size of this component. This does not include padding or margins.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the minimum size of this component.
	 */
	public Dimensions minimumSize(int vw, int vh) {
		return Dimensions.NONE;
	}

	public static final Stylesheet DEFAULT = new Stylesheet();
}
