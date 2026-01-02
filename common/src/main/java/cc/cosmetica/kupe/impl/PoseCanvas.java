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
import cc.cosmetica.kupe.mixin.GuiGraphicsAccessor;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.ColoredRectangleRenderState;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

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
	private final Matrix3x2fStack stack;
	private final MatrixStack kupeStack;
	private final Minecraft minecraft;
	private final Context context;
	private final float tickDelta;
	private float alpha = 1.0f;
	//scissor
	private @NotNull ScissorStack scissorStack;
	private boolean fastScissor;
	private boolean floatingStackActive = false;
	private @Nullable GpuTextureView texture;
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
		this.alpha = 1.0f;
	}

	@Override
	public void setTransparency(float transparency) {
		this.alpha = transparency;
	}

	@Override
	public void setTexture(ResourceKey texture) {
		this.texture = this.minecraft.getTextureManager().getTexture(texture.toResourceLocation()).getTextureView();
	}

	@Override
	public void useScissor(@Nullable Region region, boolean stack) {
		// update region minecraft rendering engine is using and in the stack
		if (region == null) {
			this.scissorStack.region = null;
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
					Minecraft.getInstance().font.split(text.toMinecraftComponent(), splitWidth)
							.stream().map(ClientTooltipComponent::create)
							.toList(),
					x_, y_,
					// TODO is this correct position
					new MenuTooltipPositioner(new ScreenRectangle(x, y, 1, 1)),
					null);
		}, x, y);
	}

	@Override
	public void setFastScissor(boolean fastScissor) {
		this.fastScissor = fastScissor;
	}

	@Override
	public void scroll(float amountX, float amountY) {
		if (!this.scissorStack.hasActiveTranslation) {
			this.stack.pushMatrix(); // SCROLL_PUSH
			this.stack.translate(amountX, amountY);
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
			this.stack.popMatrix(); // SCROLL_POP
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

	static int packColour(float r, float g, float b) {
		r = Math.max(0f, Math.min(1f, r));
		g = Math.max(0f, Math.min(1f, g));
		b = Math.max(0f, Math.min(1f, b));

		int ri = Math.round(r * 255f);
		int gi = Math.round(g * 255f);
		int bi = Math.round(b * 255f);

		return (ri << 16) | (gi << 8) | bi;
	}

	static int packColour(float r, float g, float b, float a) {
		r = Math.max(0f, Math.min(1f, r));
		g = Math.max(0f, Math.min(1f, g));
		b = Math.max(0f, Math.min(1f, b));
		a = Math.max(0f, Math.min(1f, a));

		int ri = Math.round(r * 255f);
		int gi = Math.round(g * 255f);
		int bi = Math.round(b * 255f);
		int ai = Math.round(a * 255f);

		return (ai << 24) | (ri << 16) | (gi << 8) | bi;
	}


	@Override
	public void drawRect(int x0, int y0, int width, int height, float z, float r, float g, float b) {
		GuiGraphicsAccessor graphics = (GuiGraphicsAccessor) this.graphics;
		int colour = packColour(r, g, b, this.alpha);

		graphics.getGuiRenderState().submitGuiElement(new ColoredRectangleRenderState(
				RenderPipelines.GUI_TEXTURED,
				TextureSetup.noTexture(),
				this.stack.get(new Matrix3x2f()),
				x0, y0,
				x0 + width, y0 + height,
				colour,
				colour,
				this.scissorStack.getScissorRegion().orElse(null),
				new ScreenRectangle(x0, y0, width, height)
		));
	}

	@Override
	public void drawTexture(int x0, int y0, int width, int height, float z, ResourceKey texture) {
		GuiGraphicsAccessor graphics = (GuiGraphicsAccessor) this.graphics;
		int colour = packColour(1, 1, 1, this.alpha);
		this.setTexture(texture);
		assert this.texture != null;

		graphics.getGuiRenderState().submitGuiElement(new ColoredRectangleRenderState(
				RenderPipelines.GUI_TEXTURED,
				TextureSetup.singleTexture(this.texture),
				this.stack.get(new Matrix3x2f()),
				x0, y0,
				x0 + width, y0 + height,
				colour,
				colour,
				this.scissorStack.getScissorRegion().orElse(null),
				new ScreenRectangle(x0, y0, width, height)
		));
	}

	@Override
	public PolyBuilder drawQuads(PolyBuilder.Mode mode) {
		return BufferPolyBuilder.create((GuiGraphicsAccessor) this.graphics, VertexFormat.Mode.QUADS, mode, this.alpha, this.stack.get(new Matrix3x2f()));
	}

	@Override
	public PolyBuilder drawTriangles(PolyBuilder.Mode mode) {
		return BufferPolyBuilder.create((GuiGraphicsAccessor) this.graphics, VertexFormat.Mode.TRIANGLES, mode, this.alpha, this.stack.get(new Matrix3x2f()));
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

		Optional<ScreenRectangle> getScissorRegion() {
			if (this.region == null) {
				return Optional.empty();
			}
//			double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
			double windowHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

//			return new ScreenRectangle(
//					(int) (region.getX() * guiScale),
//					(int) ((windowHeight - (region.getY()+region.getHeight())) * guiScale), // lower left corner in opengl coordinate system
//					(int) (region.getWidth() * guiScale),
//					(int) (region.getHeight() * guiScale)
//			);

			return Optional.of(
					new ScreenRectangle(
						region.getX(),
						(int) (windowHeight - (region.getY()+region.getHeight())),
						region.getWidth(),
						region.getHeight())
			);
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
