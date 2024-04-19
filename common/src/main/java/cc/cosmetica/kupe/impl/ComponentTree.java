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
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.RootStylesheet;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Consumer;

class ComponentTree {
	public ComponentTree(Component root) {
		this.root = new ComponentNode(null, root);
		this.updateDebugComponent();
	}

	private final ComponentNode root;

	/**
	 * Build the component tree from the root.
	 */
	public void buildAll() {
		this.root.walk(ComponentNode::buildOnce);
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

		Deque<ComponentNode> nodes = new ArrayDeque<>();
		nodes.push(this.root);

		// Calculate preferred sizes down the tree
		while (!nodes.isEmpty()) {
			ComponentNode node = nodes.pop();

			if (node.grey) {
				node.grey = false; // we are done with this node
				node.computeSizes(context);
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
		this.root.renderRegion = new Region(0, 0, vw, vh);

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

	// Debug

	private Text debugParentText;
	private Text debugChildText;
	private ComponentNode debugParent;
	private int debugIndex = 0;

	public void renderDebug(Canvas canvas, int vh) {
		int lineHeight = canvas.getDrawingContext().getLineHeight();

		canvas.drawText(debugParentText, 0, vh - lineHeight * 3, 0xFFFFFF);
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
				ComponentNode child = this.debugParent.children.get(this.debugIndex);
				this.debugChildText = componentDebugInfo(child);
			}
		}
	}

	private Text componentDebugInfo(ComponentNode node) {
		return Text.literal(node.element.getClass().getSimpleName() + " " + node.renderRegion +
				" (" + node.intrinsicSize + "i, " + node.minimumSize + "m, " + node.maximumSize + "M)");
	}

	private static final Text DEBUG_INSTRUCTIONS = Text.literal("[1] Back [2] Step In [3] Previous [4] Next");

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
		Dimensions minimumSize, maximumSize, intrinsicSize; // calculated and cached
		OptionalInt width, height;
		Margins padding, margins; // as above
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
		private void resize() {
			this.element.resize(this.renderRegion, this, this.children);

			for (ComponentNode node : this.children) {
				if (node.renderRegion == null) {
					throw new IllegalStateException("Node " + this.element + " has not resized child " + node.element);
				}
			}
		}

		private void render(Canvas canvas, int mouseX, int mouseY) {
			this.element.renderBackground(canvas, this.renderRegion, this.padding);
			this.element.render(canvas, this.renderRegion, mouseX, mouseY);
		}

		/**
		 * Recursively iterate (in a BFS manner) this node and its children, performing the provided operation on them.
		 * @param nodeConsumer the operation to perform on each node in the tree.
		 */
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
