package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.gui.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class ComponentTree {
	public ComponentTree(Component<?> root) {
		this.root = new ComponentNode(root);
	}

	private final ComponentNode root;

	/**
	 * Recursively build the component tree from the root.
	 */
	public void buildAll() {
		Deque<ComponentNode> nodes = new ArrayDeque<>();
		nodes.add(this.root);

		while (!nodes.isEmpty()) {
			ComponentNode node = nodes.remove();
			node.buildOnce();
			nodes.addAll(node.children);
		}
	}

	private static class ComponentNode {
		ComponentNode(Component<?> element) {
			this.element = element;
		}

		final Component<?> element;
		final List<ComponentNode> children = new ArrayList<>();

		/**
		 * Build just this node. Does not recursively build children.
		 */
		private void buildOnce() {
			this.children.clear();

			for (Component<?> component : this.element.build()) {
				this.children.add(new ComponentNode(component));
			}
		}
	}
}
