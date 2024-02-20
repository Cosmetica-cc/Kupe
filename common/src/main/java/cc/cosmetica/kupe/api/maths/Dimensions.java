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

	public static final Dimensions NONE = new Dimensions(0, 0, 0, 0);

	public static Dimensions withMargins(Dimensions dimensions, Margins margins) {
		// i am speed
		if (margins == Margins.NONE) {
			return dimensions;
		}

		return new Dimensions(
				dimensions.x - margins.left,
				dimensions.y - margins.top,
				dimensions.width + margins.left + margins.right,
				dimensions.height + margins.top + margins.bottom
		);
	}

	public static Dimensions subtractPadding(Dimensions dimensions, Margins padding) {
		// i am speed
		if (padding == Margins.NONE) {
			return dimensions;
		}

		int x = dimensions.x + padding.left;
		int y = dimensions.y + padding.top;
		int width = dimensions.width + padding.left + padding.right;
		int height = dimensions.height + padding.top + padding.bottom;

		if (width < 0) {
			x -= width/2;
			width = 0;
		}

		if (height < 0) {
			y -= height/2;
			height = 0;
		}

		return new Dimensions(x, y, width, height);
	}
}
