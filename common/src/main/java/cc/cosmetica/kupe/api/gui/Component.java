package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.maths.Position;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.impl.MathsImpl;
import net.minecraft.util.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A GUI component. Whenever relevant states are changed, the tree of components is updated via calling build().
 *
 * All nodes in the GUI tree are components, from the base to the top. Base nodes return an empty list for build().
 */
public abstract class Component<T extends Stylesheet> {
	protected Component(T defaultStylesheet) {
		this.stylesheet = defaultStylesheet;
	}

	protected T stylesheet;

	/**
	 * The map of absolute positions.
	 * If this is being used to place children, clear it and put components in the map, then call the base resize().
	 */
	protected Map<Component<?>, Position> absolutePositions = new HashMap<>();

	/**
	 * Set the style sheet of this component.
	 * @param stylesheet the stylesheet to apply to this component.
	 * @return this component.
	 */
	public Component<T> withStyle(T stylesheet) {
		this.stylesheet = stylesheet;
		return this;
	}

	/**
	 * Calculate the minimum size given the component's children. This should be consistent with how the component
	 * positions its children.
	 * @param children a list of the children of this component and their minimum sizes.
	 */
	public Dimensions minimumSize(List<Tuple<Component<?>, Dimensions>> children) {
		if (children.isEmpty()) { // leaf components
			return Dimensions.NONE;
		}

		if (children.size() == 1) {
			return children.get(0).getB();
		}

		return MathsImpl.calculateSizeAbsolute(children, this.absolutePositions);
	}

	/**
	 * Calculate the preferred size of this component given the component's children. This should be consistent with how
	 * the component positions its children.
	 * @param children a list of the children of this component and their preferred sizes.
	 */
	public Dimensions preferredSize(List<Tuple<Component<?>, Dimensions>> children) {
		if (children.isEmpty()) { // leaf components
			return Dimensions.NONE;
		}

		if (children.size() == 1) {
			return children.get(0).getB();
		}

		return MathsImpl.calculateSizeAbsolute(children, this.absolutePositions);
	}

	/**
	 * Get a list of children of this component.
	 * Will be called again if states acquired by this component or a parent component change.
	 * @return the list of children of this component
	 */
	public abstract List<Component<?>> build();

	public void resize(Region region, List<Component<?>> children) {
		// By default, lay out children in specified positions.

	}

	/**
	 * Render this component. This does not have to handle rendering children.
	 * @param canvas the canvas for drawing on the screen.
	 * @param region the region of the screen this component has been placed in.
	 */
	public void render(Canvas canvas, Region region) {
		// By default, do nothing
	}
}
