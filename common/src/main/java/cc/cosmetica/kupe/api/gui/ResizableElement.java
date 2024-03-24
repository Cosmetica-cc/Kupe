package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Region;

/**
 * Interface to get the calculated and cached preferred and minimum sizes from the component tree, and set the actual
 * render region of a component.
 */
public interface ResizableElement {
	/**
	 * Get the calculated and cached preferred size for this element the current component hierarchy.
	 * @return the preferred size of the element.
	 */
	Dimensions getPreferredSize();

	/**
	 * Get the calculated and cached minimum size for this element in the current component hierarchy.
	 * @return the minimum size of the element.
	 */
	Dimensions getMinimumSize();

	/**
	 * Get the {@linkplain Component component} being resized and positioned.
	 * @return the component underlying this {@link ResizableElement}.
	 */
	Component<?> getComponent();

	/**
	 * Set the render region of this resizable element.
	 * @param region the region on the screen at which to render this resizable element.
	 */
	void setRenderRegion(Region region);
}
