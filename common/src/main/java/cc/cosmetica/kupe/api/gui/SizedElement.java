package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;

import java.util.List;


/**
 * Interface to get the various dimensions of a component.
 */
public interface SizedElement {
	/**
	 * Get the maximum size of this element. If not specified, will return {@link Dimensions#MAX}.
	 * @return the maximum size of this element.
	 * @see cc.cosmetica.kupe.api.gui.style.CommonProperties#MAXIMUM_SIZE
	 */
	Dimensions getMaximumSize();

	/**
	 * Get the calculated and cached minimum size for this element in the current component hierarchy.
	 * @return the minimum size of the element.
	 */
	Dimensions getMinimumSize();

	/**
	 * Get the calculated and cached margins for this element.
	 * @return the margins for this element.
	 */
	Margins getMargins();

	/**
	 * Get the calculated and cached padding for this element.
	 * @return the padding for this element.
	 */
	Margins getPadding();

	/**
	 * Get the {@linkplain Component component} being resized and positioned.
	 * @return the component underlying this {@link SizedElement}.
	 */
	Component getComponent();
}
