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

import cc.cosmetica.kupe.api.*;
import cc.cosmetica.kupe.api.maths.Region;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Optional;

/**
 * Implementation of Canvas.
 * It seems somewhat wasteful to create this every frame. Perhaps if the posestack doesn't change we can cache and just
 * update tickDelta.
 */
public class PoseCanvas implements Canvas {
	public PoseCanvas(GuiGraphics graphics, Minecraft minecraft, Context context, float tickDelta) {
		this.graphics = graphics;
		this.stack = graphics.pose();
		this.kupeStack = new KupeStack(graphics.pose());
		this.minecraft = minecraft;
		this.context = context;
		this.tickDelta = tickDelta;

		// set up scissor stack
		this.scissorStack = new ScissorStack();
		this.fastScissor = false;
	}

	final GuiGraphics graphics;
	private final PoseStack stack;
	private final MatrixStack kupeStack;
	private final Minecraft minecraft;
	private final Context context;
	private final float tickDelta;
	private float alpha = 1.0f;
	//scissor
	private @NotNull ScissorStack scissorStack;
	private boolean fastScissor;
	private boolean floatingStackActive = false;
	private FloatingStack floatingStack;

	@Override
	public Context getDrawingContext() {
		return this.context;
	}

	@Override
	public MatrixStack getStack() {
		return this.kupeStack;
	}

	@Override
	public void disableTransparency() {
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		this.alpha = 1.0f;
	}

	@Override
	public void setTransparency(float transparency) {
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, transparency);
		this.alpha = transparency;
	}

	@Override
	public void setTexture(ResourceKey texture) {
		RenderSystem.setShaderTexture(0, texture.toResourceLocation());
	}

	@Override
	public void useScissor(@Nullable Region region, boolean stack) {
		// update region minecraft rendering engine is using and in the stack
		if (region == null) {
			this.scissorStack.region = null;
			RenderSystem.disableScissor();
		} else {
			// check whether to scroll scissor region
			ScissorStack scissorScroller = this.scissorStack.prevNode;
			if (scissorScroller != null && (scissorScroller.scrollX != 0 || scissorScroller.scrollY != 0)) {
				region = region.translate((int) scissorScroller.scrollX, (int) scissorScroller.scrollY);
			}

			// stack scissor
			if (stack) {
				Region previous = this.scissorStack.region;
				if (previous != null) {
					region = previous.intersect(region);
				}
			}

			// set current region
			this.scissorStack.region = region;

			double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
			double windowHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

			// 1.20.1+
			this.graphics.flush();
			RenderSystem.disableDepthTest();

			RenderSystem.enableScissor(
					(int) (region.getX() * guiScale),
					(int) ((windowHeight - (region.getY()+region.getHeight())) * guiScale), // lower left corner in opengl coordinate system
					(int) (region.getWidth() * guiScale),
					(int) (region.getHeight() * guiScale)
			);
		}
	}

	@Override
	public Optional<Region> getScissor() {
		if (this.scissorStack.region == null) {
			return Optional.empty();
		}

		// translate back
		ScissorStack scissorScroller = this.scissorStack.prevNode;
		if (scissorScroller != null && (scissorScroller.scrollX != 0 || scissorScroller.scrollY != 0)) {
			return Optional.of(this.scissorStack.region.translate(-((int)scissorScroller.scrollX), -((int)scissorScroller.scrollY)));
		} else {
			return Optional.of(this.scissorStack.region);
		}
	}

	public void startFloatingQueue() {
		this.floatingStackActive = true;
	}

	public void stopFloatingQueue() {
		this.floatingStackActive = false;

		while (this.floatingStack != null) {
			this.floatingStack.action.renderAction(this.floatingStack.x, this.floatingStack.y);
			this.floatingStack = this.floatingStack.parent;
		}
	}

	// TODO maybe make this API in an update
	/**
	 * Render after all components are rendered.
	 * @param renderable the renderable to render.
	 */
	public void renderFloating(RenderAction renderable, int x, int y) {
		if (!this.floatingStackActive) {
			renderable.renderAction(x, y);
			return;
		}

		this.floatingStack = new FloatingStack(
				this.floatingStack,
				renderable,
				(int)this.scissorStack.scrollX + x,
				(int)this.scissorStack.scrollY + y);
	}

	@Override
	public void drawTooltip(Text text, int splitWidth, int x, int y) {
		// Tooltip should be always floating!
		renderFloating((x_, y_) -> {
			this.graphics.renderTooltip(
					Minecraft.getInstance().font,
					Minecraft.getInstance().font.split(text.toMinecraftComponent(), splitWidth),
					x_, y_);
		}, x, y);
	}

	@Override
	public void setFastScissor(boolean fastScissor) {
		this.fastScissor = fastScissor;
	}

	@Override
	public void scroll(float amountX, float amountY) {
		if (!this.scissorStack.hasActiveTranslation) {
			this.stack.pushPose(); // SCROLL_PUSH
			this.stack.translate(amountX, amountY, 0);
			this.scissorStack.scroll(amountX, amountY);
		}
	}

	float getScrollX() {
		return this.scissorStack.scrollX;
	}

	float getScrollY() {
		return this.scissorStack.scrollY;
	}

	public boolean isOutOfBounds(Region region) {
		return fastScissor && this.scissorStack.region != null && !region.overlaps(this.scissorStack.region);
	}

	public void pushScissor() {
		this.scissorStack = new ScissorStack(this.scissorStack); // push
	}

	// pop the scissor translation. we don't scroll the component itself. Only its contents!
	public void popTranslation() {
		if (this.scissorStack.hasActiveTranslation) {
			this.stack.popPose(); // SCROLL_POP
		}

		this.scissorStack.hasActiveTranslation = false;
	}

	public void popScissor() {
		// in case i forger :skull:
		this.popTranslation();
		//if (this.scissorStack.hasActiveTranslation)
		//	throw new IllegalStateException("Kupe Error: Did not pop scissor translation before scissor");

		Region oldRegion = this.getScissor().orElse(null);
		this.scissorStack = this.scissorStack.prevNode; // pop
		Region newRegion = this.getScissor().orElse(null);

		// use new region if it changed
		if (newRegion != oldRegion) {
			this.useScissor(newRegion, false);
		}
	}

	@Override
	public void drawCenteredText(Text text, int x, int y, int colour) {
		this.graphics.drawCenteredString(this.minecraft.font, text.toMinecraftComponent(), x, y, colour);
	}

	@Override
	public void drawText(Text text, int x, int y, int colour) {
		this.graphics.drawString(this.minecraft.font, text.toMinecraftComponent(), x, y, colour);
	}

	@Override
	public void drawRect(Region region, int colour) {
		int x0 = region.getX();
		int y0 = region.getY();

		float r = ((colour >> 16) & 0xFF) / 255.0f;
		float g = ((colour >> 8) & 0xFF) / 255.0f;
		float b = (colour & 0xFF) / 255.0f;

		this.drawRect(x0, y0, region.getWidth(), region.getHeight(), 0.0f, r, g, b);
	}

	@Override
	public void drawRect(int x0, int y0, int width, int height, float z, float r, float g, float b) {
		RenderSystem.setShader(CoreShaders.POSITION_COLOR);
		Matrix4f matrix4f = this.stack.last().pose();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

		// x1, y1 exclusive because for some reason minecraft works this way
		int x1 = x0 + width;
		int y1 = y0 + height;

		final float a = this.alpha;
		BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferBuilder.addVertex(matrix4f, x0, y1, z).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, x1, y1, z).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, x1, y0, z).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, x0, y0, z).setColor(r, g, b, a);

		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha); // reset alpha
	}

	@Override
	public void drawTexture(int x0, int y0, int width, int height, float z, ResourceKey texture) {
		RenderSystem.setShader(CoreShaders.POSITION_TEX);
		this.setTexture(texture);
		Matrix4f matrix4f = this.stack.last().pose();

		// x1, y1 exclusive because for some reason minecraft works this way
		int x1 = x0 + width;
		int y1 = y0 + height;

		BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferBuilder.addVertex(matrix4f, (float)x0, (float)y1, z).setUv(0, 1);
		bufferBuilder.addVertex(matrix4f, (float)x1, (float)y1, z).setUv(1, 1);
		bufferBuilder.addVertex(matrix4f, (float)x1, (float)y0, z).setUv(1, 0);
		bufferBuilder.addVertex(matrix4f, (float)x0, (float)y0, z).setUv(0, 0);

		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
	}

	@Override
	public PolyBuilder drawQuads(PolyBuilder.Mode mode) {
		mode.applyShader();
		return BufferPolyBuilder.create(Tesselator.getInstance(), VertexFormat.Mode.QUADS, mode, this.stack.last().pose());
	}

	@Override
	public PolyBuilder drawTriangles(PolyBuilder.Mode mode) {
		mode.applyShader();
		return BufferPolyBuilder.create(Tesselator.getInstance(), VertexFormat.Mode.TRIANGLES, mode, this.stack.last().pose());
	}

	@Override
	public void renderMinecraftComponent(net.minecraft.client.gui.components.Renderable component, int mouseX, int mouseY) {
		component.render(this.graphics, mouseX, mouseY, this.tickDelta);
	}

	// Allows us to easily change current region without wasting time/memory
	// Probably a premature optimisation
	private static class ScissorStack {
		ScissorStack() {
			// tail
		}
		ScissorStack(ScissorStack prevNode) {
			this.prevNode = prevNode;
			this.region = prevNode.region;
			this.scrollX = prevNode.scrollX;
			this.scrollY = prevNode.scrollY;
		}
		void scroll(float scrollX, float scrollY) {
			this.scrollX += scrollX;
			this.scrollY += scrollY;
			this.hasActiveTranslation = true;
		}

		private @Nullable Region region;
		private boolean hasActiveTranslation = false; // set if scrollX or scrollY changes.
		private float scrollX, scrollY = 0;
		private ScissorStack prevNode;
	}

	private static class FloatingStack {
		FloatingStack(@Nullable FloatingStack parent, RenderAction action, int x, int y) {
			this.parent = parent;
			this.action = action;
			this.x = x;
			this.y = y;
		}

		private final @Nullable FloatingStack parent;
		private final RenderAction action;
		private final int x;
		private final int y;
	}

	@FunctionalInterface
	public interface RenderAction {
		void renderAction(int x, int y);
	}
}
