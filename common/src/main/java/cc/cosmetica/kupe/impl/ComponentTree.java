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

package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.ResizableElement;
import cc.cosmetica.kupe.api.gui.WrappingElement;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.RootStylesheet;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

class ComponentTree {
	public ComponentTree(Component root) {
		this.root = new Node(null, root);
		this.updateDebugComponent();
	}

	private final Node root;

	/**
	 * Build the component tree from the root.
	 */
	public void buildAll() {
		this.root.walk(Node::buildThis);
	}

	/**
	 * Rebuild the given components. Will only rebuild the most basal nodes. For example, if a parent and its descendant
	 * are provided, the parent will be the one rebuilt.
	 * @param components the components to rebuild.
	 */
	public void rebuildComponents(Iterable<Component> components) {
		// create a set copying the components iterable
		HashSet<Component> toRebuild = new HashSet<>();
		components.forEach(toRebuild::add);

		// find which component nodes actually need to be rebuilt
		// we want to do this at the most basal points possible.
		// That is, if a parent node and its child both need to be rebuilt, rebuild the parent.
		Deque<Node> nodes = new ArrayDeque<>();
		nodes.add(this.root);

		while (!nodes.isEmpty()) {
			Node node = nodes.remove();

			// if the node needs to be rebuilt, destroy its children and rebuild the node
			if (toRebuild.contains(node.element)) {
				// rebuild node
				node.rebuildThis();

				// build new children
				for (Node child : node.children) {
					child.walk(Node::buildThis);
				}
			}
			// otherwise check its children to see if they need rebuilding
			else for (Node child : node.children) {
				nodes.push(child);
			}
		}
	}

	/**
	 * Resize all elements in the component tree. The root region will be resized to the screen size.
	 * @param context the screen rendering context.
	 */
	public void resizeAll(Context context) {
		final int vw = context.getViewWidth();
		final int vh = context.getViewHeight();

		// Compute all paddings and margins
		this.root.walk(node -> node.computeMargins(vw, vh));

		// DFS for preferred size calculation
		// We want the leaves to have preferred sizes calculated before their parents
		// because it depends on the children's preferred sizes being calculated

		Deque<Node> nodes = new ArrayDeque<>();
		nodes.push(this.root);

		// Calculate preferred sizes up the tree
		while (!nodes.isEmpty()) {
			Node node = nodes.pop();

			if (node.grey) {
				node.grey = false; // we are done with this node
				node.computeSizes(context);
			} else {
				node.grey = true; // we need to visit it one more time, after children are done
				nodes.push(node);

				// all children need to be visited before this node
				for (Node child : node.children) {
					nodes.push(child);
				}
			}
		}

		// BFS for resizing (down the tree)
		// queue for wrapping like text if it overflows
		Queue<Node> wrappingOverflowed = new PriorityQueue<>((n, m) -> m.depth - n.depth); // reverse sort
		List<Node> immediateOverflowed = new ArrayList<>();
		this._resize(nodes, context, wrappingOverflowed, immediateOverflowed);

		// Don't bother if nothing needs to be handled
		if (!wrappingOverflowed.isEmpty()) {
			// Account for wrapping nodes to allocate additional height
			boolean updateRequired = false;
			// Only update sizings for required nodes by collecting parents (avoiding duplicates)
			// To avoid one branch racing ahead of the others we go from leaves to head
			Collection<Node> visitedAtThisDepth = new HashSet<>();
			int depth = wrappingOverflowed.peek().depth;

			while (!wrappingOverflowed.isEmpty()) {
				Node n = wrappingOverflowed.remove();

				if (n.depth != depth) {
					depth = n.depth;
					visitedAtThisDepth = new HashSet<>(); // "memory efficiency" (might not actually be worth it. could save cycles by blocking on adding?)
				} else if (visitedAtThisDepth.contains(n)) {
					continue; // skip. already visited (avoid updating duplicates)
				}

				visitedAtThisDepth.add(n);

				// Recalculate Sizing
				Dimensions oldIntrinsic = n.intrinsicSize;
				n.computeSizes(context);
				if (oldIntrinsic != n.intrinsicSize) updateRequired = true;

				// Add parent
				if (n.parent != null)
					wrappingOverflowed.add(n.parent);
			}

			// If anything changed, resize again
			if (updateRequired) {
				this._resize(nodes, context, null, null);
			}
		}

		// Update debug component
		this.updateDebugComponent();
	}

	/**
	 * BFS for resizing (down the tree)
	 * @param nodes the queue to use for nodes.
	 */
	private void _resize(Queue<Node> nodes, Context context, @Nullable Collection<Node> wrappingOverflowed, @Nullable Collection<Node> immediateOverflowed) {
		nodes.add(this.root);
		// I designed Kupe with renderRegion as the content region. So we gotta subtract padding manually here.
		final Margins rootPadding = this.root.padding;
		this.root.renderRegion = new Region(rootPadding.left, rootPadding.top, context.getViewWidth() - rootPadding.horizontal(), context.getViewHeight() - rootPadding.vertical());

		// Resize down the tree
		while (!nodes.isEmpty()) { // we have computed actual preferred sizes before resizing
			Node node = nodes.remove(); // nb we also, upon resizing, need to set the children's actual render regions
			node.resize(context);
			nodes.addAll(node.children);

			// check for wrapping overflow
			if (wrappingOverflowed != null && node.parent != null && node.element instanceof WrappingElement) {
				int realHeight = ((WrappingElement)node.element).realHeight(node.renderRegion.getWidth(), context);

				if (node.renderRegion.getHeight() < realHeight) {
					// use REAL dimensions as the actual intrinsic size
					node.intrinsicSize = new Dimensions(node.renderRegion.getWidth(), realHeight);
					// set real height as minimum height too
					// Math.max may not be necessary as it shouldnt overflow to begin with if min height is enough
					node.minimumSize = new Dimensions(node.minimumSize.getWidth(), Math.max(node.minimumSize.getHeight(), node.intrinsicSize.getHeight()));

					wrappingOverflowed.add(node.parent); // recalculate node parent

					assert immediateOverflowed != null;
					immediateOverflowed.add(node);
				}
			}
		}
	}

	public void render(PoseCanvas canvas, int mouseX, int mouseY) {
		//this.root.walk(node -> node.render(canvas, mouseX, mouseY));
		// DFS
		Deque<Node> nodes = new ArrayDeque<>();
		nodes.add(this.root);
		Set<Node> grey = new HashSet<>(); // nodes that have rendered background but not rendered
		// this ultimately makes renderBackground occur before child components are considered, and render after.

		while (!nodes.isEmpty()) {
			Node n = nodes.pop();
			if (grey.contains(n)) {
				n.render(canvas, mouseX, mouseY);// mouseX/mouseY adjusted for scroll by render()
				grey.remove(n);

				// pop scissors
				canvas.popScissor();
			} else if (n.children.isEmpty()) {
				// optimisation: do both renderBackground and render for leaf nodes without touching stack

				// push scissors
				canvas.pushScissor();
				// render
				n.renderBackground(canvas);
				n.render(canvas, mouseX, mouseY);// mouseX/mouseY adjusted for scroll by render()
				// pop scissors
				canvas.popScissor();
			} else {
				// push scissors
				canvas.pushScissor();

				// render background
				n.renderBackground(canvas);

				// non-grey item with children: put item on the stack then its children
				nodes.push(n);
				for (Node child : n.children)
					if (!canvas.isOutOfBounds(child.renderRegion)) {
						nodes.push(child);
					}

				// mark as grey so when it is revisited, it is rendered
				grey.add(n);
			}
		}
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean consumedClick = false;

		Deque<Node> nodes = new ArrayDeque<>();
		nodes.add(this.root);

		while (!nodes.isEmpty()) {
			Node node = nodes.remove();

			// only process clicks in this element's render region
			if (node.trueRenderRegion().contains((int)mouseX, (int)mouseY)) {
				// if this element consumes the click, do not pass to its children, and mark as consumed click.
				// it can be passed to overlapping siblings, however.
				if (node.element.mouseClicked(mouseX + node.scrollX, mouseY + node.scrollY, button)) {
					consumedClick = true;
				} else {
					nodes.addAll(node.children); // children should be within parent's region!
				}
			}
		}

		return consumedClick;
	}

	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.root.walkAndTest(node -> node.element.mouseReleased(mouseX + node.scrollX, mouseY + node.scrollY, button));
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		walkWithRegionalRestriction((int)mouseX, (int)mouseY, node -> node.element.mouseScrolled(mouseX + node.scrollX, mouseY + node.scrollY, delta));
		return false;
	}

	public void mouseMoved(double mouseX, double mouseY) {
		walkWithRegionalRestriction((int)mouseX, (int)mouseY, node -> node.element.mouseMoved(mouseX + node.scrollX, mouseY + node.scrollY));
	}

	// don't use for render; uses true render region
	private void walkWithRegionalRestriction(int mouseX, int mouseY, Consumer<Node> action) {
		Deque<Node> nodes = new ArrayDeque<>();
		nodes.add(this.root);

		while (!nodes.isEmpty()) {
			Node node = nodes.remove();

			if (node.trueRenderRegion().contains(mouseX, mouseY)) {
				action.accept(node);
				nodes.addAll(node.children); // children should be within parent's region!
			}
		}
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.root.walkAndTest(node -> node.element.keyPressed(keyCode, scanCode, modifiers));
	}

	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return this.root.walkAndTest(node -> node.element.keyReleased(keyCode, scanCode, modifiers));
	}

	/**
	 * Called when hooks referencing the components need to be cleared.
	 */
	public void dispose() {
		this.root.walk(Node::dispose);
	}

	// Debug

	private Text debugParentText;
	private Text debugChildText;
	private Node debugParent;
	private int debugIndex = 0;

	public void renderDebug(Canvas canvas, int vh) {
		int lineHeight = canvas.getDrawingContext().getLineHeight();

		canvas.drawText(debugParentText, 0, vh - lineHeight * 3, 0xAAAAAA); // grey to show it's not selected
		canvas.drawText(debugChildText, 0, vh - lineHeight * 2, 0xFFFFFF);
		canvas.drawText(DEBUG_INSTRUCTIONS, 0, vh - lineHeight, 0xFFFFFF);
	}

	public boolean keyDebug(int keyCode) {
		switch (keyCode) {
		case GLFW.GLFW_KEY_1: // Back
			if (this.debugParent == this.root) {
				this.debugParent = null;
				this.updateDebugComponent();
			} else if (this.debugParent != null) {
				assert this.debugParent.parent != null : "Only root has null parent.";

				this.debugIndex = this.debugParent.parent.children.indexOf(this.debugParent);
				this.debugParent = this.debugParent.parent;
				this.updateDebugComponent();
			}
			return true;
		case GLFW.GLFW_KEY_2: // Step In
			if (this.debugParent == null) {
				this.debugParent = this.root;
				this.debugIndex = 0;
				this.updateDebugComponent();
			} else if (this.debugParent.children.size() > 0) {
				this.debugParent = this.debugParent.children.get(this.debugIndex);
				this.debugIndex = 0;
				this.updateDebugComponent();
			}
			return true;
		case GLFW.GLFW_KEY_3: // Previous
			if (this.debugParent != null) {
				if (--this.debugIndex < 0) {
					this.debugIndex = this.debugParent.children.size() - 1;
				}

				this.updateDebugComponent();
			}
			return true;
		case GLFW.GLFW_KEY_4: // Next
			if (this.debugParent != null) {
				if (++this.debugIndex >= this.debugParent.children.size()) {
					this.debugIndex = 0;
				}

				this.updateDebugComponent();
			}
			return true;
		}

		return false;
	}

	private void updateDebugComponent() {
		if (this.debugParent == null) {
			this.debugParentText = Text.literal("");
			this.debugChildText = Text.literal("[Root]");
		} else {
			this.debugParentText = this.debugParent == this.root ? Text.literal("[Root]") : componentDebugInfo(this.debugParent);

			if (this.debugParent.children.size() == 0) {
				this.debugChildText = Text.literal("(No Children)");
			} else {
				Node child = this.debugParent.children.get(this.debugIndex);
				this.debugChildText = componentDebugInfo(child);
			}
		}
	}

	private Text componentDebugInfo(Node node) {
		return Text.literal(node.element.getClass().getSimpleName() + " " + node.renderRegion +
				" (" + node.intrinsicSize.toString() + "i, " + node.minimumSize + "m, " + node.maximumSize + "M)");
	}

	private static final Text DEBUG_INSTRUCTIONS = Text.literal("[1] Back [2] Step In [3] Previous [4] Next");

	private static class Node implements ResizableElement {
		Node(@Nullable ComponentTree.Node parent, Component element) {
			this.parent = parent;
			this.element = element;
			// Track depth
			this.depth = parent == null ? 0 : parent.depth + 1;
		}

		// content
		final Component element;
		// hierarchy
		final List<Node> children = new ArrayList<>();
		final @Nullable ComponentTree.Node parent;
		final int depth;
		// extra data
		Region renderRegion;
		float scrollX, scrollY = 0;
		Dimensions minimumSize, maximumSize, intrinsicSize; // calculated and cached
		OptionalInt width, height;
		Margins padding, margins; // as above
		boolean grey; // grey if visited in resizing stage for adding children
		              // but not for actual preferred size calculation

		/**
		 * Get the render region adjusted for scroll.
		 */
		Region trueRenderRegion() {
			return this.renderRegion.translate((int)this.scrollX, (int)this.scrollY);
		}

		/**
		 * Rebuild just this node.
		 */
		private void rebuildThis() {
			// clear any states used by children
			for (Node child : this.children) {
				child.walk(Node::dispose);
			}

			// clear children
			this.children.clear();

			// clear extractions as we want to re-generate these.
			StateManagerImpl.clearConfig(this.element);

			// build
			this.buildThis();
		}

		/**
		 * Build just this node. Does not recursively build children.
		 * Assumes that the style is computed for the parent node (that is, parent has been built).
		 */
		private void buildThis() {
			// build component and add children to the tree
			for (Component component : this.element.build()) {
				this.children.add(new Node(this, component));
			}

			this.element.setFlattenedStyle(this.buildStyle());
 		}
		
		private Style buildStyle() {
			// flatten style for component
			List<Style> styles = new ArrayList<>();

			// declared style takes priority
			@Nullable Style declaredStyle = this.element.getDeclaredStyle();

			if (declaredStyle != null) {
				// fill overrides for this element class
				styles.add(declaredStyle);
			}

			// stylesheet for this component is next most important
			@Nullable Stylesheet stylesheet = this.element.getStylesheet();

			if (stylesheet != null) {
				// fill overrides for this element class
				stylesheet.fillOverrides(styles, this.element.getClass(), this.element.getTags(), true);
			}

			// Parent stylesheets, applied to us, are the next most important.
			Node visitingNode = this.parent;
			while (visitingNode != null) {
				stylesheet = visitingNode.element.getStylesheet();

				if (stylesheet != null) {
					// fill overrides for this element class
					stylesheet.fillOverrides(styles, this.element.getClass(), this.element.getTags(), false);
				}
				
				visitingNode = visitingNode.parent; // each successive ancestor is lower priority
			}
			
			// Then, inherited properties from the parent
			// Only marked properties are inherited
			if (this.parent != null) styles.add(this.parent.element.getStyle().inheritance);

			// finally, the root stylesheet has lowest priority
			RootStylesheet.fillDefaultOverrides(styles, this.element.getClass());
			
			return Style.merge(styles);
		}

		private void computeMargins(int vw, int vh) {
			this.padding = this.element.getStyle().get(CommonProperties.PADDING).apply(vw, vh);
			this.margins = this.element.getStyle().get(CommonProperties.MARGINS).apply(vw, vh);
		}

		private void computeSizes(Context context) {
			final int vw = context.getViewWidth();
			final int vh = context.getViewHeight();

			this.minimumSize = Dimensions.max(
					this.element.minimumSize(this.children, vw, vh),
					this.element.getStyle().get(CommonProperties.MINIMUM_SIZE).apply(vw, vh).orElse(Dimensions.NONE)
			);

			// max size is specified in the style only
			this.maximumSize = this.element.getStyle().get(CommonProperties.MAXIMUM_SIZE).apply(vw, vh);
			// intrinsic size is a property of the component only
			// it will typically be used when combined with other properties to make 'preferred size'
			this.intrinsicSize = this.element.intrinsicSize(this.children, context);
			// clamp to min/max size of this element (if it can only go up to 30px we dont want it saying it wants 100px!)
			this.intrinsicSize = Dimensions.clamp(this.intrinsicSize, this.minimumSize, this.maximumSize);

			// width and height
			this.width = this.element.getStyle().get(CommonProperties.WIDTH).apply(vw, vh);

			if (this.width.isPresent()) {
				int n = this.width.getAsInt();

				this.width = OptionalInt.of(
					Math.min(this.maximumSize.getWidth(), Math.max(n, this.minimumSize.getWidth()))
				);
			}

			this.height = this.element.getStyle().get(CommonProperties.HEIGHT).apply(vw, vh);

			if (this.height.isPresent()) {
				int n = this.height.getAsInt();

				this.height = OptionalInt.of(
						Math.min(this.maximumSize.getHeight(), Math.max(n, this.minimumSize.getHeight()))
				);
			}
		}

		/**
		 * Resize the component
		 */
		private void resize(Context context) {
			this.element.resize(this.renderRegion, this, this.children, context);

			for (Node node : this.children) {
				if (node.renderRegion == null) {
					throw new IllegalStateException("Node " + this.element + " has not resized child " + node.element);
				}
			}
		}

		private void renderBackground(PoseCanvas canvas) {
			try {
				RenderSystem.enableDepthTest();
				GL11.glDepthFunc(GL11.GL_LEQUAL);
				this.element.renderBackground(canvas, this.renderRegion, this.padding);
				// set scroll
				this.scrollY = canvas.getScrollY();
				this.scrollX = canvas.getScrollX();
			} catch (NullPointerException e) {
				throw new RuntimeException("Rendering " + this.element, e);
			}
		}

		private void render(Canvas canvas, int mouseX, int mouseY) {
			try {
				RenderSystem.enableDepthTest();
				GL11.glDepthFunc(GL11.GL_LEQUAL);
				// render still thinks it's at the original position
				// so if it's moved up, move mouse positions accordingly
				this.element.render(canvas, this.renderRegion, mouseX + (int)this.scrollX, mouseY + (int)this.scrollY);
			} catch (NullPointerException e) {
				throw new RuntimeException("Rendering " + this.element, e);
			}
		}

		/**
		 * Called when hooks referencing this component need to be cleared.
		 */
		private void dispose() {
			StateManagerImpl.clearStates(this.element);
		}

		/**
		 * Recursively iterate (in a BFS manner) this node and its children, performing the provided operation on them.
		 * @param nodeConsumer the operation to perform on each node in the tree.
		 */
		private void walk(Consumer<Node> nodeConsumer) {
			Deque<Node> nodes = new ArrayDeque<>();
			nodes.add(this);

			while (!nodes.isEmpty()) {
				Node node = nodes.remove();
				nodeConsumer.accept(node);
				nodes.addAll(node.children);
			}
		}

		/**
		 * Recursively iterate (in a BFS manner) this node and its children, performing the provided operation on them.
		 * @param nodePredicate the operation to perform on each node in the tree.
		 * @return if the predicate returned true at least once.
		 */
		private boolean walkAndTest(Predicate<Node> nodePredicate) {
			Deque<Node> nodes = new ArrayDeque<>();
			nodes.add(this);
			boolean result = false;

			while (!nodes.isEmpty()) {
				Node node = nodes.remove();
				result |= nodePredicate.test(node);
				nodes.addAll(node.children);
			}

			return result;
		}

		// ResizableElement

		@Override
		public Dimensions getMinimumSize() {
			if (this.minimumSize == null) {
				throw new NullPointerException("Minimum Size not yet calculated!");
			}

			return this.minimumSize;
		}

		@Override
		public Dimensions getMaximumSize() {
			if (this.maximumSize == null) {
				throw new NullPointerException("Maximum Size not yet calculated!");
			}

			return maximumSize;
		}

		@Override
		public Dimensions getIntrinsicSize() {
			if (this.intrinsicSize == null) {
				throw new NullPointerException("Intrinsic Size not yet calculated!");
			}

			return intrinsicSize;
		}

		@Override
		public OptionalInt getWidth() {
			return width;
		}

		@Override
		public OptionalInt getHeight() {
			return height;
		}

		@Override
		public Margins getMargins() {
			return margins;
		}

		@Override
		public Margins getPadding() {
			return padding;
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
