package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.maths.Region;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

class ComponentTree {
	public ComponentTree(Component<?> root) {
		this.root = new ComponentNode(root);
	}

	private final ComponentNode root;

	/**
	 * Build the component tree from the root.
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

	/**
	 * Resize all elements in the component tree.
	 * @param screenRegion the region to size the root component to.
	 */
	public void resizeAll(Region screenRegion) {
		Deque<ComponentNode> nodes = new ArrayDeque<>();
		nodes.add(this.root);

		// Calculate preferred sizes down the tree
		while (!nodes.isEmpty()) {
			ComponentNode node = nodes.remove();
			// TODO
		}

		nodes.add(this.root);

		// Resize down the tree
		while (!nodes.isEmpty()) { // nb we need to compute actual preferred sizes before resizing
			ComponentNode node = nodes.remove(); // nb we also, upon resizing, need to set the children's actual render regions
			node.element.resize(node.renderRegion, node.children.stream().map(nd -> nd.element).collect(Collectors.toList()));
			nodes.addAll(node.children);
		}
	}

	private static class ComponentNode {
		ComponentNode(Component<?> element) {
			this.element = element;
		}

		final Component<?> element;
		final List<ComponentNode> children = new ArrayList<>();
		Region renderRegion;

		/**
		 * Build just this node. Does not recursively build children.
		 */
		private void buildOnce() {
			this.children.clear();

			for (Component<?> component : this.element.build()) {
				this.children.add(new ComponentNode(component));
			}
		}

		/**
		 * Resize the component
		 */
		private void resize() {

		}
	}
}
