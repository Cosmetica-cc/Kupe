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

import cc.cosmetica.kupe.api.gui.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Implementation for {@link cc.cosmetica.kupe.api.Screens}.
 */
public class ScreenRegistryImpl {
	private static final Map<ResourceLocation, ScreenEntry> REGISTRY = new HashMap<>();

	public static void registerScreen(ResourceLocation location, Supplier<Component> component) {
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
		Screen screen = new KupeScreen(Minecraft.getInstance().screen, location, entry.component.get(), entry.defaultBackground);
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
		ScreenEntry(Supplier<Component> component) {
			this.component = component;
			this.defaultBackground = true;
		}

		final Supplier<Component> component;
		boolean defaultBackground;
	}
}
