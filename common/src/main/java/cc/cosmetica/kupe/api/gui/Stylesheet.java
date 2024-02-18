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

	public static final Stylesheet DEFAULT = new Stylesheet();
}
