package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.gui.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation for {@link cc.cosmetica.kupe.api.Screens}.
 */
public class ScreenRegistryImpl {
	private static final Map<ResourceLocation, ScreenEntry> REGISTRY = new HashMap<>();

	public static void registerScreen(ResourceLocation location, Component component) {
		if (REGISTRY.containsKey(location)) {
			throw new IllegalArgumentException("Screen already registered at " + location);
		}

		REGISTRY.put(location, new ScreenEntry(component));
	}

	public static void setScreen(ResourceLocation location) {
		if (!REGISTRY.containsKey(location)) {
			throw new IllegalArgumentException("No screen at the given location");
		}

		ScreenEntry entry = REGISTRY.get(location);
		Screen screen = new KupeScreen(location, entry.component, entry.defaultBackground);
		Minecraft.getInstance().setScreen(screen);
	}

	public static void setDefaultBackground(ResourceLocation location, boolean useDefault) {
		if (!REGISTRY.containsKey(location)) {
			throw new IllegalArgumentException("No screen at the given location");
		}

		// TODO more general configuration?
		REGISTRY.get(location).defaultBackground = useDefault;
	}

	public static void closeCurrentScreen() {
		Minecraft.getInstance().screen.onClose();
	}

	private static class ScreenEntry {
		ScreenEntry(Component component) {
			this.component = component;
			this.defaultBackground = true;
		}

		final Component component;
		boolean defaultBackground;
	}
}
