package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.util.BiMultiMap;

/**
 * Faster than using reflection!
 */
public final class StateManagerImpl {
	private static BiMultiMap<Component, State<?>> STATES = new BiMultiMap<>();

	static void clearStates(Component component) {
		// clear states acquired by this component
		STATES.remove(component);
	}

	public static void acquireState(State<?> state, Component component) {
		STATES.put(component, state);
	}
}
