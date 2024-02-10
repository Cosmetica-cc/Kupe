package cc.cosmetica.kupe.api;

import net.minecraft.resources.ResourceLocation;

/**
 * Tool for drawing on the screen without needing to use Mojang code directly.
 */
public interface Canvas {
	/**
	 * Draw the text with the given colour at the given location on the screen.
	 * @param text the text to draw.
	 * @param x the x position at which to draw.
	 * @param y the y position at which to draw.
	 * @param colour the colour to use, as an RGB int.
	 */
	void drawText(Text text, int x, int y, int colour);

	/**
	 * Draw a rectangle filled with the given colour.
	 * @param x0 the x coordinate of the top left corner.
	 * @param y0 the y coordinate of the top left corner
	 * @param x1 the x coordinate of the bottom right corner
	 * @param y1 the y coordinate of the bottom right corner
	 * @param colour the colour to use, as an RGB int.
	 */
	void drawRect(int x0, int y0, int x1, int y1, int colour);

	/**
	 * Draw a rectangle filled with the given texture.
	 * @param x0 the x coordinate of the top left corner.
	 * @param y0 the y coordinate of the top left corner
	 * @param x1 the x coordinate of the bottom right corner
	 * @param y1 the y coordinate of the bottom right corner
	 * @param texture the location at which the texture can be found.
	 */
	void drawTexture(int x0, int y0, int x1, int y1, ResourceLocation texture);

	/**
	 * Start drawing quads.
	 *
	 * @param mode the format to draw vertices in. Each vertex will require these parameters.
	 */
	QuadBuilder drawQuads(QuadBuilder.Mode mode);
}
