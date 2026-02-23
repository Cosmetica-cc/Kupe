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

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection of properties to apply to a specific component, or group of components.
 */
public class Style {
	private Style(boolean inheritance) {
		this.inheritance = inheritance ? new Style(false) : null;
	}

	protected final Map<Property<?>, Object> properties = new HashMap<>();
	/**
	 * Properties that will be passed on to children.
	 */
	public final @Nullable Style inheritance;

	/**
	 * Get the value of a given property from this Style.
	 * @param property the property to fetch.
	 * @return the value for this property in this Style, or the default value if this Style does not contain that
	 * property.
	 * @param <T> the type contained within the property.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Property<T> property) {
		return (T) this.properties.getOrDefault(property, property.getDefaultValue());
	}

	@Override
	public String toString() {
		return this.properties.toString();
	}

	/**
	 * Create a new, {@linkplain MutableStyle mutable style}.
	 * @return the newly created style.
	 */
	public static MutableStyle create() {
		return new MutableStyle();
	}

	/**
	 * Merge the given list of styles together to make a flattened style.
	 * @param styles the list of styles, in descending level of importance.
	 * @return the new, flattened style object.
	 */
	public static Style merge(List<Style> styles) {
		Style result = new Style(true);
		assert result.inheritance != null; // shut up compiler warning

		for (int i = styles.size() - 1; i >= 0; i--) {
			result.properties.putAll(styles.get(i).properties);
            @Nullable Style inheritance = styles.get(i).inheritance;

			result.inheritance.properties.putAll(inheritance == null ? styles.get(i).properties : inheritance.properties);
		}

		return result;
	}

	public static class MutableStyle extends Style {
		protected MutableStyle() {
			// no-op; visibility change
			super(true);
		}

		/**
		 * Set the given property in this {@linkplain Style style}.
		 * @param property the property to set.
		 * @param value the value to give to the property.
		 * @return this mutable style object.
		 * @param <T> the type of data contained within the property.
		 */
		public <T> MutableStyle set(Property<T> property, T value) {
			this.properties.put(property, value);
			if (property.inherits) this.inheritance.properties.put(property, value);
			return this;
		}
	}

	/**
	 * A style property that can be configured.
	 */
	public static class Property<T> {
		public Property(String debugName, T defaultValue, boolean inherits) {
			this.debugName = debugName;
			this.defaultValue = defaultValue;
			this.inherits = inherits;
		}

		private final String debugName;
		private final T defaultValue;
		private final boolean inherits;

		public T getDefaultValue() {
			return this.defaultValue;
		}

		public boolean inherits() {
			return this.inherits;
		}

		@Override
		public String toString() {
			return this.debugName;
		}
	}
}
