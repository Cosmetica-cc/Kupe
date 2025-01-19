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
	 * Get a new position with the given coordinates added.
	 * @param dx the x to add.
	 * @param dy the y to add.
	 * @return the newly created position.
	 */
	public Position add(int dx, int dy) {
		return new Position(this.x + dx, this.y + dy);
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
