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

package cc.cosmetica.kupe.api.gui.style;

import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * The root stylesheet. This contains component-class specific overrides and is the lowest priority stylesheet.
 * This should be called by subclasses of components to set default overrides.
 */
public final class RootStylesheet {
	private RootStylesheet() {
		// no-op
	}

	/**
	 * Construct a component with the given default overrides in the root stylesheet.
	 * This is useful for something like {@linkplain Button buttons}, where you want the default size to be 200, 20.
	 * @param componentClass the class of component to override defaults for.
	 * @param defaultOverrides the default style overrides for this type of component.
	 * @throws IllegalArgumentException if the default style overrides for the given component class have already been configured.
	 */
	public static void setDefaultOverrides(Class<? extends Component> componentClass, Style defaultOverrides) throws IllegalArgumentException {
		STYLESHEET.component(componentClass, defaultOverrides);
	}

	/**
	 * Add the default overrides for the given class to the end of the list, if present.
	 * @param styles the list of styles.
	 * @param componentClass the component class to get default overrides for.
	 */
	public static void fillDefaultOverrides(List<Style> styles, Class<? extends Component> componentClass) {
		STYLESHEET.fillOverrides(styles, componentClass, ImmutableList.of(), false);
	}

	/**
	 * Get a debug string containing the data of the root stylesheet.
	 * @return a string containing info about the properties of the root stylesheet.
	 */
	public static String getDebugString() {
		return STYLESHEET.toString();
	}

	private static final Stylesheet STYLESHEET = new Stylesheet();
}
