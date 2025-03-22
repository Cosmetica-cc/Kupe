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

import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Border;
import cc.cosmetica.kupe.api.gui.PointerEvents;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.impl.dim.*;
import cc.cosmetica.kupe.util.FloatBiFunction;
import cc.cosmetica.kupe.util.IntBiFunction;

import java.util.Optional;
import java.util.OptionalInt;


public final class CommonProperties {
	private CommonProperties() {
		// No Instantiation
	}

	public static final CommonProperties.DimensionsOperator<OptionalInt> NO_SIZE = (vw, vh, pw, ph) -> OptionalInt.empty();
	public static final CommonProperties.DimensionsOperator<Margins> NO_MARGINS = (vw, vh, pw, ph) -> Margins.NONE;
	public static final CommonProperties.DimensionsOperator<Optional<Dimensions>> NO_DIMENSIONS = (vw, vh, pw, ph) -> Optional.empty();

	/**
	 * The margins of the component. These are the boundaries around the component.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the margins of this component.
	 */
	public static final Style.Property<DimensionsOperator<Margins>> MARGINS = new Style.Property<>("margins", NO_MARGINS, false);

	/**
	 * The padding of the component. These are the boundaries within this component.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the padding of this component.
	 */
	public static final Style.Property<DimensionsOperator<Margins>> PADDING = new Style.Property<>("padding", NO_MARGINS, false);

	/**
	 * The maximum size of the component. This does not include margins, but includes padding.
	 */
	public static final Style.Property<DimensionsOperator<Dimensions>> MAXIMUM_SIZE = new Style.Property<>("maximumSize", fixed(Dimensions.MAX), false);

	/**
	 * The fixed width of the component. This is bounded by the {@linkplain CommonProperties#MAXIMUM_SIZE maximum} and
	 * {@linkplain CommonProperties#MIN_WIDTH minimum} sizes.
	 * This may still resize due to flex attributes.
	 */
	public static final Style.Property<DimensionsOperator<OptionalInt>> WIDTH = new Style.Property<>("width", NO_SIZE, false);

	/**
	 * The fixed height of the component. This is bounded by the {@linkplain CommonProperties#MAXIMUM_SIZE maximum} and
	 * {@linkplain CommonProperties#MIN_HEIGHT minimum} sizes.
	 * This may still resize due to flex attributes.
	 */
	public static final Style.Property<DimensionsOperator<OptionalInt>> HEIGHT = new Style.Property<>("height", NO_SIZE, false);

	/**
	 * Override the minimum size of the component. This does not include margins, but includes padding.
	 */
	public static final Style.Property<DimensionsOperator<OptionalInt>> MIN_WIDTH = new Style.Property<>("minWidth", NO_SIZE, false);
	/**
	 * Override the minimum size of the component. This does not include margins, but includes padding.
	 */
	public static final Style.Property<DimensionsOperator<OptionalInt>> MIN_HEIGHT = new Style.Property<>("minHeight", NO_SIZE, false);

	/**
	 * Controls the component draw order at this level. Lower numbers are drawn first (behind) other components.
	 */
	public static final Style.Property<Integer> Z_INDEX = new Style.Property<>("zIndex", 0, false);

	/**
	 * Controls when this component receives pointer events.
	 */
	public static final Style.Property<PointerEvents> POINTER_EVENTS = new Style.Property<>("pointerEvents", PointerEvents.VISIBLE, false);

	/**
	 * Get the amount this object flexes in divisions. That is, when objects are being made to fill the space of the
	 * division, allocate this proportion to this component. If 0, this component will not flex.
	 */
	public static final Style.Property<Integer> FLEX = new Style.Property<>("flex", 0, false);

	/**
	 * Get the amount this object flexes in divisions when shrinking. That is, when objects are being shrunk to fit
	 * the space of the division, shrink this component proportionally. If 0, this component will not shrink.
	 */
	public static final Style.Property<Integer> FLEX_SHRINK = new Style.Property<>("flexShrink", 1, false);

	/**
	 * Override how this component aligns in the hierarchy.
	 */
	public static final Style.Property<Optional<Align>> ALIGN_SELF = new Style.Property<>("alignSelf", Optional.empty(), false);

	// Background and Border
	// ======================

	/**
	 * The background colour for this component.
	 */
	public static final Style.Property<OptionalInt> BACKGROUND_COLOUR = new Style.Property<>("backgroundColour", OptionalInt.empty(), false); // but effectively does

	/**
	 * The component's border.
	 */
	public static final Style.Property<Optional<Border>> BORDER = new Style.Property<>("border", Optional.empty(), false);

	/**
	 * A tooltip to render when hovering over the component.
	 */
	public static final Style.Property<Optional<Tooltip>> TOOLTIP = new Style.Property<>("tooltip", Optional.empty(), false);

	// Utility Dimension Operator and Dimension Operator constructors for common use cases.

	public static final CommonProperties.DimensionsOperator<OptionalInt> SCREEN_WIDTH = new DimensionsOperator<OptionalInt>() {
		@Override
		public OptionalInt apply(int vw, int vh, int pw, int ph) {
			return OptionalInt.of(vw);
		}

		@Override
		public String toString() {
			return "100vw";
		}
	};
	public static final CommonProperties.DimensionsOperator<OptionalInt> SCREEN_HEIGHT = new DimensionsOperator<OptionalInt>() {
		@Override
		public OptionalInt apply(int vw, int vh, int pw, int ph) {
			return OptionalInt.of(vh);
		}

		@Override
		public String toString() {
			return "100vh";
		}
	};
	public static final CommonProperties.DimensionsOperator<OptionalInt> FULL_WIDTH = new DimensionsOperator<OptionalInt>() {
		@Override
		public OptionalInt apply(int vw, int vh, int pw, int ph) {
			return OptionalInt.of(pw);
		}

		@Override
		public String toString() {
			return "100% width";
		}
	};
	public static final CommonProperties.DimensionsOperator<OptionalInt> FULL_HEIGHT = new DimensionsOperator<OptionalInt>() {
		@Override
		public OptionalInt apply(int vw, int vh, int pw, int ph) {
			return OptionalInt.of(ph);
		}

		@Override
		public String toString() {
			return "100% height";
		}
	};

	/**
	 * Provide a fixed value independent of screen size.
	 * @param value the value to give to the property.
	 * @return a dimension operator for this fixed value.
	 * @param <T> the type of data contained within the property.
	 */
	public static <T> DimensionsOperator<T> fixed(T value) {
		return new FixedDimensions<>(value);
	}

	/**
	 * Provide a fixed value independent of screen size. A shorter version of the common idiom
	 * {@code fixed(OptionalInt.of(...))}
	 * @param value the value to give to the property.
	 * @return a dimension operator for this fixed value.
	 */
	public static DimensionsOperator<OptionalInt> fixedSize(int value) {
		return new FixedDimensions<>(OptionalInt.of(value));
	}

	/**
	 * Provide a percentage of the width and height.
	 * @param widthPercent the percentage (out of 100) of width.
	 * @param heightPercent the percentage (out of 100) of height.
	 * @return a dimension operator for w% + h%.
	 */
	public static DimensionsOperator<OptionalInt> percent(float widthPercent, float heightPercent) {
		return new PercentDimensions(widthPercent, heightPercent);
	}

	/**
	 * Provide an object from a percentage of the width and height.
	 * @param widthPercent the percentage (out of 100) of width.
	 * @param heightPercent the percentage (out of 100) of height.
	 * @param factory a factory that takes the given percentage of width and height (as ints) and produces an object.
	 * @return a dimension operator to create a generic object from w% and h%.
	 */
	public static <T> DimensionsOperator<T> percent(float widthPercent, float heightPercent, IntBiFunction<T> factory) {
		return new PercentGenericDimensions<>(widthPercent, heightPercent, factory);
	}

	/**
	 * Provide an object from a percentage of the width and height.
	 * @param widthPercent the percentage (out of 100) of width.
	 * @param heightPercent the percentage (out of 100) of height.
	 * @param factory a factory that takes the given percentage of width and height (as ints) and produces an object.
	 * @return a dimension operator to create a generic object from w% and h%.
	 */
	public static <T> DimensionsOperator<T> percent(float widthPercent, float heightPercent, FloatBiFunction<T> factory) {
		return new PercentGenericFloatDimensions<>(widthPercent, heightPercent, factory);
	}

	/**
	 * Provide a percentage of the screen width and height.
	 * @param widthPercent the percentage (out of 100) of width.
	 * @param heightPercent the percentage (out of 100) of height.
	 * @return a dimension operator for width vw + height vh.
	 */
	public static DimensionsOperator<OptionalInt> screen(float widthPercent, float heightPercent) {
		return new ScreenDimensions(widthPercent, heightPercent);
	}

	/**
	 * Provide an object from a percentage of the screen width and height.
	 * @param widthPercent the percentage (out of 100) of width.
	 * @param heightPercent the percentage (out of 100) of height.
	 * @return a dimension operator to create an object from (vw, vh).
	 */
	public static <T> DimensionsOperator<T> screen(float widthPercent, float heightPercent, IntBiFunction<T> factory) {
		return new ScreenGenericDimensions<>(widthPercent, heightPercent, factory);
	}

	public static final IntBiFunction<Margins> MARGINS_FACTORY = (w, h) -> new Margins(h, w);

	public interface DimensionsOperator<T> {
		/**
		 * Apply the given screen dimensions to get the value.
		 * @param vw the screen width.
		 * @param vh the screen height.
		 * @param pw the parent width. Provide 0 if unknown.
		 * @param ph the parent height. Provide 0 if unknown.
		 * @return the value provided from the given dimensions.
		 */
		T apply(int vw, int vh, int pw, int ph);
	}
}
