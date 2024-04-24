package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.util.BiMultiMap;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * Faster than using reflection!
 */
public final class StateManagerImpl {
	private static @Nullable Component building;
	private static int stateIndex;
	private static BiMultiMap<Component, State<?>> STATES = new BiMultiMap<>();

	static void setBuildingComponent(@Nullable Component component) {
		building = component;
		stateIndex = 0; // reset state index
	}

	static void clearStates(Component component) {
		// clear states acquired by this component
		STATES.remove(component);
	}

	public static void acquireState(State<?> state, Component component) {
		if (!STATES.getReverse(state).contains(component)) {
			STATES.put(component, state);
		}
	}

	/**
	 * Fetch, or create and acquire, the next state for the currently building component.
	 * @param defaultValue the default value of the state.
	 * @param constructor the constructor for a new state.
	 * @param resizeOnly whether it should hook on resize only.
	 * @return the fetched or created state (which is guaranteed to be acquired by the building component).
	 */
	public static <T, S extends State<T>> S fetchAndAcquireState(T defaultValue, Function<T, S> constructor, boolean resizeOnly) {
		if (building == null) {
			throw new IllegalStateException("Can only useState when building a component!");
		}

		// check if a state already exists
		List<State<?>> existing = STATES.get(building);

		S state;

		// does it already exist?
		if (stateIndex < existing.size()) {
			// get existing state
			state = (S) existing.get(stateIndex++);
		} else {
			// create new state
			state = constructor.apply(defaultValue);
			state.acquire(building);
			stateIndex++;
		}

		return state;
	}
}
