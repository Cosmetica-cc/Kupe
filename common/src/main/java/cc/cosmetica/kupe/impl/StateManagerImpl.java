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
	private static final Map<Component, Map<State<?>, List<Follower>>> EXTRACTIONS = new HashMap<>();

	static void clearStates(Component component) {
		// clear states acquired by this component
		ACQUIRED.remove(component);
		EXTRACTIONS.remove(component);
	}

	public static void acquireState(State<?> state, Component component) {
		ACQUIRED.put(component, state);
	}

	public static <T, E> E extractState(State<T> state, Component component, Function<T, E> mapping) {
		ACQUIRED.put(component, state);

		List<Follower> followers = EXTRACTIONS
				.computeIfAbsent(component, x -> new HashMap<>())
				.computeIfAbsent(state, x -> new ArrayList<>());

		// TODO reuse the same follower :(
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

	public static void scheduleRebuild(State<?> state) {
		Iterable<Component> components = ACQUIRED.getReverse(state);

		RenderSystem.recordRenderCall(() -> {
			Screen screen = Minecraft.getInstance().screen;

			if (screen instanceof KupeScreen) {
				((KupeScreen) screen).rebuildComponents(components, );
			}
		});
	}

	private static boolean shouldRebuild(State<?> state, Component component) {
		for (Follower follower : EXTRACTIONS.get(component).get(state)) {
			if (follower.update(state.peek())) {
				return true;
			}
		}
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
