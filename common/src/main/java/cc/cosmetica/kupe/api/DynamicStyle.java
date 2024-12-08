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

import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.impl.StateManagerImpl;

/**
 * Dynamic Style. This is similar to using a pure {@link State}&lt;{@link cc.cosmetica.kupe.api.gui.style.Style}&gt;,
 * but will not rebuild the component when the stylesheet changes. Instead, it will just trigger a resize.
 * Use this when you want to modify the sizes of components at runtime.
 // FIXME isn't style computed during build phase?
 */
@Deprecated
public class DynamicStyle extends State<Style> {
	public DynamicStyle(Style initialValue) {
		super(initialValue);
	}

	@Override
	public void set(Style value) {
		this.value = value;

		// resize root
		StateManagerImpl.scheduleResize();
	}
}
