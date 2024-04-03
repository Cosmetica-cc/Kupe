package cc.cosmetica.kupe.api.gui.style;

import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;

import java.util.List;

/**
 * The root stylesheet. This contains component-class specific overrides and is the lowest priority stylesheet.
 * This should be called by subclasses of components to set default overrides.
 */
public final class RootStylesheet {
	private RootStylesheet() {
		// no-op
	}

	/**
	 * Construct a component with the given default overrides in the root stylesheet.
	 * This is useful for something like {@linkplain Button buttons}, where you want the default size to be 200, 20.
	 * @param componentClass the class of component to override defaults for.
	 * @param defaultOverrides the default style overrides for this type of component.
	 * @throws IllegalArgumentException if the default style overrides for the given component class have already been configured.
	 */
	public static void setDefaultOverrides(Class<? extends Component> componentClass, Style defaultOverrides) throws IllegalArgumentException {
		STYLESHEET.component(componentClass, defaultOverrides);
	}

	/**
	 * Add the default overrides for the given class to the end of the list, if present.
	 * @param styles the list of styles.
	 * @param componentClass the component class to get default overrides for.
	 */
	public static void fillDefaultOverrides(List<Style> styles, Class<? extends Component> componentClass) {
		STYLESHEET.fillOverrides(styles, componentClass, false);
	}

	private static final Stylesheet STYLESHEET = new Stylesheet();
}
