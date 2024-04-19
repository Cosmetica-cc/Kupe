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
	 * Draw a rectangle filled with the given colour.
	 * @param x0 the x coordinate of the top left corner.
	 * @param y0 the y coordinate of the top left corner
	 * @param x1 the x coordinate of the bottom right corner
	 * @param y1 the y coordinate of the bottom right corner
	 * @param r the red value to use for the colour.
	 * @param g the green value to use for the colour.
	 * @param b the blue value to use for the colour.
	 */
	void drawRect(int x0, int y0, int x1, int y1, float r, float g, float b);

	/**
	 * Draw a rectangle filled with the given texture.
	 * @param x0 the x coordinate of the top left corner.
	 * @param y0 the y coordinate of the top left corner
	 * @param x1 the x coordinate of the bottom right corner
	 * @param y1 the y coordinate of the bottom right corner
	 * @param z the z coordinate to draw the texture at. Write 0 if you do not need to use this parameter.
	 * @param texture the location at which the texture can be found.
	 */
	void drawTexture(int x0, int y0, int x1, int y1, float z, ResourceLocation texture);

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
