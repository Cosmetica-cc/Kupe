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
import cc.cosmetica.kupe.api.Renderable;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.impl.text.FormattedCharSeqRenderer;
import cc.cosmetica.kupe.util.ImageUtilities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FormattedCharSequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The screen that controls the Kupe gui rendering. This handles the rendering and event calls to the root component.
 */
public final class KupeScreen extends Screen {
	public KupeScreen(ResourceLocation location, Component rootComponent) {
		super(new TranslatableComponent("screens." + location.getNamespace() + "." + location.getPath()));

		this.tree = new ComponentTree(rootComponent);
		this.context = new KupeScreenContext();
	}

	private final ComponentTree tree;
	private final Context context;

	@Override
	public void init(Minecraft minecraft, int w, int h) {
		super.init(minecraft, w, h); // required
		this.tree.buildAll();
		this.resize();
	}

	@Override
	public void resize(Minecraft minecraft, int w, int h) {
		super.init(minecraft, w, h); // required
		this.resize();
	}

	private void resize() {
		this.tree.resizeAll(this.context);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float tickDelta) {
		this.renderBackground(poseStack); // TODO way to change background in screen -- perhaps do background rendering in `Screen extends Component`

		Canvas canvas = new PoseCanvas(poseStack, this.minecraft, this.context, tickDelta);
		this.tree.render(canvas, mouseX, mouseY);

		if (debug) {
			this.tree.renderDebug(canvas, this.height);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int j, int k) {
		if (debug) {
			if (this.tree.keyDebug(keyCode)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Handle a mouse click at the specified position on the screen.
	 * @return whether to consume click and not pass to game.
	 */
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.tree.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		this.tree.mouseMoved(mouseX, mouseY);
	}

	private static boolean debug;

	public static void enableDebug() {
		debug = true;
	}

	class KupeScreenContext implements Context {
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
			return KupeScreen.this.minecraft.font.wordWrapHeight(text.getDisplayString(), maxWidth);
		}

		@Override
		public AbstractTexture getTexture(ResourceLocation location) {
			assert KupeScreen.this.minecraft != null;
			return KupeScreen.this.minecraft.getTextureManager().getTexture(location);
		}

		@Override
		public Optional<Dimensions> getImageDimensions(ResourceLocation location) throws IOException {
			Resource resource = Minecraft.getInstance().getResourceManager().getResource(location);
			return ImageUtilities.getImageDimensions(resource.getInputStream());
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
