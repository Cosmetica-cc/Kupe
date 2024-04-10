package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;

import java.util.List;
import java.util.OptionalInt;


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
	 * @return the minimum size of this element.
	 */
	Dimensions getMinimumSize();

	/**
	 * Get the calculated and cached intrinsic size for this element in the current component hierarchy.
	 * This is the 'raw' intrinsic size. For most cases, you should use {@link SizedElement#getPreferredSize()}}.
	 * @return the intrinsic size of this element.
	 */
	Dimensions getIntrinsicSize();

	/**
	 * Get the size this component would size to on its own, had it infinite space.
	 * @return the preferred size of this component.
	 */
	default Dimensions getPreferredSize() {
		Dimensions intrinsic = this.getIntrinsicSize();
		Dimensions min = this.getMinimumSize();

		return new Dimensions(
				Math.max(min.getWidth(), this.getWidth().orElse(intrinsic.getWidth())),
				Math.max(min.getHeight(), this.getHeight().orElse(intrinsic.getHeight()))
		);
	}

	/**
	 * Get the computed width, bounded by the maximum and minimum sizes. Empty if not provided.
	 * @return the width of this component.
	 */
	OptionalInt getWidth();

	/**
	 * Get the computed height, bounded by the maximum and minimum sizes. Empty if not provided.
	 * @return the height of this component.
	 */
	OptionalInt getHeight();

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
