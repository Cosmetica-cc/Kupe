package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.maths.Region;

/**
 * Interface to get the calculated and cached preferred and minimum sizes from the component tree, and set the actual
 * render region of a component.
 */
public interface ResizableElement extends SizedElement {
	/**
	 * Set the render region of this resizable element.
	 * @param region the region on the screen at which to render this resizable element.
	 */
	void setRenderRegion(Region region);
}
