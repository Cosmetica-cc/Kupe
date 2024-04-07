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
	public static final Style.Property<DimensionsOperator<Optional<Dimensions>>> MAXIMUM_SIZE = new Style.Property<>((vw, vh) -> Optional.empty());

	/**
	 * The minimum size of the component. This does not include padding or margins.
	 * The actual minimum size of the component is the maximum, per-axis, of this and the minimum size due to its
	 * children.
	 */
	public static final Style.Property<DimensionsOperator<Optional<Dimensions>>> MINIMUM_SIZE = new Style.Property<>((vw, vh) -> Optional.empty());

	/**
	 * Whether to preserve the shape (ratio of x and y) of the component, when resizing.
	 */
	public static final Style.Property<Boolean> PRESERVE_SHAPE = new Style.Property<>(false);

	/**
	 * Get the amount this object flexes in divisions. That is, when objects are being made to fill the space of the
	 * division, allocate this proportion to this component. If 0, this component will not flex.
	 */
	public static final Style.Property<Integer> FLEX = new Style.Property<>(0);

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
	public static final Style.Property<Integer> BORDER_COLOUR = new Style.Property<>(0x000000);

	/**
	 * Get the border size for this component.
	 */
	public static final Style.Property<Integer> BORDER_SIZE = new Style.Property<>(0);

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
