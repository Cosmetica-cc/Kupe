package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.ResizableElement;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Region;
import org.apache.commons.lang3.NotImplementedException;

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
		// DFS for preferred size calculation
		// We want the leaves to have preferred sizes calculated before their parents
		// because it depends on the children's preferred sizes being calculated

		Deque<ComponentNode> nodes = new ArrayDeque<>();
		nodes.push(this.root);

		// Calculate preferred sizes down the tree
		while (!nodes.isEmpty()) {
			ComponentNode node = nodes.pop();

			if (node.grey) {
				node.grey = false; // we are done with this node
				node.computeSizes();
			} else {
				node.grey = true; // we need to visit it one more time, after children are done
				nodes.push(node);

				// all children need to be visited before this node
				for (ComponentNode child : node.children) {
					nodes.push(child);
				}
			}
		}

		// BFS for resizing (down the tree)

		nodes.add(this.root);
		this.root.renderRegion = screenRegion;

		// Resize down the tree
		while (!nodes.isEmpty()) { // we have computed actual preferred sizes before resizing
			ComponentNode node = nodes.remove(); // nb we also, upon resizing, need to set the children's actual render regions
			node.resize();
			nodes.addAll(node.children);
		}
	}

	public void render() {

	}

	public void mouseMoved(double mouseX, double mouseY) {
		throw new NotImplementedException("Not yet implemented");
	}

	private static class ComponentNode implements ResizableElement {
		ComponentNode(Component<?> element) {
			this.element = element;
		}

		final Component<?> element;
		final List<ComponentNode> children = new ArrayList<>();
		Region renderRegion;
		Dimensions preferredSize, minimumSize; // calculated and cached
		boolean grey; // grey if visited in resizing stage for adding children
		              // but not for actual preferred size calculation

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
			this.element.resize(this.renderRegion, this.children);
		}

		private void computeSizes(int vw, int vh) {
			this.minimumSize = Dimensions.max(
					this.element.minimumSize(this.children),
					this.element.getStylesheet().minimumSize(vw, vh).orElse(Dimensions.NONE)
			);

			this.preferredSize = this.element.getStylesheet().preferredSize(vw, vh)
					.orElse(this.element.preferredSize(this.children));
		}

		// ResizableElement

		@Override
		public Dimensions getPreferredSize() {
			if (this.preferredSize == null) {
				throw new NullPointerException("Preferred Size not yet calculated!");
			}

			return this.preferredSize;
		}

		@Override
		public Dimensions getMinimumSize() {
			if (this.minimumSize == null) {
				throw new NullPointerException("Minimum Size not yet calculated!");
			}

			return this.minimumSize;
		}

		@Override
		public Component<?> getComponent() {
			return this.element;
		}

		@Override
		public void setRenderRegion(Region region) {
			this.renderRegion = region;
		}
	}
}
