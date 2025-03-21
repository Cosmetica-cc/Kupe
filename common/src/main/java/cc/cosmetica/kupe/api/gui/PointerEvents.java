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
 * How components can receive pointer events.
 */
public enum PointerEvents {
	/**
	 * Skip this component. Make it 'transparent' to pointers.
	 */
	NONE,
	/**
	 * Default behaviour. Receive events only on this component's region, when visible.
	 */
	VISIBLE,
	/**
	 * All pointer events on this component's region, even when occluded.
	 */
	REGION,
	/**
	 * All pointer events, always.
	 */
	ALL
}
