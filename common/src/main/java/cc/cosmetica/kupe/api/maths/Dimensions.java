package cc.cosmetica.kupe.api.maths;

/**
 * Represents the size of a component.
 */
public class Dimensions {
	/**
	 * Construct a new Dimensions object.
	 * @param width the width of these dimensions.
	 * @param height the height of these dimensions.
	 */
	public Dimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	private final int width;
	private final int height;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return "Dimensions(" + this.width + "x" + this.height + ")";
	}

	/**
	 * A constant representing the dimensions (0, 0).
	 */
	public static Dimensions NONE = new Dimensions(0, 0);

	public static Dimensions max(Dimensions first, Dimensions second) {
		return new Dimensions(
				Math.max(first.width, second.width),
				Math.max(first.height, second.height)
		);
	}
}
