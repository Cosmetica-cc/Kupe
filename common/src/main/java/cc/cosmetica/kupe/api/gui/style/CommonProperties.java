package cc.cosmetica.kupe.api.gui.style;

import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;

import java.util.Optional;


public final class CommonProperties {
	private CommonProperties() {
		// No Instantiation
	}

	/**
	 * Get the margins of this component. These are the boundaries around the component.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the margins of this component.
	 */
	public static final Style.Property<DimensionsOperator<Margins>> MARGINS = new Style.Property<>((vw, vh) -> Margins.NONE);

	/**
	 * Get the padding of this component. These are the boundaries within this component.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the padding of this component.
	 */
	public static final Style.Property<DimensionsOperator<Margins>> PADDING = new Style.Property<>((vw, vh) -> Margins.NONE);

	/**
	 * Get the maximum size of this component. This does not include padding or margins.
	 * TODO If this is not provided, but the preferred size is, will default to the preferred size.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the maximum size of this component.
	 */
	public static final Style.Property<Optional<Dimensions>> MAXIMUM_SIZE = new Style.Property<>(Optional.empty());

	/**
	 * Get the preferred size of this component. This does not include padding or margins.
	 * This overrides the preferred size due to the component's children.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the preferred size of this component.
	 */
	public static final Style.Property<Optional<Dimensions>> PREFERRED_SIZE = new Style.Property<>(Optional.empty());

	/**
	 * Get the minimum size of this component. This does not include padding or margins.
	 * The actual minimum size of the component is the maximum, per-axis, of this and the minimum size due to its
	 * children.
	 * @param vw the screen width.
	 * @param vh the screen height.
	 * @return the minimum size of this component.
	 */
	public static final Style.Property<Optional<Dimensions>> MINIMUM_SIZE = new Style.Property<>(Optional.empty());

	/**
	 * Whether to preserve the shape (ratio of x and y) of this component, when resizing.
	 * @return whether the shape of this component should be preserved.
	 */
	public static final Style.Property<Boolean> PRESERVE_SHAPE = new Style.Property<>(false);

	/**
	 * Get the amount this object flexes in collections. That is, when objects are being made to fill the space of the
	 * collection, allocate this proportion to this component.
 	 * @return the amount this object flexes in collections. If 0, this component will not flex.
	 */
	public static final Style.Property<Integer> FLEX = new Style.Property<>(0);

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
