package cc.cosmetica.kupe.api.gui;

/**
 * The alignment inside a Div along the secondary axis.
 */
public enum Align {
	/**
	 * Align elements to the start of the Div.
	 */
	START,
	/**
	 * Align elements to the centre of the Div.
	 */
	CENTRE,
	/**
	 * Align elements to the end of the Div.
	 */
	END,
	/**
	 * Align elements to stretch to either side of the div. If space is remaining due to constraints, aligns elements
	 * to the start.
	 */
	STRETCH_START,
	/**
	 * Align elements to stretch to either side of the div. If space is remaining due to constraints, aligns elements
	 * to the centre.
	 */
	STRETCH_CENTRE,
	/**
	 * Align elements to stretch to either side of the div. If space is remaining due to constraints, aligns elements
	 * to the end.
	 */
	STRETCH_END
}
