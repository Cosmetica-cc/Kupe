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

	/**
	 * Close the current screen.
	 */
	public static void closeCurrentScreen() {
		Minecraft.getInstance().screen.onClose();
	}

	/**
	 * Enables debug on all Kupe screens.
	 */
	public static void enableDebug() {
		KupeScreen.enableDebug();
	}
}
