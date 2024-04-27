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

import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;

import java.util.Optional;
import java.util.OptionalInt;


public final class CommonProperties {
	private CommonProperties() {
		// No Instantiation
	}

	/**
	 * The margins of the component. These are the boundaries around the component.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the margins of this component.
	 */
	public static final Style.Property<DimensionsOperator<Margins>> MARGINS = new Style.Property<>((vw, vh) -> Margins.NONE);

	/**
	 * The padding of the component. These are the boundaries within this component.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the padding of this component.
	 */
	public static final Style.Property<DimensionsOperator<Margins>> PADDING = new Style.Property<>((vw, vh) -> Margins.NONE);

	/**
	 * The maximum size of the component. This does not include padding or margins.
	 */
	public static final Style.Property<DimensionsOperator<Dimensions>> MAXIMUM_SIZE = new Style.Property<>((vw, vh) -> Dimensions.MAX);

	/**
	 * The fixed width of the component. This is bounded by the {@linkplain CommonProperties#MAXIMUM_SIZE maximum} and
	 * {@linkplain CommonProperties#MINIMUM_SIZE minimum} sizes.
	 * This may still resize due to flex attributes.
	 */
	public static final Style.Property<DimensionsOperator<OptionalInt>> WIDTH = new Style.Property<>((vw, vh) -> OptionalInt.empty());

	/**
	 * The fixed height of the component. This is bounded by the {@linkplain CommonProperties#MAXIMUM_SIZE maximum} and
	 * {@linkplain CommonProperties#MINIMUM_SIZE minimum} sizes.
	 * This may still resize due to flex attributes.
	 */
	public static final Style.Property<DimensionsOperator<OptionalInt>> HEIGHT = new Style.Property<>((vw, vh) -> OptionalInt.empty());

	/**
	 * The minimum size of the component. This does not include padding or margins.
	 * The actual minimum size of the component is the maximum, per-axis, of this and the minimum size due to its
	 * children.
	 */
	public static final Style.Property<DimensionsOperator<Optional<Dimensions>>> MINIMUM_SIZE = new Style.Property<>((vw, vh) -> Optional.empty());

	/**
	 * Get the amount this object flexes in divisions. That is, when objects are being made to fill the space of the
	 * division, allocate this proportion to this component. If 0, this component will not flex.
	 */
	public static final Style.Property<Integer> FLEX = new Style.Property<>(0);

	/**
	 * Get the amount this object flexes in divisions when shrinking. That is, when objects are being shrunk to fit
	 * the space of the division, shrink this component proportionally. If 0, this component will not shrink.
	 */
	public static final Style.Property<Integer> FLEX_SHRINK = new Style.Property<>(1);

	/**
	 * Override how this component aligns in the hierarchy.
	 */
	public static final Style.Property<Optional<Align>> ALIGN_SELF = new Style.Property<>(Optional.empty());

	// Background and Border
	// ======================

	/**
	 * The background colour for this component.
	 */
	public static final Style.Property<OptionalInt> BACKGROUND_COLOUR = new Style.Property<>(OptionalInt.empty());

	/**
	 * The border colour for this component.
	 */
	public static final Style.Property<Integer> BORDER_COLOUR = new Style.Property<>(0xFFFFFF);

	/**
	 * Get the border size for this component. The border is placed at the edge of the padding, within the padding.
	 */
	public static final Style.Property<Integer> BORDER_SIZE = new Style.Property<>( 0);

	public interface DimensionsOperator<T> {
		/**
		 * Apply the given screen dimensions to get the value.
		 * @param vw the screen width.
		 * @param vh the screen height.
		 * @return the value provided from the given dimensions.
		 */
		T apply(int vw, int vh);
	}
}
