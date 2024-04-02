package cc.cosmetica.kupe.api.gui.style;

import cc.cosmetica.kupe.api.gui.Component;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
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

	private final Map<Class<Component>, Style> classStyles;
	private Style self;

	/**
	 * Apply the given style overrides to components of the given class.
	 * @param componentClass the class of components to apply the given style overrides to.
	 * @param style the style overrides.
	 * @return this style sheet.
	 */
	public Stylesheet component(Class<Component> componentClass, Style style) {
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
