package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.util.BiMultiMap;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.*;
import java.util.function.Function;

/**
 * Faster than using reflection!
 */
public final class StateManagerImpl {
	private static final BiMultiMap<Component, State<?>> ACQUIRED = new BiMultiMap<>();
	private static final Map<Component, Map<State<?>, Config>> EXTRACTIONS = new HashMap<>();

	private static class Config {
		List<Follower> followers = new ArrayList<>();
		boolean globalCapture = false; // enabled if the whole state is captured, so should be updated every time.
	}

	/**
	 * Clear states acquired by the given component. Is called when a component is disposed.
	 * @param component the component for which to clear states acquired.
	 */
	static void clearStates(Component component) {
		// clear states acquired by this component
		ACQUIRED.remove(component);
		EXTRACTIONS.remove(component);
	}

	/**
	 * Clear only the extractions acquired by this component. Called upon rebuilding a component.
	 * @param component used to re-generate Followers.
	 */
	static void clearConfig(Component component) {
		// clear only extractions for this component.
		// used for rebuilding a component when it doesn't need to be disposed.
		EXTRACTIONS.remove(component);
	}

	public static void acquireState(State<?> state, Component component) {
		ACQUIRED.put(component, state);

		// configure global in config
		EXTRACTIONS
				.computeIfAbsent(component, x -> new HashMap<>())
				.computeIfAbsent(state, x -> new Config()).globalCapture = true;
	}

	public static <T, E> E extractState(State<T> state, Component component, Function<T, E> mapping) {
		ACQUIRED.put(component, state);

		List<Follower> followers = EXTRACTIONS
				.computeIfAbsent(component, x -> new HashMap<>())
				.computeIfAbsent(state, x -> new Config()).followers;

		// the same follower doesn't need to be reused on rebuilds as we reset extractions upon rebuild.
		Follower<T, E> follower = new Follower<>(mapping);

		followers.add(follower);

		follower.update(state.peek());
		return follower.get();
	}

	public static void scheduleResize() {
		RenderSystem.recordRenderCall(() -> {
			Screen screen = Minecraft.getInstance().screen;

			if (screen instanceof KupeScreen) {
				((KupeScreen) screen).resize();
			}
		});
	}

	/**
	 * Schedule rebuild for the given state updating.
	 */
	public static void scheduleRebuild(State<?> state) {
		Iterable<Component> allComponents = ACQUIRED.getReverse(state);

		// filter
		Collection<Component> rebuildComponents = new ArrayList<>();

		for (Component component : allComponents) {
			if (shouldRebuild(state, component)) {
				rebuildComponents.add(component);
			}
		}

		// rebuild
		RenderSystem.recordRenderCall(() -> {
			Screen screen = Minecraft.getInstance().screen;

			if (screen instanceof KupeScreen) {
				((KupeScreen) screen).rebuildComponents(rebuildComponents);
			}
		});
	}

	/**
	 * Determine if a given component should be rebuilt given a state update ("re-rendered" for react fans).
	 * @param state the state that updated.
	 * @param component the component that needs determining whether it should be rebuilt.
	 * @return whether the component should be rebuilt.
	 */
	private static boolean shouldRebuild(State<?> state, Component component) {
		Config config = EXTRACTIONS.get(component).get(state);

		// n.b. if this method returns true, not all followers have necessarily updated
		// but that's okay, because a rebuilding component resets abstractions and creates the objects again.
		if (config.globalCapture) {
			return true;
		}

		for (Follower follower : config.followers) {
			if (follower.update(state.peek())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 'Follows' a state.
	 */
	private static class Follower<T, E> {
		private Follower(Function<T, E> function) {
			this.function = function;
		}

		private final Function<T, E> function;
		private E value;

		@SuppressWarnings("unchecked")
		private boolean update(Object newState) {
			E newValue = this.function.apply((T) newState);

			if (!Objects.equals(newValue, this.value)) {
				this.value = newValue;
				return true;
			} else {
				return false;
			}
		}

		private E get() {
			return this.value;
		}
	}
}
