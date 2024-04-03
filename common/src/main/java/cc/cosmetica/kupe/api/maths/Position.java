package cc.cosmetica.kupe.api.maths;

/**
 * Represents a position on the screen.
 */
public class Position {
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public final int x;
	public final int y;

	/**
	 * Get a new position with the coordinates of the two positions added together.
	 * @param other the other position.
	 * @return the newly created position.
	 */
	public Position add(Position other) {
		return new Position(this.x + other.x, this.y + other.y);
	}

	/**
	 * A constant for the 0, 0 position.
	 */
	public static final Position ZERO = new Position(0, 0);

	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}
}
