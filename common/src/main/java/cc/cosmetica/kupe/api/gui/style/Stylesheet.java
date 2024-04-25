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

package cc.cosmetica.kupe.api.gui.style;

import cc.cosmetica.kupe.api.gui.Component;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <i>30 years late to the party.</i><br>
 * A style sheet. That is, a collection of style overrides to use for this component and its children.
 * The actual style used for a given component will also inherit properties from parent pages, and fill in defaults.
 */
public class Stylesheet {
	public Stylesheet() {
		this.classStyles = new HashMap<>();
	}

	private final Map<Class<? extends Component>, Style> classStyles;
	private Style self;

	/**
	 * Used internally when flattening styles. Adds relevant styles from this stylesheet to the provided list.
	 * @param styles the list of style overrides, in order of most important to least.
	 * @param component the component class to add styles for.
	 * @param self whether to add self overrides.
	 */
	public void fillOverrides(List<Style> styles, Class<? extends Component> component, boolean self) {
		if (self && this.self != null) {
			styles.add(this.self);
		}

		Style componentStyle = this.classStyles.get(component);

		if (componentStyle != null) {
			styles.add(componentStyle);
		}
	}

	/**
	 * Apply the given style overrides to components of the given class.
	 * @param componentClass the class of components to apply the given style overrides to. This will match *exactly* this
	 *                       class, and not subclasses.
	 * @param style the style overrides.
	 * @return this style sheet.
	 * @throws IllegalArgumentException if the style overrides for the given component class have already been set.
	 */
	public Stylesheet component(Class<? extends Component> componentClass, Style style) {
		if (this.classStyles.containsKey(componentClass)) {
			throw new IllegalArgumentException("Cannot set style overrides for same class (" + componentClass + ") twice.");
		}

		this.classStyles.put(componentClass, style);
		return this;
	}

	/**
	 * Apply the given style overrides to the component.
	 * @param style the style overrides.
	 * @return this style sheet.
	 */
	public Stylesheet self(Style style) {
		this.self = style;
		return this;
	}
}
