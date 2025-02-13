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

package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

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

	public static Screen getMinecraftScreen(ResourceKey location, Screen parent) {
		if (!REGISTRY.containsKey(location)) {
			throw new IllegalArgumentException("No screen registered at the given location");
		}

		ScreenEntry entry = REGISTRY.get(location);

		return new KupeScreen(parent, location.translationKey("screens").toMinecraftComponent(), entry.component.get(), entry.defaultBackground);
	}

	public static Screen getMinecraftScreen(Component component, Text title, Screen parent, boolean defaultBackground) {
		return new KupeScreen(parent, title.toMinecraftComponent(), component, defaultBackground);
	}

	public static void setDefaultBackground(ResourceKey id, boolean useDefault) {
		if (!REGISTRY.containsKey(id)) {
			throw new IllegalArgumentException("No screen registered at the given location");
		}

		// TODO more general configuration?
		REGISTRY.get(id).defaultBackground = useDefault;
	}

	public static void closeCurrentScreen() {
		Minecraft.getInstance().screen.onClose();
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
