package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.gui.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Faster than using reflection!
 */
public final class StateManagerImpl {
	private static @Nullable Component building;

	static void setBuildingComponent(@Nullable Component component) {
		building = component;
	}

	static void clearStates(Component component) {
		// clear states acquired by this component
	}

	public static <T, S extends State<T>> S fetchAndAcquireState(T defaultValue, Function<T, State<T>> constructor, boolean resizeOnly) {
		if (building == null) {
			throw new IllegalStateException("Can only useState when building a component!");
		}

		// TODO create and acquire or fetch
	}
}
