package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.ResizableElement;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.RootStylesheet;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Region;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

class ComponentTree {
	public ComponentTree(Component root) {
		this.root = new ComponentNode(null, root);
	}

	private final ComponentNode root;

	/**
	 * Build the component tree from the root.
	 */
	public void buildAll() {
		this.root.walk(ComponentNode::buildOnce);
	}

	/**
	 * Resize all elements in the component tree.
	 * @param screenRegion the region to size the root component to.
	 */
	public void resizeAll(Region screenRegion) {
		final int vw = screenRegion.getWidth();
		final int vh = screenRegion.getHeight();

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
				node.computeSizes(vw, vh);
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

	public void render(Canvas canvas, int mouseX, int mouseY) {
		this.root.walk(node -> node.render(canvas, mouseX, mouseY));
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean consumedClick = false;

		Deque<ComponentNode> nodes = new ArrayDeque<>();
		nodes.add(this.root);

		while (!nodes.isEmpty()) {
			ComponentNode node = nodes.remove();

			// only process clicks in this element's render region
			if (node.renderRegion.contains((int)mouseX, (int)mouseY)) {
				// if this element consumes the click, do not pass to its children, and mark as consumed click.
				// it can be passed to overlapping siblings, however.
				if (node.element.mouseClicked(mouseX, mouseY, button)) {
					consumedClick = true;
				} else {
					nodes.addAll(node.children); // children should be within parent's region!
				}
			}
		}

		return consumedClick;
	}

	public void mouseMoved(double mouseX, double mouseY) {
		Deque<ComponentNode> nodes = new ArrayDeque<>();
		nodes.add(this.root);

		while (!nodes.isEmpty()) {
			ComponentNode node = nodes.remove();

			if (node.renderRegion.contains((int)mouseX, (int)mouseY)) {
				node.element.mouseMoved(mouseX, mouseY);
				nodes.addAll(node.children); // children should be within parent's region!
			}
		}
	}

	private static class ComponentNode implements ResizableElement {
		ComponentNode(@Nullable ComponentNode parent, Component element) {
			this.parent = parent;
			this.element = element;
		}

		// content
		final Component element;
		// hierarchy
		final List<ComponentNode> children = new ArrayList<>();
		final @Nullable ComponentNode parent;
		// extra data
		Region renderRegion;
		Dimensions preferredSize, minimumSize; // calculated and cached
		boolean grey; // grey if visited in resizing stage for adding children
		              // but not for actual preferred size calculation

		/**
		 * Build just this node. Does not recursively build children.
		 */
		private void buildOnce() {
			// rebuild children
			this.children.clear();

			for (Component component : this.element.build()) {
				this.children.add(new ComponentNode(this, component));
			}

			// flatten style for component
			List<Style> styles = new ArrayList<>();
			ComponentNode visitingNode = this;
			boolean self = true;

			while (visitingNode != null) {
				@Nullable Stylesheet stylesheet = visitingNode.element.getStylesheet();

				if (stylesheet != null) {
					// fill overrides for this element class
					stylesheet.fillOverrides(styles, this.element.getClass(), self);
				}

				visitingNode = visitingNode.parent;
				self = false;
			}

			RootStylesheet.fillDefaultOverrides(styles, this.element.getClass());

			this.element.setFlattenedStyle(Style.merge(styles));
 		}

		private void computeSizes(int vw, int vh) {
			this.minimumSize = Dimensions.max(
					this.element.minimumSize(this.children),
					this.element.getStyle().get(CommonProperties.MINIMUM_SIZE).apply(vw, vh).orElse(Dimensions.NONE)
			);

			this.preferredSize = this.element.getStyle().get(CommonProperties.PREFERRED_SIZE).apply(vw, vh)
					.orElse(this.element.preferredSize(this.children));
		}

		/**
		 * Resize the component
		 */
		private void resize() {
			this.element.resize(this.renderRegion, this.children);

			for (ComponentNode node : this.children) {
				if (node.renderRegion == null) {
					throw new IllegalStateException("Node " + this.element + " has not resized child " + node.element);
				}
			}
		}

		private void render(Canvas canvas, int mouseX, int mouseY) {
			this.element.render(canvas, this.renderRegion, mouseX, mouseY);
		}

		private void walk(Consumer<ComponentNode> nodeConsumer) {
			Deque<ComponentNode> nodes = new ArrayDeque<>();
			nodes.add(this);

			while (!nodes.isEmpty()) {
				ComponentNode node = nodes.remove();
				nodeConsumer.accept(node);
				nodes.addAll(node.children);
			}
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
		public Component getComponent() {
			return this.element;
		}

		@Override
		public void setRenderRegion(Region region) {
			this.renderRegion = region;
		}
	}
}
