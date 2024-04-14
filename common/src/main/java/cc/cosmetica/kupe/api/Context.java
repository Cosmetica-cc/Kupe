package cc.cosmetica.kupe.api;

/**
 * Provides access to information about the context in which we are drawing. Used to query information, but not draw.
 */
public interface Context {
	/**
	 * Get the width of the given text.
	 * @param text the text to compute the width of.
	 * @return the width, in pixels, of the given text.
	 */
	int getWidth(Text text);

	/**
	 * Get the line height of text.
	 * @return the line height of text.
	 */
	int getLineHeight();

	/**
	 * Get the height the given text would be if it can be at maximum the provided width.
	 * @param text the text.
	 * @param maxWidth the maximum width.
	 * @return the height, in pixels, this text would take up.
	 */
	int getTextHeight(Text text, int maxWidth);

	/**
	 * Get the window width.
	 * @return the width of the window.
	 */
	int getViewWidth();

	/**
	 * Get the window height.
	 * @return the height of the window.
	 */
	int getViewHeight();
}
