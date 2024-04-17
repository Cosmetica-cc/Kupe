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
 * Represents the boundaries of something. Could be Margins or Padding.
 */
public class Margins {
	public Margins(int margin) {
		this(margin, margin, margin, margin);
	}

	public Margins(int vertical, int horizontal) {
		this(vertical, horizontal, vertical, horizontal);
	}

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

	public int horizontal() {
		return left + right;
	}

	public int vertical() {
		return top + bottom;
	}

	@Override
	public String toString() {
		return "Margins(" + this.top + ", " + this.right + ", " + this.bottom + ", " + this.left + ")";
	}

	public static final Margins NONE = new Margins(0, 0, 0, 0);
}
