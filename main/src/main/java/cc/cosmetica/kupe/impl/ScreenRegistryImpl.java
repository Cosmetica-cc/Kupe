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

package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Implementation for {@link cc.cosmetica.kupe.api.Screens}.
 */
public class ScreenRegistryImpl {
	private static final Map<ResourceKey, ScreenEntry> REGISTRY = new HashMap<>();

	public static <T> void registerScreen(ResourceKey location, Supplier<Component> component) {
		if (REGISTRY.containsKey(location)) {
			throw new IllegalArgumentException("Screen already registered at " + location);
		}

		REGISTRY.put(location, new ScreenEntry(component));
	}

	public static GuiScreen getMinecraftScreen(ResourceKey location, GuiScreen parent) {
		if (!REGISTRY.containsKey(location)) {
			throw new IllegalArgumentException("No screen registered at the given location");
		}

		ScreenEntry entry = REGISTRY.get(location);

		return new KupeScreen(parent, entry.component.get(), entry.defaultBackground);
	}

	public static GuiScreen getMinecraftScreen(Component component, Text title, GuiScreen parent, boolean defaultBackground) {
		// title used on 1.16+ (for narrator?)

		return new KupeScreen(parent, component, defaultBackground);
	}

	public static void setDefaultBackground(ResourceKey id, boolean useDefault) {
		if (!REGISTRY.containsKey(id)) {
			throw new IllegalArgumentException("No screen registered at the given location");
		}

		// TODO more general configuration?
		REGISTRY.get(id).defaultBackground = useDefault;
	}

	public static void closeCurrentScreen() {
		// set parent screen
		if (Minecraft.getMinecraft().currentScreen instanceof KupeScreen) {
			((KupeScreen) Minecraft.getMinecraft().currentScreen).close();
		} else {
			// TODO set parent screen?
			Minecraft.getMinecraft().displayGuiScreen(null);
		}
	}

	private static class ScreenEntry {
		ScreenEntry(Supplier<Component> component) {
			this.component = component;
			this.defaultBackground = true;
		}

		final Supplier<Component> component;
		boolean defaultBackground;
	}
}
