package cc.cosmetica.kupe.api.gui;

/**
 * The alignment inside a Div for Justification of contents (primary axis). This determines how extra space will be
 * distributed.
 */
public enum Justify {
	/**
	 * Justify content to the start of the Div.
	 */
	START,
	/**
	 * Justify content to the centre of the Div.
	 */
	CENTRE,
	/**
	 * Justify content to the end of the Div.
	 */
	END,
	/**
	 * Makes extra space fill the gaps between component.
	 */
	SPACE_BETWEEN,
	/**
	 * Places extra space evenly around each component.
	 */
	SPACE_AROUND
}
