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

import cc.cosmetica.kupe.api.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Implementation of Canvas.
 * It seems somewhat wasteful to create this every frame. Perhaps if the posestack doesn't change we can cache and just
 * update tickDelta.
 */
public class PoseCanvas implements Canvas {
	public PoseCanvas(PoseStack stack, Minecraft minecraft, Context context, float tickDelta) {
		this.stack = stack;
		this.kupeStack = new KupeStack();
		this.minecraft = minecraft;
		this.context = context;
		this.tickDelta = tickDelta;
	}

	private final PoseStack stack;
	private final MatrixStack kupeStack;
	private final Minecraft minecraft;
	private final Context context;
	private final float tickDelta;

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
	}

	@Override
	public void setTransparency(float transparency) {
		RenderSystem.enableBlend();
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, transparency);
	}

	@Override
	public void drawCenteredText(Text text, int x, int y, int colour) {
		GuiComponent.drawCenteredString(this.stack, this.minecraft.font, text.toMinecraftComponent(), x, y, colour);
	}

	@Override
	public void drawText(Text text, int x, int y, int colour) {
		GuiComponent.drawString(this.stack, this.minecraft.font, text.toMinecraftComponent(), x, y, colour);
	}

	@Override
	public void drawRect(int x0, int y0, int x1, int y1, float r, float g, float b) {
		RenderSystem.disableTexture();
		BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferBuilder.vertex(x0, y1, 0.0D).color(r, g, b, 1.0F).endVertex();
		bufferBuilder.vertex(x1, y1, 0.0D).color(r, g, b, 1.0F).endVertex();
		bufferBuilder.vertex(x1, y0, 0.0D).color(r, g, b, 1.0F).endVertex();
		bufferBuilder.vertex(x0, y0, 0.0D).color(r, g, b, 1.0F).endVertex();

		bufferBuilder.end();
		BufferUploader.end(bufferBuilder);
	}

	@Override
	public void drawTexture(int x0, int y0, int x1, int y1, float z, ResourceLocation texture) {
		RenderSystem.enableTexture();
		Minecraft.getInstance().getTextureManager().bind(texture);
		BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
		Matrix4f matrix4f = this.stack.last().pose();

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferBuilder.vertex(matrix4f, (float)x0, (float)y1, z).uv(0, 1).endVertex();
		bufferBuilder.vertex(matrix4f, (float)x1, (float)y1, z).uv(1, 1).endVertex();
		bufferBuilder.vertex(matrix4f, (float)x1, (float)y0, z).uv(1, 0).endVertex();
		bufferBuilder.vertex(matrix4f, (float)x0, (float)y0, z).uv(0, 0).endVertex();

		bufferBuilder.end();
		BufferUploader.end(bufferBuilder);
	}

	@Override
	public QuadBuilder drawQuads(QuadBuilder.Mode mode) {
		return new BufferQuadBuilder(Tesselator.getInstance().getBuilder(), mode, this.stack.last().pose());
	}

	@Override
	public void renderMinecraftComponent(Widget component, int mouseX, int mouseY) {
		component.render(this.stack, mouseX, mouseY, this.tickDelta);
	}

	/// Impl Only

	/**
	 * Get the minecraft pose stack.
	 * @return the pose stack.
	 */
	@LeavesSandbox
	public PoseStack getPoseStack() {
		return this.stack;
	}

	class KupeStack implements MatrixStack {
		@Override
		public void push() {
			PoseCanvas.this.stack.pushPose();
		}

		@Override
		public void pop() {
			PoseCanvas.this.stack.popPose();
		}

		@Override
		public PoseStack toMinecraftStack() {
			return PoseCanvas.this.stack;
		}
	}
}
