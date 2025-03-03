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
import cc.cosmetica.kupe.api.maths.Region;

/**
 * Interface to get the calculated and cached preferred and minimum sizes from the component tree, and set the actual
 * render region of a component.
 */
public interface ResizableElement extends SizedElement {
	/**
	 * Set the render region (content region) of this resizable element.
	 * @param region the region on the screen at which to render this resizable element.
	 */
	void setRenderRegion(Region region);

	/**
	 * Get the new preferred height after shrinking width.
	 * Doesn't need to account for min/max.
	 * @param newWidth the new width.
	 * @param height the current height.
	 * @param context the rendering context.
	 * @return the new preferred height.
	 */
	int shrinkHeight(int newWidth, int height, Context context);

	/**
	 * Get the new preferred width after shrinking height
	 * @param newHeight the new height.
	 * @param width the current width.
	 * @param c
	 * @return the new preferred width.
	 */
	int shrinkWidth(int newHeight, int width, Context context);
}
