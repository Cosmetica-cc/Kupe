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

package cc.cosmetica.kupe.api.gui;

/**
 * The alignment inside a Div for Justification of contents (primary axis). This determines how extra space will be
 * distributed.
 */
public enum Justify {
	/**
	 * Justify content to the start of the Div.
	 */
	START,
	/**
	 * Justify content to the centre of the Div.
	 */
	CENTRE,
	/**
	 * Justify content to the end of the Div.
	 */
	END,
	/**
	 * Makes extra space fill the gaps between component.
	 */
	SPACE_BETWEEN,
	/**
	 * Places extra space evenly around each component.
	 */
	SPACE_AROUND
}
