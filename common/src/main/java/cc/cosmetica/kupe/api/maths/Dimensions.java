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
		return this.width + "x" + this.height;
	}

	/**
	 * A constant representing the dimensions (0, 0).
	 */
	public static Dimensions NONE = new Dimensions(0, 0);

	/**
	 * A constant representing the dimensions (Integer.MAX_VALUE, Integer.MAX_VALUE).
	 */
	public static Dimensions MAX = new Dimensions(Integer.MAX_VALUE, Integer.MAX_VALUE);

	public static Dimensions max(Dimensions first, Dimensions second) {
		return new Dimensions(
				Math.max(first.width, second.width),
				Math.max(first.height, second.height)
		);
	}

	public static Dimensions clamp(Dimensions dimensions, Dimensions min, Dimensions max) {
		return new Dimensions(
				dimensions.width > max.width ? max.width : Math.max(dimensions.width, min.width),
				dimensions.height > max.height ? max.height : Math.max(dimensions.height, min.height)
		);
	}
}
