package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Position;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.impl.MathsImpl;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

/**
 * A GUI component. Whenever relevant states are changed, the tree of components is updated via calling build().
 *
 * All nodes in the GUI tree are components, from the base to the top. Base nodes return an empty list for build().
 */
public abstract class Component {
	/**
	 * Base constructor for a component.
	 */
	protected Component() {
		// no-op
	}

	protected @Nullable Stylesheet stylesheet;

	/**
	 * The flattened style settings to apply to this component. These are inherited from this component's stylesheet and
	 * parent stylesheets.
	 */
	private Style style;

	/**
	 * The map of absolute positions.
	 * If this is being used to place children, clear it and put components in the map, then call the base resize().
	 * Positions are relative to the top left corner of the parent element.
	 */
	protected Map<Component, Position> absolutePositions = new HashMap<>();

	/**
	 * Set the style sheet of this component.
	 * @param stylesheet the stylesheet to apply to this component.
	 * @return this component.
	 */
	public Component withStyle(Stylesheet stylesheet) {
		this.stylesheet = stylesheet;
		return this;
	}

	/**
	 * Used internally to set the style of this component.
	 * @param style the flattened style of the component.
	 */
	public void setFlattenedStyle(Style style) {
		this.style = style;
	}

	/**
	 * Get the style of this component.
	 * @return the style of this component.
	 */
	public final Style getStyle() {
		return this.style;
	}

	/**
	 * Get the style sheet attached to this component.
	 * @return the style sheet of this component.
	 */
	public final @Nullable Stylesheet getStylesheet() {
		return this.stylesheet;
	}

	/**
	 * Calculate the minimum size given the component's children. This should be consistent with how the component
	 * positions its children.
	 * @param children a list of the children of this component and their minimum sizes.
	 * @param vw view width, the width of the window.
	 * @param vh view height, the height of the window.
	 */
	public Dimensions minimumSize(List<? extends SizedElement> children, int vw, int vh) {
		if (children.isEmpty()) { // leaf components
			return Dimensions.NONE;
		}

		if (children.size() == 1) {
			return children.get(0).getMinimumSize();
		}

		return MathsImpl.calculateSizeAbsolute(children, SizedElement::getMinimumSize, this.absolutePositions);
	}

	/**
	 * Get a list of children of this component.
	 * Will be called again if states acquired by this component or a parent component change.
	 * @return the list of children of this component
	 */
	public abstract List<Component> build();

	/**
	 * Resize this component's children.
	 * @param region the region allocated to this component.
	 * @param sizedElement this component's dimensions. These may differ from this component's actual allocated size
	 *                     as provided in the region.
	 * @param children a list of children of this component, including their preferred, minimum, and maximum sizes, and
	 *                 a method with which to allocate their regions.
	 */
	public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children) {
		final Position start = new Position(region.getX(), region.getY());

		// By default, lay out children in specified positions.
		for (ResizableElement child : children) {
			Position position = start.add(this.absolutePositions.getOrDefault(child.getComponent(), Position.ZERO));
			Dimensions max = child.getMaximumSize();
			Dimensions min = child.getMinimumSize();

			// shrink child region so it doesn't extend beyond the borders of the parent region
			int endX = Math.min(position.x + max.getWidth() - 1, region.getEndX());
			int endY = Math.min(position.y + max.getHeight() - 1, region.getEndY());

			int width = Math.min(max.getWidth(), Math.max(min.getWidth(), endX - position.x));
			int height = Math.min(max.getHeight(), Math.max(min.getHeight(), endY - position.y));
			// TODO preserve_shape

			Region childRegion = new Region(position, new Dimensions(width, height));
			child.setRenderRegion(childRegion);
		}
	}

	/**
	 * Render this component's background. This includes things like borders.
	 */
	public void renderBackground(Canvas canvas, Region region, Margins padding) {
		OptionalInt backgroundColour = this.getStyle().get(CommonProperties.BACKGROUND_COLOUR);

		if (backgroundColour.isPresent()) {
			int x0 = region.getX() - padding.left;
			int x1 = region.getEndX() + padding.right;
			int y0 = region.getY() - padding.top;
			int y1 = region.getEndY() + padding.bottom;

			int colour = backgroundColour.getAsInt();
			// TODO should we store colour as 3 floats instead to eliminate floating point division?
			float r = ((colour >> 16) & 0xFF) / 255.0f;
			float g = ((colour >> 8) & 0xFF) / 255.0f;
			float b = (colour & 0xFF) / 255.0f;

			canvas.drawRect(x0, y0, x1, y1, r, g, b);
		}
		// TODO border
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
