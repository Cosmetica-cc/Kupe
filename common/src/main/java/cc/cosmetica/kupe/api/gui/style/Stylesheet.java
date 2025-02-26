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
import com.google.common.collect.ImmutableList;
import org.apache.http.annotation.Immutable;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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
		this.tagStyles = new HashMap<>();
	}

	private final Map<Class<? extends Component>, Style> classStyles;
	private final Map<String, Style> tagStyles;
	private Style self;

	/**
	 * Used internally when flattening styles. Adds relevant styles from this stylesheet to the provided list.
	 * @param styles the list of style overrides, in order of most important to least.
	 * @param component the component class to add styles for.
	 * @param tags the tags to add styles for.
	 * @param self whether to add self overrides.
	 */
	public void fillOverrides(List<Style> styles, Class<? extends Component> component, Collection<String> tags, boolean self) {
		// overrides set for self are most important: goes first
		if (self && this.self != null) {
			styles.add(this.self);
		}

		// overrides set for tags are next most important
		for (String tag : tags) {
			Style tagStyle = this.tagStyles.get(tag);

			if (tagStyle != null) {
				styles.add(tagStyle);
			}
		}

		// lastly, overrides for the component class are added
		Style componentStyle = this.classStyles.get(component);

		if (componentStyle != null) {
			styles.add(componentStyle);
		}
	}

	/**
	 * Apply the given style overrides to components with the given tag.
	 * @param tag the tag components must have applied to inherit these style overrides.
	 * @param style the style overrides.
	 * @return this style sheet.
	 */
	public Stylesheet tag(String tag, Style style) {
		@Nullable Style existing = this.tagStyles.get(tag);

		if (existing != null) {
			this.tagStyles.put(tag, Style.merge(ImmutableList.of(style, existing)));
		} else {
			this.tagStyles.put(tag, style);
		}

		return this;
	}

	/**
	 * Apply the given style overrides to components with the specific tag.
	 * @param componentClass the class of components to apply the given style overrides to. This will match *exactly* this
	 *                       class, and not subclasses.
	 * @param style the style overrides.
	 * @return this style sheet.
	 */
	public Stylesheet component(Class<? extends Component> componentClass, Style style) {
		@Nullable Style existing = this.classStyles.get(componentClass);

		if (existing != null) {
			this.classStyles.put(componentClass, Style.merge(ImmutableList.of(style, existing)));
		} else {
			this.classStyles.put(componentClass, style);
		}

		return this;
	}

	/**
	 * Apply the given style overrides to the component.
	 * @param style the style overrides.
	 * @return this style sheet.
	 */
	public Stylesheet self(Style style) {
		if (this.self == null) {
			this.self = style;
		} else {
			this.self = Style.merge(ImmutableList.of(style, this.self));
		}

		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{\nself: ").append(this.self);

		this.classStyles.forEach((clazz, style) -> sb.append(",\n").append(clazz.getName()).append(":").append(style));

		sb.append("\n}");
		return sb.toString();
	}
}
