package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.api.gui.Component;

/**
 * Reactive state.
 */
public class State<T> {
	public State(T initialValue) {
		this.value = initialValue;
	}

	T value;

	/**
	 * Peek at this state's value. This should be used outside of GUIs. Within GUIs, use {@link State#acquire}.
	 * @return the value stored in this state.
	 */
	public T peek() {
		return this.value;
	}

	public T acquire(Component component) {
		// TODO register component as a listener for state changes
		// If the component is destroyed, we also want to remove it from our list of listeners
		return this.value;
	}

	public void set(T value) {
		this.value = value;
		// TODO rebuild listeners
	}
}
