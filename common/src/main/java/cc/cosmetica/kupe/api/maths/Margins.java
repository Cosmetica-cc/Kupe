package cc.cosmetica.kupe.api.maths;

/**
 * Represents the boundaries of something. Could be Margins or Padding.
 */
public class Margins {
	public Margins(int top, int right, int bottom, int left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

	public final int top;
	public final int right;
	public final int bottom;
	public final int left;

	public static final Margins NONE = new Margins(0, 0, 0, 0);
}
