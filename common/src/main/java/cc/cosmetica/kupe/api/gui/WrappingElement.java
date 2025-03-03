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

import cc.cosmetica.kupe.api.Context;

/**
 * An element that wraps content vertically when constrained horizontally.
 * You should still try to allocate the right size initially, accounting for wrapping, in your base component sizing
 * methods.
 * @implNote These are considered in an additional step during element sizing.
 */
public interface WrappingElement {
	/**
	 * Get the real height of this component, given the width.
	 * @param width the width.
	 * @param context the context.
	 * @return the real height of this component.
	 */
	int realHeight(int width, Context context);
}
