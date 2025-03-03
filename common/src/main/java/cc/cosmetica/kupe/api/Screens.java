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

package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.impl.KupeScreen;
import cc.cosmetica.kupe.impl.LeavesSandbox;
import cc.cosmetica.kupe.impl.ScreenRegistryImpl;
import net.minecraft.client.Minecraft;

import java.util.function.Supplier;

/**
 * For managing screens.
 */
public final class Screens {
	/**
	 * Register a component as a new screen.
	 * @param id the location at which to register the screen.
	 * @param component the component to use as the root of the screen. It is recommended to make this extend
	 *                  {@link Screen}.
	 * @throws IllegalArgumentException if a screen is already registered at this location.
	 */
	public static void registerScreen(ResourceKey id, Component component) {
		ScreenRegistryImpl.registerScreen(id, () -> component);
	}

	/**
	 * Register a component as a new screen.
	 * @param id the location at which to register the screen.
	 * @param component a factory for the component to use as the root of the screen. It is recommended to make this
	 *                  extend {@link Screen}.
	 * @throws IllegalArgumentException if a screen is already registered at this location.
	 */
	public static void registerScreen(ResourceKey id, Supplier<Component> component) {
		ScreenRegistryImpl.registerScreen(id, component);
	}

	/**
	 * Set the current screen to the screen registered at the given location.
	 * @param id the resource key for the screen to use.
	 * @throws IllegalArgumentException if the provided location has no registered screen.
	 */
	public static void setScreen(ResourceKey id) {
		net.minecraft.client.gui.screens.Screen screen = ScreenRegistryImpl.getMinecraftScreen(id, Minecraft.getInstance().screen);
		Minecraft.getInstance().setScreen(screen);
	}

	/**
	 * Set the current screen to a Kupe component screen.
	 * @param component the root component for the screen.
	 * @param title the resource key from which to create the title text.
	 */
	public static void setScreen(Component component, ResourceKey title) {
		net.minecraft.client.gui.screens.Screen screen = ScreenRegistryImpl.getMinecraftScreen(component, title.translationKey("screens"), Minecraft.getInstance().screen, true);
		Minecraft.getInstance().setScreen(screen);
	}

	/**
	 * Set the current screen to a Kupe component screen.
	 * @param component the root component for the screen.
	 * @param title the screen title text.
	 */
	public static void setScreen(Component component, Text title) {
		net.minecraft.client.gui.screens.Screen screen = ScreenRegistryImpl.getMinecraftScreen(component, title, Minecraft.getInstance().screen, true);
		Minecraft.getInstance().setScreen(screen);
	}

	/**
	 * Get a new minecraft screen for the given Kupe screen resource location.
	 * @param location the resource key for the screen to use.
	 * @param parent the parent minecraft screen.
	 * @throws IllegalArgumentException if the provided location has no registered screen.
	 */
	@LeavesSandbox
	public static net.minecraft.client.gui.screens.Screen getMinecraftScreen(ResourceKey location, net.minecraft.client.gui.screens.Screen parent) {
		return ScreenRegistryImpl.getMinecraftScreen(location, parent);
	}

	@LeavesSandbox
	public static net.minecraft.client.gui.screens.Screen getMinecraftScreen(Component root, Text title, net.minecraft.client.gui.screens.Screen parent) {
		return ScreenRegistryImpl.getMinecraftScreen(root, title, parent, true);
	}

	/**
	 * Set whether the given screen should use the default background.
	 * @param id the resource key for the screen to configure.
	 * @param useDefault whether to use the default background.
	 * @throws IllegalArgumentException if the provided location has no registered screen.
	 */
	public static void setDefaultBackground(ResourceKey id, boolean useDefault) {
		ScreenRegistryImpl.setDefaultBackground(id, useDefault);
	}

	/**
	 * Close the current screen.
	 */
	public static void closeCurrentScreen() {
		ScreenRegistryImpl.closeCurrentScreen();
	}

	/**
	 * Controls the ability to enable debug inspect with Ctrl+Shift+I. This is on by default.
	 */
	public static void setAllowDebug(boolean allowDebug) {
		KupeScreen.setAllowDebug(allowDebug);
	}

	/**
	 * Enabled Kupe debug inspect (can also be triggered with Ctrl+Shift+I). Cannot be enabled while allowDebug is false.
	 */
	public static void setDebugInspect(boolean allowDebug) {
		KupeScreen.setDebug(allowDebug);
	}
}
