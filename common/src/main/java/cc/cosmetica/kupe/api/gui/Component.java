/*
 * Copyright 2024 Cosmetica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Position;
import cc.cosmetica.kupe.api.maths.Region;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Function;

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
	 * @param children a list of the children of this component and their sizes.
	 * @param vw view width, the width of the window.
	 * @param vh view height, the height of the window.
	 */
	public Dimensions minimumSize(List<? extends SizedElement> children, int vw, int vh) {
		if (children.isEmpty()) { // leaf components
			return Dimensions.NONE;
		}

		// by default all components are positioned at top left (including margins and padding)
		return largestSizeWithMargins(children, SizedElement::getMinimumSize);
	}

	/**
	 * Calculate the default size of this component, given its children. This should be consistent with how the component
	 * positions its children. If more than a container, this should also take into account its contents and their
	 * preferred sizing, should infinite space be available.
	 * @param children a list of the children of this component and their sizes.
	 * @param context provides information about the drawing context the component is being sized in.
	 * @return the intrinsic size of this component.
	 */
	public Dimensions intrinsicSize(List<? extends SizedElement> children, Context context) {
		if (children.isEmpty()) { // leaf components
			return Dimensions.NONE;
		}

		// by default all components are positioned at top left (including margins and padding)
		return largestSizeWithMargins(children, SizedElement::getPreferredSize);
	}

	/**
	 * Used for components with a preferred size and ratio to determine an intrinsic size.
	 * @param preferred the preferred dimensions, at the preferred ratio.
	 * @param context the context we are rendering in.
	 * @return the actual intrinsic dimensions in this context.
	 */
	protected Dimensions tryDimensionsWithPreferredRatio(Dimensions preferred, Context context) {
		final int vw = context.getViewWidth();
		final int vh = context.getViewHeight();

		OptionalInt fixedWidth = this.getStyle().get(CommonProperties.WIDTH).apply(vw, vh);

		if (fixedWidth.isPresent()) {
			Dimensions maxDimensions = this.getStyle().get(CommonProperties.MAXIMUM_SIZE).apply(vw, vh);

			int width = Math.min(fixedWidth.getAsInt(), maxDimensions.getWidth());

			// size height respectfully
			float aspectRatio = (float) preferred.getHeight() / preferred.getWidth();
			return new Dimensions(width, (int) (width * aspectRatio));
		}

		OptionalInt fixedHeight = this.getStyle().get(CommonProperties.HEIGHT).apply(vw, vh);

		if (fixedHeight.isPresent()) {
			Dimensions maxDimensions = this.getStyle().get(CommonProperties.MAXIMUM_SIZE).apply(vw, vh);

			int height = Math.min(fixedHeight.getAsInt(), maxDimensions.getHeight());

			// size width respectfully
			float aspectRatio = (float) preferred.getWidth() / preferred.getHeight();
			return new Dimensions((int) (height * aspectRatio), height);
		} else {
			return preferred;
		}
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
	 * @param context provides information about the drawing context the component is being sized in.
	 */
	public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children, Context context) {
		final Position start = new Position(region.getX(), region.getY());

		// By default, lay out children in specified positions.
		for (ResizableElement child : children) {
			final Margins margins = child.getMargins();
			final Margins padding = child.getPadding();

			Position position = start.add(margins.left + padding.left, margins.top + padding.top);
			Dimensions preferred = child.getPreferredSize();

			// default size is preferred size, which accounts for a few properties already.
			// shrink child region so it doesn't extend beyond the borders of the parent region
			int endX = Math.min(position.x + preferred.getWidth() - 1, region.getEndX());
			int endY = Math.min(position.y + preferred.getHeight() - 1, region.getEndY());
			// - 1 is required to match endX() and endY()

			// apply min and max restrictions to find actual width
			Dimensions max = child.getMaximumSize();
			Dimensions min = child.getMinimumSize();

			// + 1 compensates for - 1 earlier.
			int width = Math.min(max.getWidth(), Math.max(min.getWidth(), endX + 1 - position.x));
			int height = Math.min(max.getHeight(), Math.max(min.getHeight(), endY + 1 - position.y));

			Region childRegion = new Region(position, new Dimensions(width, height));
			child.setRenderRegion(childRegion);
		}
	}

	/**
	 * Render this component's background. This includes things like borders.
	 * The border is placed within the padding.
	 */
	public void renderBackground(Canvas canvas, Region region, Margins padding) {
		// we want to draw within the padded region
		Region drawRegion = region.addMargins(padding);

		OptionalInt backgroundColour = this.getStyle().get(CommonProperties.BACKGROUND_COLOUR);
		int borderSize = this.getStyle().get(CommonProperties.BORDER_SIZE);

		if (backgroundColour.isPresent()) {
			if (borderSize > 0) {
				int borderColour = this.getStyle().get(CommonProperties.BORDER_COLOUR);
				canvas.drawRect(drawRegion, borderColour);

				// draw background colour in the shrunk area
				drawRegion = drawRegion.shrink(borderSize);
			}

			// Draw background Colour
			canvas.drawRect(drawRegion, backgroundColour.getAsInt());
		}
		else if (borderSize > 0) {
			int borderColour = this.getStyle().get(CommonProperties.BORDER_COLOUR);

			float r = ((borderColour >> 16) & 0xFF) / 255.0f;
			float g = ((borderColour >> 8) & 0xFF) / 255.0f;
			float b = (borderColour & 0xFF) / 255.0f;

			// ======
			// |    |
			// ======

			// border with transparent centre
			canvas.drawRect( // top
					drawRegion.getX(), drawRegion.getY(),
					drawRegion.getWidth(), borderSize,
					r, g, b);

			canvas.drawRect( // bottom
					drawRegion.getX(), drawRegion.getY() + drawRegion.getHeight() - borderSize,
					drawRegion.getWidth(), borderSize,
					r, g, b);

			canvas.drawRect( // left
					drawRegion.getX(), drawRegion.getY() + borderSize,
					borderSize, drawRegion.getHeight() - borderSize * 2,
					r, g, b);

			canvas.drawRect( // right
					drawRegion.getX() + drawRegion.getWidth() - borderSize, drawRegion.getY() + borderSize,
					borderSize, drawRegion.getHeight() - borderSize * 2,
					r, g, b);
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
	 * Called when a key is pressed on a screen containing this component.
	 * @param keyCode the key code of the key pressed.
	 * @param scanCode the scan code of the key pressed. This is independent of keyboard layout.
	 * @param modifiers modifiers to the key being pressed, like control.
	 */
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	/**
	 * Called when a key is released on a screen containing this component.
	 * @param keyCode the key code of the key pressed.
	 * @param scanCode the scan code of the key pressed. This is independent of keyboard layout.
	 * @param modifiers modifiers to the key being pressed, like control.
	 */
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return false;
	}

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

	/**
	 * Compute the dimensions of the largest total width and largest total height, when the sizes, margins, and padding
	 * of each child is summed up.
	 * @param children the children to compute for.
	 * @param childDimensionGetter the function to get the dimensions for this calculation.
	 * @return a Dimensions object containing the largest total width and largest total height, including margins and padding of the children.
	 */
	private static Dimensions largestSizeWithMargins(List<? extends SizedElement> children, Function<SizedElement, Dimensions> childDimensionGetter) {
		// calculate the largest total width of all children
		// assume the top left corner of all (with margins and padding added) is top-left corner
		int width = 0;
		int height = 0;

		for (SizedElement child : children) {
			Dimensions dimensions = childDimensionGetter.apply(child);

			// check if we need to expand the region for the new widget
			// width
			int childTotalWidth = dimensions.getWidth() + child.getMargins().horizontal() + child.getPadding().horizontal();

			if (childTotalWidth > width) {
				width = childTotalWidth;
			}

			// height
			int childTotalHeight = dimensions.getHeight() + child.getMargins().vertical() + child.getPadding().vertical();

			if (childTotalHeight > height) {
				height = childTotalHeight;
			}
		}

		return new Dimensions(width, height);
	}
}
