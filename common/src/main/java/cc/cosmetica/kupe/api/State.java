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
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.impl.StateManagerImpl;

/**
 * Reactive state.
 * To do something to the effect of useState() in react, create a new State<T> instance in the constructor of your component,
 * and acquire it in your component's {@linkplain Component#build() build} method.
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
		// register component as a listener for state changes
		// If the component is destroyed, we also want to remove it from our list of listeners. This is handled in the tree.
		StateManagerImpl.acquireState(this, component);
		return this.value;
	}

	public void set(T value) {
		this.value = value;

		// rebuild listeners
		StateManagerImpl.scheduleRebuild(this);
	}
}
