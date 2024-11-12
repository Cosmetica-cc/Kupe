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

package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.impl.LeavesSandbox;
import net.minecraft.resources.ResourceLocation;

/**
 * Tool for drawing on the screen without needing to use Mojang code directly.
 */
public interface Canvas {
	/**
	 * Get the drawing context. This allows you to make queries for various properties of the environment in which we
	 * are drawing.
	 * @return the drawing context.
	 */
	Context getDrawingContext();

	/**
	 * Get the matrix stack for manipulating pose.
	 * @return the matrix stack.
	 */
	MatrixStack getStack();

	/**
	 * Enable scissor for this component and its children. This restricts them to the bounds of this region.
	 * @apiNote Call this in renderBackground!
	 * @implNote If you call this in render() it will only affect this component's render.
	 */
	void useScissor(Region region);

	/**
	 * Scroll the view the given amount for this component and its children. This is not just equivalent to translating
	 * the matrix as it also adjusts mouse position in children and mouse position in child render().
	 * @apiNote Call this at the end of renderBackground!
	 * @param amountX the amount to scroll in X, in pixels.
	 * @param amountY the amount to scroll in Y, in pixels.
	 */
	void scroll(float amountX, float amountY);

	/**
	 * Set whether to use 'fast scissor'. This trims child branches that do not fall within a component's scissor.
	 * That is, said children will not get rendered. This is on by default.
	 * @param fastScissor whether to use fast scissor.
	 */
	void setFastScissor(boolean fastScissor);

	/**
	 * Disable transparent textures being drawn.
	 */
	void disableTransparency();

	/**
	 * Enable transparent textures being drawn, and use the given transparency.
	 * @param transparency the transparency to use, between 0 and 1.
	 */
	void setTransparency(float transparency);

	/**
	 * Draw the text with the given colour at the given location on the screen.
	 * @param text the text to draw.
	 * @param x the x position at which to draw.
	 * @param y the y position at which to draw.
	 * @param colour the colour to use, as an RGB int.
	 */
	void drawText(Text text, int x, int y, int colour);

	/**
	 * Draw the text with the given colour centered at the given location on the screen.
	 * @param text the text to draw.
	 * @param x the x position at which to draw.
	 * @param y the y position at which to draw.
	 * @param colour the colour to use, as an RGB int.
	 */
	void drawCenteredText(Text text, int x, int y, int colour);

	/**
	 * Fill the given colour value in the given region.
	 * @param region the region to draw in.
	 * @param colour the colour, as a packed RGB integer. For example, 0xFFFFFF.
	 */
	void drawRect(Region region, int colour);

	/**
	 * Draw a rectangle filled with the given colour.
	 * @param x0 the x coordinate of the top left corner.
	 * @param y0 the y coordinate of the top left corner
	 * @param width the width of the rectangle in pixels.
	 * @param height the height of the rectangle in pixels.
	 * @param z the z coordinate to draw the texture at.
	 * @param r the red value to use for the colour, on a scale of 0-1.
	 * @param g the green value to use for the colour, on a scale of 0-1.
	 * @param b the blue value to use for the colour, on a scale of 0-1.
	 */
	void drawRect(int x0, int y0, int width, int height, float z, float r, float g, float b);

	/**
	 * Draw a rectangle filled with the given texture.
	 * @param x0 the x coordinate of the top left corner.
	 * @param y0 the y coordinate of the top left corner
	 * @param width the width of the rectangle in pixels.
	 * @param height the height of the rectangle in pixels.
	 * @param z the z coordinate to draw the texture at. Write 0 if you do not need to use this parameter.
	 * @param texture the location at which the texture can be found.
	 */
	void drawTexture(int x0, int y0, int width, int height, float z, ResourceLocation texture);

	/**
	 * Start drawing quads.
	 *
	 * @param mode the format to draw vertices in. Each vertex will require these parameters.
	 */
	QuadBuilder drawQuads(QuadBuilder.Mode mode);

	/**
	 * Render the provided minecraft component.
	 *
	 * @param component the minecraft component to render.
	 * @param mouseX the x position of the mouse on the screen.
	 * @param mouseY the y position of the mouse on the screen.
	 */
	@LeavesSandbox
	void renderMinecraftComponent(net.minecraft.client.gui.components.Widget component, int mouseX, int mouseY);
}
