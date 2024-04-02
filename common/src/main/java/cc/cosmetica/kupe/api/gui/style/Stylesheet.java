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
	 * Used internally when flattening styles.
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
	 * @param componentClass the class of components to apply the given style overrides to.
	 * @param style the style overrides.
	 * @return this style sheet.
	 */
	public Stylesheet component(Class<? extends Component> componentClass, Style style) {
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
