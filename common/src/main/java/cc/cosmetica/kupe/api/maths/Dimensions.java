package cc.cosmetica.kupe.api.maths;

/**
 * Represents a region of the screen.
 */
public class Dimensions {
	/**
	 * Construct a new Dimensions object.
	 * @param x x of the top left corner of the area.
	 * @param y y of the top left corner of the area.
	 * @param width the width of this area.
	 * @param height the height of this area.
	 */
	public Dimensions(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	private final int x;
	private final int y;
	private final int width;
	private final int height;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
