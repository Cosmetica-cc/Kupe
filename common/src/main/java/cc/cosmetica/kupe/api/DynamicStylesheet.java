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

import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.impl.StateManagerImpl;

/**
 * Dynamic Stylesheet. This is similar to using a pure {@link State}&lt;{@link cc.cosmetica.kupe.api.gui.style.Stylesheet Stylesheet}&gt;,
 * but will not rebuild the component when the stylesheet changes. Instead, it will just trigger a resize.
 * Use this when you want to modify the sizes of components at runtime.
 */
public class DynamicStylesheet extends State<Stylesheet> {
	public DynamicStylesheet(Stylesheet initialValue) {
		super(initialValue);
	}

	@Override
	public void set(Stylesheet value) {
		this.value = value;
		// TODO resize root
	}

	/**
	 * Fetch or create a dynamic stylesheet for the calling component. This must be called during build.
	 * <br/>
	 * This can be called multiple times per build, but must be called the same number of times and in the same order
	 * each iteration to prevent unpredictable behaviour. This includes mixing with {@linkplain State#useState(Object)}.
	 * @param initialValue the initial value upon state creation.
	 * @return the fetched state.
	 */
	public static DynamicStylesheet useStylesheet(Stylesheet initialValue) {
		return StateManagerImpl.fetchAndAcquireState(initialValue, DynamicStylesheet::new, true);
	}
}
