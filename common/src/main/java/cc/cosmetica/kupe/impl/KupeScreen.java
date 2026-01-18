/*
 * Copyright 2024, 2025 Cosmetica
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

import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.Renderable;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.impl.text.FormattedCharSeqRenderer;
import cc.cosmetica.kupe.util.ImageUtilities;
import cc.cosmetica.kupe.util.MultiCache;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.*;

/**
 * The screen that controls the Kupe gui rendering. This handles the rendering and event calls to the root component.
 */
public final class KupeScreen extends Screen {
	public KupeScreen(@Nullable Screen parent, net.minecraft.network.chat.Component title, Component rootComponent, boolean defaultBackground) {
		super(title);

		this.parent = parent;
		this.tree = new ComponentTree(rootComponent);
		this.context = new KupeScreenContext();
		this.defaultBackground = defaultBackground;
	}

	private final Screen parent;
	private final ComponentTree tree;
	private final Context context;
	private final boolean defaultBackground;
	private boolean built = false;
	// todo how does this interact with states being deleted/reinstated
	private final Map<Class<Component>, Map<Object, Component>> cache = new HashMap<>();

	@Override
	public void init(int w, int h) {
		super.init(w, h); // required

		// can only do the initial tree build once
		// subsequent changes to the tree *must* be rebuilds!
		if (!built) {
			this.tree.buildAll();
			built = true;
		} else {
			this.tree.rebuildAll();
		}

		this.resize();
	}

	@Override
	public void resize(int w, int h) {
		super.init(w, h); // required
		this.resize();
	}

	void resize() {
		this.tree.resizeAll(this.context);
	}

	void rebuildComponents(Iterable<Component> components) {
		this.tree.rebuildComponents(components);
		this.resize();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float tickDelta) {
		// TODO re-implement this
//		if (this.defaultBackground) { // TODO better way to do this that also handles scrolling backgrounds
//			this.renderBackground(graphics, mouseX, mouseY, tickDelta);
//		}

		PoseCanvas canvas = new PoseCanvas(graphics, this.minecraft, this.context, tickDelta);
		this.tree.render(canvas, mouseX, mouseY);
		
		if (debug) {
			this.tree.renderDebug(canvas, this.height);
		}
	}

	@Override
	public boolean keyPressed(KeyEvent e) {
		return this.keyPressed(e.key(), e.scancode(), e.modifiers());
	}

	private boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (debug) {
			if (this.tree.keyDebug(keyCode)) {
				return true;
			}
		}

		if (allowDebug) {
			if (keyCode == GLFW.GLFW_KEY_I && modifiers == (GLFW.GLFW_MOD_CONTROL|GLFW.GLFW_MOD_SHIFT)) {
				debug = !debug;
			}
		}

		return this.tree.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(KeyEvent e) {
		return this.tree.keyReleased(e.key(), e.scancode(), e.modifiers());
	}

	@Override
	public boolean charTyped(CharacterEvent e) {
		return this.tree.charTyped((char)e.codepoint(), e.modifiers());
	}

	/**
	 * Handle a mouse click at the specified position on the screen.
	 * @return whether to consume click and not pass to game.
	 */
	@Override
	public boolean mouseClicked(MouseButtonEvent e, boolean bl) {
		doubleClick = bl;
		clickModifiers = e.modifiers();
		return this.tree.mouseClicked(e.x(), e.y(), e.button());
	}

	// TODO Use event classes in Kupe to prevent the need for hackery like this
	// hack to pass data
	public static boolean doubleClick;
	public static int clickModifiers;

	@Override
	public boolean mouseReleased(MouseButtonEvent e) {
		return this.tree.mouseReleased(e.x(), e.y(), e.button());
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double f, double delta) {
		return this.tree.mouseScrolled(mouseX, mouseY, delta);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		this.tree.mouseMoved(mouseX, mouseY);
	}

	@Override
	public void onClose() {
		// set parent screen
		this.minecraft.setScreen(this.parent);
		this.tree.dispose();
	}

	@Override
	public String toString() {
		return "KupeScreen { screen=" + this.tree + ", built=" + this.built + "}";
	}

	private static boolean debug;
	private static boolean allowDebug = true;

	public static void setAllowDebug(boolean enabled) {
		allowDebug = enabled;
		if (!enabled) debug = false;
	}

	public static void setDebug(boolean enabled) {
		if (allowDebug)
			debug = enabled;
	}

	class KupeScreenContext implements Context {
		// don't do lots of IO when resizing the screen!
		private final MultiCache<Optional<Dimensions>> dimensionCache = new MultiCache<>(new Optional[64], 5000L);

		@Override
		public int getWidth(Text text) {
			return KupeScreen.this.font.width(text.toMinecraftComponent());
		}

		@Override
		public int getLineHeight() {
			return KupeScreen.this.font.lineHeight;
		}

		@Override
		public int getTextHeight(Text text, int maxWidth) {
			assert KupeScreen.this.minecraft != null;
			return KupeScreen.this.minecraft.font.wordWrapHeight(text.toMinecraftComponent(), maxWidth);
		}

		@Override
		public AbstractTexture getTexture(ResourceKey location) {
			assert KupeScreen.this.minecraft != null;
			return KupeScreen.this.minecraft.getTextureManager().getTexture(location.toResourceLocation());
		}

		@Override
		public Optional<Dimensions> getImageDimensions(ResourceKey location) throws IOException {
			return this.dimensionCache.compute(location, this::computeImageDimensions);
		}

		private Optional<Dimensions> computeImageDimensions(ResourceKey location) throws IOException {
			Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(location.toResourceLocation());
			return resource.isPresent() ? ImageUtilities.getImageDimensions(resource.get().open()) : Optional.empty();
		}

		@Override
		public List<Renderable> split(Text text, int maxWidth) {
			assert KupeScreen.this.minecraft != null;

			List<Renderable> result = new ArrayList<>();

			for (FormattedCharSequence component : KupeScreen.this.minecraft.font.split(text.toMinecraftComponent(), maxWidth)) {
				result.add(new FormattedCharSeqRenderer(KupeScreen.this.minecraft.font, component));
			}

			return result;
		}

		@Override
		public int getViewWidth() {
			return KupeScreen.this.width;
		}

		@Override
		public int getViewHeight() {
			return KupeScreen.this.height;
		}
	}
}
