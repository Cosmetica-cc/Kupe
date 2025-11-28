/*
 * Copyright 2024, 2025 Cosmetica
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
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

	protected @Nullable Style declaredStyle;
	private Collection<String> tags = ImmutableList.of();

	/**
	 * The flattened style settings to apply to this component. These are inherited from this component's stylesheet and
	 * parent stylesheets.
	 */
	private Style style;

	/**
	 * Get the new preferred height after shrinking width.
	 * Doesn't need to account for min/max.
	 * @param newWidth the new width.
	 * @param height the current height.
	 * @return the new preferred height.
	 */
	public int shrinkHeight(int newWidth, int height, Context context) {
		return height;
	}

	/**
	 * Get the new preferred width after shrinking height
	 * @param newHeight the new height.
	 * @param width the current width.
	 * @return the new preferred width.
	 */
	public int shrinkWidth(int newHeight, int width, Context context) {
		return width;
	}

	/**
	 * Set the style for this component instance.
	 * @param style the style to apply to this component.
	 * @return this component.
	 */
	public Component withStyle(Style style) {
		this.declaredStyle = style;
		return this;
	}

	/**
	 * Apply tags to this component.
	 * @param tags the tags to apply to this component. This overrides any existing tags.
	 * @return this component.
	 */
	public Component tag(String ...tags) {
		this.tags = ImmutableSet.copyOf(tags);
		return this;
	}

	public Collection<String> getTags() {
		return this.tags;
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
	 * Get the style attached to this component.
	 * @return the style overrides declared for this component.
	 */
	public final @Nullable Style getDeclaredStyle() {
		return this.declaredStyle;
	}

	/**
	 * Override to declare a stylesheet for this component.
	 */
	public @Nullable Stylesheet getStylesheet() {
		return null;
	}

	/**
	 * Calculate the minimum size given the component's children, inclusive of the component's padding.
	 * This should be consistent with how the component positions its children.
	 * @param children a list of the children of this component and their sizes.
	 * @param vw view width, the width of the window.
	 * @param vh view height, the height of the window.
	 */
	public Dimensions minimumSize(List<? extends SizedElement> children, Margins padding, int vw, int vh) {
		if (children.isEmpty()) { // leaf components
			return Dimensions.NONE;
		}

		// by default all components are positioned at top left (including margins)
		return largestSizeWithMargins(children, padding, SizedElement::getMinimumSize);
	}

	/**
	 * Calculate the default size of this component, given its children, inclusive of the component's padding.
	 * This should be consistent with how the component positions its children. If more than a container,
	 * this should also take into account its contents and their preferred sizing, should infinite space be available.
	 * @param children a list of the children of this component and their sizes.
	 * @param context provides information about the drawing context the component is being sized in.
	 * @return the intrinsic size of this component.
	 */
	public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
		if (children.isEmpty()) { // leaf components
			return Dimensions.NONE;
		}

		// by default all components are positioned at top left (including margins and padding)
		return largestSizeWithMargins(children, padding, SizedElement::getPreferredSize);
	}

	/**
	 * Used for components with a preferred size and ratio to determine an intrinsic size.
	 * @param preferred the preferred dimensions, at the preferred ratio. Excluding padding.
	 * @param context the context we are rendering in.
	 * @return the actual intrinsic dimensions in this context. With padding included.
	 */
	protected Dimensions tryDimensionsWithPreferredRatio(Dimensions preferred, Margins padding, Context context) {
		final int vw = context.getViewWidth();
		final int vh = context.getViewHeight();

		Dimensions maxDimensions = this.getStyle().get(CommonProperties.MAXIMUM_SIZE).apply(vw, vh, 0, 0);

		OptionalInt fixedWidth = this.getStyle().get(CommonProperties.WIDTH).apply(vw, vh, 0, 0);

		if (fixedWidth.isPresent()) {
			int width = Math.min(fixedWidth.getAsInt(), maxDimensions.getWidth());
			return shrinkByWidth(width - padding.horizontal(), preferred, padding);
		}

		OptionalInt fixedHeight = this.getStyle().get(CommonProperties.HEIGHT).apply(vw, vh, 0, 0);

		if (fixedHeight.isPresent()) {
			int height = Math.min(fixedHeight.getAsInt(), maxDimensions.getHeight());
			return shrinkByHeight(height - padding.vertical(), preferred, padding);
		}

		// treat clamp like fixed dimensions
		Dimensions shrunkWidth = null, shrunkHeight = null;

		if (maxDimensions.getWidth() < preferred.getWidth() + padding.horizontal()) {
			fixedWidth = OptionalInt.of(maxDimensions.getWidth());
			shrunkWidth = shrinkByWidth(maxDimensions.getWidth() - padding.horizontal(), preferred, padding);
		}
		if (maxDimensions.getHeight() < preferred.getHeight() + padding.vertical()) {
			fixedHeight = OptionalInt.of(maxDimensions.getHeight());
			shrunkHeight = shrinkByHeight(maxDimensions.getHeight() - padding.vertical(), preferred, padding);
		}

		if (fixedHeight.isPresent() && fixedWidth.isPresent()) {
			// choose the smaller shrink so it respects both maximum dimensions
			return shrunkHeight.getWidth() < shrunkWidth.getWidth() ? shrunkHeight : shrunkWidth;
		} else if (fixedWidth.isPresent()) {
			return shrunkWidth;
		} else if (fixedHeight.isPresent()) {
			return shrunkHeight;
		} else {
			// no fixed width and height
			return new Dimensions(preferred.getWidth() + padding.horizontal(), preferred.getHeight() + padding.vertical());
		}
	}

	/**
	 * Try fixed dimensions for an intrinsic size. Limited by style-set sizes. No ratio is preserved.
	 * @param preferred the preferred sizing.
	 * @return the intrinsic size.
	 */
	protected Dimensions tryFixed(Dimensions preferred, Margins padding, Context context) {
		final int vw = context.getViewWidth();
		final int vh = context.getViewHeight();
		int width = preferred.getWidth() + padding.horizontal();
		int height = preferred.getHeight() + padding.vertical(); // style-specified dimensions (min,max,fixed) include padding
		Dimensions maxDimensions = this.getStyle().get(CommonProperties.MAXIMUM_SIZE).apply(vw, vh, 0, 0);
		Dimensions minDimensions = new Dimensions(
				this.getStyle().get(CommonProperties.MIN_WIDTH).apply(vw, vh, 0, 0).orElse(0),
				this.getStyle().get(CommonProperties.MIN_HEIGHT).apply(vw, vh, 0, 0).orElse(0)
		);

		OptionalInt fixedWidth = this.getStyle().get(CommonProperties.WIDTH).apply(vw, vh, 0, 0);

		if (fixedWidth.isPresent()) {
			width = Math.max(Math.min(fixedWidth.getAsInt(), maxDimensions.getWidth()), minDimensions.getWidth());
		}

		OptionalInt fixedHeight = this.getStyle().get(CommonProperties.HEIGHT).apply(vw, vh, 0, 0);

		if (fixedHeight.isPresent()) {
			height = Math.max(Math.min(fixedHeight.getAsInt(), maxDimensions.getHeight()), minDimensions.getWidth());
		}

		return new Dimensions(width, height);//we aren't clamping preferred dimensions to max/min because we know the system will do that
	}

	/**
	 * Adjust given dimensions to match a fixed width, and add padding.
	 * @param width the fixed width to target, excluding padding.
	 * @param preferred the preferred dimensions, excluding padding.
	 * @param padding padding to add to the final dimensions.
	 * @return the shrunk dimensions.
	 */
	private static Dimensions shrinkByWidth(int width, Dimensions preferred, Margins padding) {
		// size height respectfully
		float aspectRatio = (float) preferred.getHeight() / preferred.getWidth();
		return new Dimensions(width + padding.horizontal(), (int) (width * aspectRatio) + padding.vertical());
	}

	/**
	 * Adjust given dimensions to match a fixed height, and add padding.
	 * @param height the fixed height to target, excluding padding.
	 * @param preferred the preferred dimensions, excluding padding.
	 * @param padding padding to add to the final dimensions.
	 * @return the shrunk dimensions.
	 */
	private static Dimensions shrinkByHeight(int height, Dimensions preferred, Margins padding) {
		// size width respectfully
		float aspectRatio = (float) preferred.getWidth() / preferred.getHeight();
		return new Dimensions((int) (height * aspectRatio) + padding.horizontal(), height + padding.vertical());
	}

	/**
	 * Get a list of children of this component.
	 * Will be called again if states acquired by this component or a parent component change.
	 * @return the list of children of this component
	 */
	public abstract List<Component> build();

	/**
	 * Resize this component's children.
	 * @param region the region allocated to this component. This is solely content size and does not include padding.
	 * @param sizedElement this component's specified dimensions. These may differ from this component's actual
	 *                     allocated size as provided in the region.
	 * @param children a list of children of this component, including their preferred, minimum, and maximum sizes, and
	 *                 a method with which to allocate their regions.
	 * @param context provides information about the drawing context the component is being sized in.
	 */
	public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children, Context context) {
		final Position start = new Position(region.getX(), region.getY());

		// By default, lay out children in specified positions.

		// Pass down the size given this component: components will try fill this component's space.
		// this component has acted as ambassador anyway.
		// Considerations: Fixed width/height; (Maximum Size: accounted for already);
		// Aspect ratios in intrinsic sizes (already effectively forwards intrinsic size in case of 1 component)
		for (ResizableElement child : children) {
			final Margins margins = child.getMargins();

			OptionalInt fixedWidth = child.getComponent().getStyle().get(CommonProperties.WIDTH).apply(context.getViewWidth(), context.getViewHeight(), region.getWidth(), region.getHeight());
			OptionalInt fixedHeight = child.getComponent().getStyle().get(CommonProperties.HEIGHT).apply(context.getViewWidth(), context.getViewHeight(), region.getWidth(), region.getHeight());

			int availableWidth = region.getWidth() - margins.horizontal();
			int availableHeight = region.getHeight() - margins.vertical();

			// dimensions including padding
			Dimensions attempting = new Dimensions(
					fixedWidth.orElse(availableWidth),
					fixedHeight.orElse(availableHeight)
			);

			Region childRegion = layChildToPreferredSize(region, start, attempting, child);
			child.setRenderRegion(childRegion);
		}
	}

	/**
	 * Lay the child to a given preferred size it should take.
	 * @param region the content region of the parent element.
	 * @param start the start position from which to position the element.
	 * @param preferred the preferred size for the element (including padding). This doesn't have to be the <i>child's</i> preferred size.
	 * @param child the child element itself.
	 * @return the content region of the child element.
	 */
	protected static Region layChildToPreferredSize(Region region, Position start, Dimensions preferred, SizedElement child) {
		final Margins margins = child.getMargins();

		Position position = start.add(margins.left, margins.top);

		// default size is preferred size, which accounts for a few properties already.
		// shrink child region so it doesn't extend beyond the borders of the parent
		int endX = Math.min(position.x + preferred.getWidth() - 1, region.getFinalX());
		int endY = Math.min(position.y + preferred.getHeight() - 1, region.getFinalY());
		// - 1 is required to match finalX() and finalY()

		// apply min and max restrictions to find actual width
		Dimensions max = child.getMaximumSize();
		Dimensions min = child.getMinimumSize();

		// + 1 compensates for - 1 earlier.
		int width = Math.min(max.getWidth(), Math.max(min.getWidth(), endX + 1 - position.x));
		int height = Math.min(max.getHeight(), Math.max(min.getHeight(), endY + 1 - position.y));

		// child region
		// subtract padding to get content region
		final Margins padding = child.getPadding();
		return new Region(position, new Dimensions(width, height)).shrinkMargins(padding);
	}

	/**
	 * Render this component. This does not handle rendering children.
	 * @param canvas the canvas for drawing on the screen.
	 * @param region the region of the screen this component has been placed in.
	 * @param padding the padding around this component's content region.
	 * @param mouseX the x position of the mouse on the screen.
	 * @param mouseY the y position of the mosue on the screen.
	 */
	public void render(Canvas canvas, Region region, Margins padding, int mouseX, int mouseY) {
		this.paintBackground(canvas, region, padding);

		// stencil
		@Nullable Region scissorRegion = this.getScissorRegion(region);
		if (scissorRegion != null) {
			canvas.useScissor(region, true);
		}

		this.paint(canvas, region, mouseX, mouseY);
	}

	/**
	 * Paint this component's background. This includes things like borders.
	 * The border is placed within the padding.
	 */
	protected void paintBackground(Canvas canvas, Region region, Margins padding) {
		// we want to draw within the padded region
		Region drawRegion = region.addMargins(padding);

		OptionalInt backgroundColour = this.getStyle().get(CommonProperties.BACKGROUND_COLOUR);

		if (backgroundColour.isPresent()) {
			// Draw background Colour
			canvas.drawRect(drawRegion, backgroundColour.getAsInt());
		}

		Optional<Border> border = this.getStyle().get(CommonProperties.BORDER);

		if (border.isPresent()) {
			border.get().paint(canvas, drawRegion, this.getStyle());
		}
	}

	/**
	 * Paint the contents of this component. This does not handle rendering children.
	 * @param canvas the canvas for drawing on the screen.
	 * @param region the region of the screen this component has been placed in.
	 * @param mouseX the x position of the mouse on the screen.
	 * @param mouseY the y position of the mosue on the screen.
	 */
	protected void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
		// No default behaviour.
	}

	/**
	 * Paint deocrations after this component and its children are rendered.
	 * @param canvas the canvas for drawing on the screen.
	 * @param region the region of the screen this component has been placed in.
	 * @param mouseX the x position of the user's mouse.
	 * @param mouseY the y position of the user's mouse.
	 */
	public void paintDecorations(Canvas canvas, Region region, int mouseX, int mouseY) {
		// Draw tooltip
		Optional<Tooltip> tooltip = this.getStyle().get(CommonProperties.TOOLTIP);

		if (tooltip.isPresent() && region.contains(mouseX, mouseY)) {
			tooltip.get().render(canvas, mouseX, mouseY);
		}
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
	 * Called when a character is typed on a screen containing this component.
	 * @param symbol the symbol typed.
	 * @param modifiers modifiers to the key being pressed, like control.
	 */
	public boolean charTyped(char symbol, int modifiers) { return false; }

	/**
	 * Called when the mouse is clicked on this component, or screen, as specified in {@link CommonProperties#POINTER_EVENTS}.
	 * @param target the frontmost, non-occluded element that received this event.
	 * @param x the x position of the mouse on the screen.
	 * @param y the y position of the mouse on the screen.
	 * @param button the button pressed.
	 */
	public void mouseClicked(Element target, double x, double y, int button) {
	}

	/**
	 * Called when the mouse button is released on a screen containing this component.
	 * @param x the x position of the mouse on the screen.
	 * @param y the y position of the mouse on the screen.
	 * @param button the button released.
	 */
	public void mouseReleased(double x, double y, int button) {
	}

	/**
	 * Called when the mouse is moved on this component.
	 * @param region this component's region.
	 * @param x the x position of the mouse on the screen.
	 * @param y the y position of the mouse on the screen.
	 */
	public void mouseMoved(Region region, double x, double y) {
		// No default implementation
	}

	/**
	 * Called when the mouse is scrolled on this component.
	 * @param x the x position of the mouse on the screen.
	 * @param y the y position of the mouse on the screen.
	 * @param delta the amount the mouse scrolled.
	 * @return whether to consume the mouse scroll. Children have priority over parents.
	 */
	public boolean mouseScrolled(double x, double y, double delta) {
		// No default implementation
		return false;
	}

	/**
	 * Called when this component is unmounted. That is, when it disappears from the hierarchy.
	 */
	public void unmount() {
		// No default behaviour.
	}

	/**
	 * Get whether this component (not including children) is occluding components underneath.
	 * The coordinates provided are not guaranteed to be contained by the given {@link Region}.
	 * @param region the render region of this component in global coordinates.
	 * @param scissorRegion the scissor region of this component in global coordinates.
	 * @param x the x position at which the mouse clicked.
	 * @param y the y position at which the mouse clicked.
	 * @param decorations whether this testing for occlusion by overlaid decorations.
	 * @return whether this component is visible at the given position.
	 * @apiNote used to determine obstruction for {@linkplain PointerEvents pointer events}.
	 */
	public boolean isOccluding(Region region, Region scissorRegion, int x, int y, boolean decorations) {
		return !decorations && region.intersect(scissorRegion).contains(x, y);
	}

	/**
	 * Get the scissor region of this component for both render and mouse events.
	 * @param region the component's render region.
	 * @return the region to scissor. null does not apply a new scissor.
	 * @apiNote this is applied between the <i>paintBackground</i> and <i>paint</i> stages.
	 */
	public @Nullable Region getScissorRegion(Region region) {
		return null;
	}

	/**
	 * Compute the dimensions of the largest total width and largest total height, when the sizes, margins, and padding
	 * of each child is summed up.
	 * @param children the children to compute for.
	 * @param childDimensionGetter the function to get the dimensions for this calculation.
	 * @return a Dimensions object containing the largest total width and largest total height, including margins and padding of the children.
	 */
	private static Dimensions largestSizeWithMargins(List<? extends SizedElement> children, Margins padding, Function<SizedElement, Dimensions> childDimensionGetter) {
		// calculate the largest total width of all children
		// assume the top left corner of all (with margins and padding added) is top-left corner
		int width = 0;
		int height = 0;

		for (SizedElement child : children) {
			Dimensions dimensions = childDimensionGetter.apply(child);

			// check if we need to expand the region for the new widget
			// width
			int childTotalWidth = dimensions.getWidth() + child.getMargins().horizontal();

			if (childTotalWidth > width) {
				width = childTotalWidth;
			}

			// height
			int childTotalHeight = dimensions.getHeight() + child.getMargins().vertical();

			if (childTotalHeight > height) {
				height = childTotalHeight;
			}
		}

		// add own padding
		width += padding.horizontal();
		height += padding.vertical();

		return new Dimensions(width, height);
	}
}
