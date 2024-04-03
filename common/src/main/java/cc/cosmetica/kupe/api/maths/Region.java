package cc.cosmetica.kupe.api.maths;

/**
 * Represents a region of the screen.
 */
public class Region {
	/**
	 * Construct a new Region object from a position and dimensions.
	 * @param position the position of the top left corner of this region.
	 * @param dimensions the dimensions of this region.
	 */
	public Region(Position position, Dimensions dimensions) {
		this.x = position.x;
		this.y = position.y;
		this.width = dimensions.getWidth();
		this.height = dimensions.getHeight();
	}

	/**
	 * Construct a new Region object.
	 * @param x x of the top left corner of the region.
	 * @param y y of the top left corner of the region.
	 * @param width the width of this area.
	 * @param height the height of this area.
	 */
	public Region(int x, int y, int width, int height) {
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

	public int getEndX() {
		return this.x + this.width - 1;
	}

	public int getEndY() {
		return this.y + this.height - 1;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Get whether the given set of coordinates are in this region.
	 * This method assumes the width and height are provided as positive values.
	 * @param x the x coordinate to test.
	 * @param y the y coordinate to test.
	 * @return whether the coordinates are within this region.
	 */
	public boolean contains(int x, int y) {
		return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
	}

	@Override
	public String toString() {
		return "Region{" +
				"x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				'}';
	}

	public static Region withMargins(Region dimensions, Margins region) {
		// i am speed
		if (region == Margins.NONE) {
			return dimensions;
		}

		return new Region(
				dimensions.x - region.left,
				dimensions.y - region.top,
				dimensions.width + region.left + region.right,
				dimensions.height + region.top + region.bottom
		);
	}

	public static Region subtractPadding(Region region, Margins padding) {
		// i am speed
		if (padding == Margins.NONE) {
			return region;
		}

		int x = region.x + padding.left;
		int y = region.y + padding.top;
		int width = region.width + padding.left + padding.right;
		int height = region.height + padding.top + padding.bottom;

		if (width < 0) {
			x -= width/2;
			width = 0;
		}

		if (height < 0) {
			y -= height/2;
			height = 0;
		}

		return new Region(x, y, width, height);
	}
}
