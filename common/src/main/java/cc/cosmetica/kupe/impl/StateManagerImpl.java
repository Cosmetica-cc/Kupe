package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.util.BiMultiMap;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/**
 * Faster than using reflection!
 */
public final class StateManagerImpl {
	private static BiMultiMap<Component, State<?>> ACQUIRED = new BiMultiMap<>();

	static void clearStates(Component component) {
		// clear states acquired by this component
		ACQUIRED.remove(component);
	}

	public static void acquireState(State<?> state, Component component) {
		ACQUIRED.put(component, state);
	}

	public static void scheduleResize() {
		RenderSystem.recordRenderCall(() -> {
			Screen screen = Minecraft.getInstance().screen;

			if (screen instanceof KupeScreen) {
				((KupeScreen) screen).resize();
			}
		});
	}

	public static void scheduleRebuild(State<?> state) {
		Iterable<Component> components = ACQUIRED.getReverse(state);

		RenderSystem.recordRenderCall(() -> {
			Screen screen = Minecraft.getInstance().screen;

			if (screen instanceof KupeScreen) {
				((KupeScreen) screen).rebuildComponents(components);
			}
		});
	}
}
