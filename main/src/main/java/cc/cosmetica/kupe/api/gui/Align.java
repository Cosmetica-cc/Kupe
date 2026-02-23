/*
 * Copyright 2024, 2025 Cosmetica
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
 * The alignment inside a Div along the secondary axis.
 */
public enum Align {
	/**
	 * Align elements to the start of the Div.
	 */
	START,
	/**
	 * Align elements to the centre of the Div.
	 */
	CENTRE,
	/**
	 * Align elements to the end of the Div.
	 */
	END,
	/**
	 * Align elements to stretch to either side of the div. If space is remaining due to constraints, aligns elements
	 * to the start.
	 */
	STRETCH_START,
	/**
	 * Align elements to stretch to either side of the div. If space is remaining due to constraints, aligns elements
	 * to the centre.
	 */
	STRETCH_CENTRE,
	/**
	 * Align elements to stretch to either side of the div. If space is remaining due to constraints, aligns elements
	 * to the end.
	 */
	STRETCH_END
}
