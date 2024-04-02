package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.impl.KupeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * For managing screens.
 */
public final class Screens {
	private static final Map<ResourceLocation, Component> REGISTRY = new HashMap<>();

	/**
	 * Register a component as a new screen.
	 * @param location the location at which to register the screen.
	 * @param component the component to use as the root of the screen.
	 * @throws IllegalArgumentException if a screen is already registered at this location.
	 */
	public static void registerScreen(ResourceLocation location, Component component) {
		if (REGISTRY.containsKey(location)) {
			throw new IllegalArgumentException("Screen already registered at " + location);
		}

		REGISTRY.put(location, component);
	}

	/**
	 * Set the current screen to the screen registered at the given location.
	 * @param location the resource key for the screen to use.
	 * @throws IllegalArgumentException if there is no screen at the given location.
	 */
	public static void setScreen(ResourceLocation location) {
		if (!REGISTRY.containsKey(location)) {
			throw new IllegalArgumentException("No screen at the given location");
		}

		Screen screen = new KupeScreen(location, REGISTRY.get(location));
		Minecraft.getInstance().setScreen(screen);
	}
}
