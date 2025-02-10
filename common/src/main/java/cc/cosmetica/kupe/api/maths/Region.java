/*
 * Copyright 2024 Cosmetica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

	/**
	 * Get the x of the final pixel in this region.
	 */
	public int getFinalX() {
		return this.x + this.width - 1;
	}

	/**
	 * Get the x of the edge of this region. This pixel is not included in the region.
	 */
	public int getEndX() {
		return this.x + this.width;
	}

	/**
	 * Get the y the final pixel in this region.
	 */
	public int getFinalY() {
		return this.y + this.height - 1;
	}

	/**
	 * Get the y of the edge of this region. This pixel is not included in the region.
	 */
	public int getEndY() {
		return this.y + this.height;
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

	public boolean overlaps(Region other) {
		// Check if one rectangle is to the left of the other
		if (this.getEndX() <= other.getX() || other.getEndX() <= this.getX()) {
			return false;
		}

		// Check if one rectangle is above the other
		if (this.getEndY() <= other.getY() || other.getEndY() <= this.getY()) {
			return false;
		}

		// If none of the above, the rectangles overlap
		return true;
	}

	public Region translate(int scrollX, int scrollY) {
		return new Region(
				this.x + scrollX,
				this.y + scrollY,
				this.width,
				this.height
		);
	}

	public Region shrinkMargins(Margins margins) {
		// i am speed
		if (margins == Margins.NONE) {
			return this;
		}

		return new Region(
				this.x + margins.left,
				this.y + margins.top,
				this.width - margins.left - margins.right,
				this.height - margins.top - margins.bottom
		);
	}

	/**
	 * Get a region representing this region with the provided margins expanded around the outside.
	 * The original region is guaranteed not to be modified.
	 * @param margins the margins to add.
	 * @return a region with expanded left, right, top, bottom as specified by the given margins.
	 */
	public Region addMargins(Margins margins) {
		// i am speed
		if (margins == Margins.NONE) {
			return this;
		}

		return new Region(
				this.x - margins.left,
				this.y - margins.top,
				this.width + margins.left + margins.right,
				this.height + margins.top + margins.bottom
		);
	}

	/**
	 * Shrink this region by the given amount on all sides. The original region is guaranteed to not be
	 * modified.
	 * @param by the amount, in pixels, to shrink the region by.
	 * @return a region representing the shrunk region.
	 */
	public Region shrink(int by) {
		// i am speed
		if (by == 0) {
			return this;
		}

		int x = this.x + by;
		int y = this.y + by;
		int width = this.width - by * 2;
		int height = this.height - by * 2;

		// ensure no negative width and height
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

	@Override
	public String toString() {
		return "Region{" +
				"x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				'}';
	}
}
