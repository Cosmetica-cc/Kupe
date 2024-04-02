package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Position;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.impl.MathsImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A GUI component. Whenever relevant states are changed, the tree of components is updated via calling build().
 *
 * All nodes in the GUI tree are components, from the base to the top. Base nodes return an empty list for build().
 */
public abstract class Component<T extends Stylesheet> {
	protected Component(T defaultStylesheet) {
		this.stylesheet = defaultStylesheet;
	}

	protected T stylesheet;

	/**
	 * The map of absolute positions.
	 * If this is being used to place children, clear it and put components in the map, then call the base resize().
	 */
	protected Map<Component<?>, Position> absolutePositions = new HashMap<>();

	/**
	 * Set the style sheet of this component.
	 * @param stylesheet the stylesheet to apply to this component.
	 * @return this component.
	 */
	public Component<T> withStyle(T stylesheet) {
		this.stylesheet = stylesheet;
		return this;
	}

	public final T getStylesheet() {
		return this.stylesheet;
	}

	/**
	 * Calculate the minimum size given the component's children. This should be consistent with how the component
	 * positions its children.
	 * @param children a list of the children of this component and their minimum sizes.
	 */
	public Dimensions minimumSize(List<? extends ResizableElement> children) {
		if (children.isEmpty()) { // leaf components
			return Dimensions.NONE;
		}

		if (children.size() == 1) {
			return children.get(0).getMinimumSize();
		}

		return MathsImpl.calculateSizeAbsolute(children, ResizableElement::getMinimumSize, this.absolutePositions);
	}

	/**
	 * Calculate the preferred size of this component given the component's children. This should be consistent with how
	 * the component positions its children.
	 * @param children a list of the children of this component and their preferred sizes.
	 */
	public Dimensions preferredSize(List<? extends ResizableElement> children) {
		if (children.isEmpty()) { // leaf components
			return Dimensions.NONE;
		}

		if (children.size() == 1) {
			return children.get(0).getPreferredSize();
		}

		return MathsImpl.calculateSizeAbsolute(children, ResizableElement::getPreferredSize, this.absolutePositions);
	}

	/**
	 * Get a list of children of this component.
	 * Will be called again if states acquired by this component or a parent component change.
	 * @return the list of children of this component
	 */
	public abstract List<Component<?>> build();

	public void resize(Region region, List<? extends ResizableElement> children) {
		// By default, lay out children in specified positions.
		for (ResizableElement child : children) {
			Position position = this.absolutePositions.get(child.getComponent());
			Dimensions dimensions = child.getPreferredSize();
			Dimensions min = child.getMinimumSize();

			// shrink child region so it doesn't extend beyond the borders of the parent region
			int endX = Math.min(position.x + dimensions.getWidth() - 1, region.getEndX());
			int endY = Math.min(position.y + dimensions.getHeight() - 1, region.getEndY());

			int width = Math.min(dimensions.getWidth(), Math.max(min.getWidth(), endX - position.x));
			int height = Math.min(dimensions.getHeight(), Math.max(min.getHeight(), endY - position.y));

			Region childRegion = new Region(position, new Dimensions(width, height));
			child.setRenderRegion(childRegion);
		}
	}

	/**
	 * Render this component. This does not have to handle rendering children.
	 * @param canvas the canvas for drawing on the screen.
	 * @param region the region of the screen this component has been placed in.
	 * @param mouseX the x position of the mouse on the screen.
	 * @param mouseY the y position of the mosue on the screen.
	 */
	public void render(Canvas canvas, Region region, int mouseX, int mouseY) {
		// By default, do nothing
	}

	// Non-Render Methods

	/**
	 * Called when the mouse is clicked on this component.
	 * @param x the x position of the mouse on the screen.
	 * @param y the y position of the mouse on the screen.
	 * @param button the button pressed.
	 * @return whether this component should consume the click. If the click is consumed, children won't be passed the
	 * 		   mouse click.
	 */
	public boolean mouseClicked(double x, double y, int button) {
		return false;
	}

	/**
	 * Called when the mouse is moved on this component.
	 * @param x the x position of the mouse on the screen.
	 * @param y the y position of the mouse on the screen.
	 */
	public void mouseMoved(double x, double y) {
		// No default implementation
	}
}
