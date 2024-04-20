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

package cc.cosmetica.kupe.api;

/**
 * A renderable element.
 */
@FunctionalInterface
public interface Renderable {
	/**
	 * Render starting at the given coordinates.
	 * @param canvas the canvas to draw onto.
	 * @param x the x coordinate to render from.
	 * @param y the y coordinate to render from.
	 */
	default void render(Canvas canvas, int x, int y) {
		this.render(canvas, x, y, 0xFFFFFF);
	}

	/**
	 * Render starting at the given coordinates.
	 * @param canvas the canvas to draw onto.
	 * @param x the x coordinate to render from.
	 * @param y the y coordinate to render from.
	 * @param colour the colour tint to draw with.
	 */
	void render(Canvas canvas, int x, int y, int colour);

	/**
	 * Get the width of this component, in pixels.
	 * @return the width of this component.
	 */
	default int width() {
		throw new UnsupportedOperationException(this.getClass() + " does not support width() operation.");
	}
}
