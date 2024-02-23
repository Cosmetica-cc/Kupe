package cc.cosmetica.kupe.api.maths;

/**
 * Represents an Axis in 2D.
 */
public enum Axis2D {
	/**
	 * Right
	 */
	POSITIVE_X(1, 0),
	/**
	 * Down
	 */
	POSITIVE_Y(0, 1),
	/**
	 * Left
	 */
	NEGATIVE_X(-1, 0),
	/**
	 * Up
	 */
	NEGATIVE_Y(0, -1);

	Axis2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	private final int x;
	private final int y;

	private int getX() {
		return this.x;
	}

	private int getY() {
		return this.y;
	}
}
