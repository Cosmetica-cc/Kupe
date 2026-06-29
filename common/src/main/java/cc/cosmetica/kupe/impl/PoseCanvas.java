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
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.impl.fakeplayer.FakePlayerGuiRenderer;
import cc.cosmetica.kupe.impl.fakeplayer.FakePlayerRenderer;
import cc.cosmetica.kupe.mixin.GuiGraphicsAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.ColoredRectangleRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of Canvas.
 * It seems somewhat wasteful to create this every frame. Perhaps if the posestack doesn't change we can cache and just
 * update tickDelta.
 */
public class PoseCanvas implements Canvas, ModernCanvas {
	public PoseCanvas(GuiGraphicsExtractor graphics, Minecraft minecraft, Context context, float tickDelta) {
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

	final GuiGraphicsExtractor graphics;
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
			this.graphics.tooltip(
					Minecraft.getInstance().font,
					Minecraft.getInstance().font.split(text.toMinecraftComponent(), splitWidth)
							.stream().map(ClientTooltipComponent::create)
							.collect(Collectors.toList()),
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
		FormattedCharSequence sequence = text.toMinecraftComponent().getVisualOrderText();
		x -= this.minecraft.font.width(sequence) / 2;

		this.drawCharSequenceInternal(
				this.minecraft.font,
				text.toMinecraftComponent().getVisualOrderText(),
				x,
				y,
				colour,
				true
		);
	}

	@Override
	public void drawText(Text text, int x, int y, int colour) {
		this.drawCharSequenceInternal(
				this.minecraft.font,
				text.toMinecraftComponent().getVisualOrderText(),
				x,
				y,
				colour,
				true
		);
	}

	@Override
	public void drawCharSequence(Font font, FormattedCharSequence sequence, int x, int y, int colour) {
		// for consistency with older GUI versions. I think shadow on everything is visually nicer on new minecraft in many case, though.
		this.drawCharSequenceInternal(font, sequence, x, y, colour, false);
	}

	private void drawCharSequenceInternal(Font font, FormattedCharSequence sequence, int x, int y, int colour, boolean shadow) {
		GuiGraphicsAccessor graphics = (GuiGraphicsAccessor) this.graphics;
		GuiRenderState guiRenderState = graphics.getGuiRenderState();
		colour = addAlpha(colour, this.alpha);

		ScreenRectangle scissor = this.scissorStack.getScissorRegion().orElse(null);
		if (scissor != null && (scissor.height() == 0 || scissor.width() == 0)) {
			return;
		}

		guiRenderState.addText(new GuiTextRenderState(
				font,
				sequence,
				new Matrix3x2f(this.stack),
				x,
				y,
				colour,
				0,
				shadow,
				false,
				scissor
		));
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

	static int addAlpha(int rgb, float alpha) {
		alpha = Math.max(0f, Math.min(1f, alpha));
		int a = Math.round(alpha * 255f) & 0xFF;
		return (a << 24) | (rgb & 0x00FFFFFF);
	}

	@Override
	public void drawRect(int x0, int y0, int width, int height, float z, float r, float g, float b) {
		GuiGraphicsAccessor graphics = (GuiGraphicsAccessor) this.graphics;
		int colour = packColour(r, g, b, this.alpha);

		ScreenRectangle scissor = this.scissorStack.getScissorRegion().orElse(null);
		if (scissor != null && (scissor.height() == 0 || scissor.width() == 0)) {
			return;
		}

		graphics.getGuiRenderState().addGuiElement(new ColoredRectangleRenderState(
				RenderPipelines.GUI,
				TextureSetup.noTexture(),
				this.stack.get(new Matrix3x2f()),
				x0, y0,
				x0 + width, y0 + height,
				colour,
				colour,
				scissor,
				new ScreenRectangle(x0, y0, width, height)
		));
	}

	@Override
	public void drawTexture(int x0, int y0, int width, int height, float z, ResourceKey texture) {
		GuiGraphicsAccessor graphics = (GuiGraphicsAccessor) this.graphics;
		int colour = packColour(1, 1, 1, this.alpha);
		this.setTexture(texture);
		assert this.texture != null;

		ScreenRectangle scissor = this.scissorStack.getScissorRegion().orElse(null);
		if (scissor != null && (scissor.height() == 0 || scissor.width() == 0)) {
			return;
		}

		graphics.getGuiRenderState().addGuiElement(new BlitRenderState(
				RenderPipelines.GUI_TEXTURED,
				// TODO add filter mode configuration on Canvas in all Kupe minecraft versions
				TextureSetup.singleTexture(this.texture, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST)),
				this.stack.get(new Matrix3x2f()),
				x0, y0,
				x0 + width, y0 + height,
				0,
				1,
				0,
				1,
				colour,
				scissor,
				new ScreenRectangle(x0, y0, width, height)
		));
	}

	@Override
	public PolyBuilder drawQuads(PolyBuilder.Mode mode) {
		ScreenRectangle scissor = this.scissorStack.getScissorRegion().orElse(null);
		if (scissor != null && (scissor.height() == 0 || scissor.width() == 0)) {
			return NoOpPolybuilder.INSTANCE;
		}

		return BufferPolyBuilder.create((GuiGraphicsAccessor) this.graphics, BufferPolyBuilder.Shape.QUADS, mode, this.texture, this.alpha, this.stack.get(new Matrix3x2f()), scissor);
	}

	@Override
	public PolyBuilder drawTriangles(PolyBuilder.Mode mode) {
		ScreenRectangle scissor = this.scissorStack.getScissorRegion().orElse(null);
		if (scissor != null && (scissor.height() == 0 || scissor.width() == 0)) {
			return NoOpPolybuilder.INSTANCE;
		}

		return BufferPolyBuilder.create((GuiGraphicsAccessor) this.graphics, BufferPolyBuilder.Shape.TRIANGLES, mode, this.texture, this.alpha, this.stack.get(new Matrix3x2f()), scissor);
	}

	private static final Vector3f XP = new Vector3f(1, 0, 0);
	private static final Vector3f ZP = new Vector3f(0, 0, 1);

	@Override
	public void renderFakePlayer(GUIPlayer player, FakePlayerRenderer renderer, Region region, int left, int top, float extraScale, float lookX, float lookY) {
		ScreenRectangle scissor = this.scissorStack.getScissorRegion().orElse(null);
		if (scissor != null && (scissor.height() == 0 || scissor.width() == 0)) {
			return;
		}

		// InventoryScreen#renderEntityInInventoryFollowsMouse
		float l = (float)Math.atan(lookY / 40.0F);

		Quaternionf zRotation = new Quaternionf(new AxisAngle4f((float)Math.toRadians(180.0F), ZP));
		Quaternionf xRotation = new Quaternionf(new AxisAngle4f((float)Math.toRadians(l * 20.0F), XP));
		zRotation.mul(xRotation);

		xRotation.conjugate();

		// render
		FakePlayerGuiRenderer.State state = new FakePlayerGuiRenderer.State(
				renderer, player, extraScale, xRotation, zRotation, this.context, left, top, lookX, lookY,
				region, 1.0f, scissor,
				new ScreenRectangle(region.getX(), region.getY(), region.getWidth(), region.getHeight()));
		GuiGraphicsAccessor graphics = (GuiGraphicsAccessor) this.graphics;
		graphics.getGuiRenderState().addPicturesInPictureState(state);
	}

	@Override
	public void renderMinecraftComponent(net.minecraft.client.gui.components.Renderable component, int mouseX, int mouseY) {
//		ScissorStack stack = this.scissorStack;
		Optional<Region> rect = this.getScissor();
		if (rect.isPresent()) {
			Region region = rect.get();
//			this.graphics.enableScissor(region.left(), region.top(), region.right(), region.bottom());
			this.graphics.enableScissor(region.getX(), region.getY(), region.getEndX(), region.getEndY());
		}
//		if (stack.prevNode != null) {
//			mouseY += (int)stack.scrollY;
//		}
		component.extractRenderState(this.graphics, mouseX, mouseY, this.tickDelta);
		if (rect.isPresent()) {
			this.graphics.disableScissor();
		}
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
			Region region = this.region;

			return Optional.of(
					new ScreenRectangle(
						region.getX(),
						region.getY(),
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
