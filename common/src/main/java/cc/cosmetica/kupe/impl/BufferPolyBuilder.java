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

import cc.cosmetica.kupe.api.PolyBuilder;
import cc.cosmetica.kupe.mixin.GuiGraphicsAccessor;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class BufferPolyBuilder implements PolyBuilder {
	private BufferPolyBuilder(GuiGraphicsAccessor graphics, VertexFormat.Mode shape, TextureSetup textureSetup, float alpha, @Nullable Matrix3x2f matrix4f, @Nullable ScreenRectangle scissor, int uvFlags) {
		this.graphics = graphics;
		this.triangles = shape == VertexFormat.Mode.TRIANGLES;
		this.alpha = alpha;
		this.matrix = matrix4f;
		this.textureSetup = textureSetup;
		this.scissor = scissor;
		this.uvFlags = uvFlags;
	}

	private final GuiGraphicsAccessor graphics;
	private final boolean triangles;
	private final float alpha;
	private final Matrix3x2f matrix;
	private final TextureSetup textureSetup;
	private final ScreenRectangle scissor;
	private final int uvFlags;

	private final List<Vertex> vertices = new ArrayList<>();
	private Vertex current;
	private boolean appliedAlpha;

	private float x0, y0, x1, y1;

	@Override
	public PolyBuilder vertex(double x, double y, double z) {
		if (this.current != null) {
			throw new IllegalStateException("Cannot create new vertex without ending previous vertex!");
		}

		this.current = new Vertex(this.matrix, (float)x, (float)y, (float)z);
		if (this.vertices.isEmpty()) {
			this.x0 = this.x1 = this.current.x;
			this.y0 = this.y1 = this.current.y;
		} else {
			if (this.current.x < x0) {
				this.x0 = this.current.x;
			} else if (this.current.x > x1) {
				this.x1 = this.current.x;
			}

			if (this.current.y < y0) {
				this.y0 = this.current.y;
			} else if (this.current.y > y1) {
				this.y1 = this.current.y;
			}
		}

		this.appliedAlpha = false;
		return this;
	}

	@Override
	public PolyBuilder colour(float r, float g, float b, float a) {
		this.current.colour = PoseCanvas.packColour(r, g, b, a * this.alpha);
		this.appliedAlpha = true;
		return this;
	}

	@Override
	public PolyBuilder uv(float u, float v) {
		this.current.u = u;
		this.current.v = v;
		return this;
	}

	@Override
	public PolyBuilder lightmap(int u, int v) {
		this.current.u2 = u;
		this.current.v2 = v;
		return this;
	}

	@Override
	public PolyBuilder endVertex() {
		if (!this.appliedAlpha) {
			this.current.colour = PoseCanvas.packColour(1, 1, 1, this.alpha);
		}
		this.vertices.add(this.current);
		// duplicate final vertex for triangles
		if (this.triangles && ((this.vertices.size() + 1) & 3) == 0) {
			this.vertices.add(this.current);
		}
		this.current = null;
		return this;
	}

	@Override
	public void build() {
		if (this.current != null) {
			throw new IllegalStateException("Cannot build polygons: vertex has not been ended.");
		}

		GuiRenderState state = this.graphics.getGuiRenderState();
		state.submitGuiElement(new BufferPolyVertexList(this, (this.uvFlags & 1) == 1, (this.uvFlags & 2) == 2));
	}

	public static BufferPolyBuilder create(GuiGraphicsAccessor graphics, VertexFormat.Mode shape, Mode mode, @Nullable GpuTextureView texture, float alpha, @Nullable Matrix3x2f matrix4f, @Nullable ScreenRectangle scissor) {
		TextureSetup setup = TextureSetup.noTexture();
		int textureUV = 0;

		switch (mode) {
		case POSITION:
		case POSITION_COLOUR:
			break;
		case POSITION_TEXTURE:
		case POSITION_COLOUR_TEXTURE:
			if (texture == null) {
				throw new IllegalArgumentException("Building polygons with texture but no texture set");
			}
			setup = TextureSetup.singleTexture(texture, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
			textureUV = 1;
			break;
		case POSITION_COLOUR_LIGHTMAP:
			setup = TextureSetup.singleTextureWithLightmap(null, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
			textureUV = 2;
			break;
		case POSITION_COLOUR_TEXTURE_LIGHTMAP:
			if (texture == null) {
				throw new IllegalArgumentException("Building polygons with texture but no texture set");
			}
			setup = TextureSetup.singleTextureWithLightmap(texture, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
			textureUV = 3;
			break;
        }
		return new BufferPolyBuilder(graphics, shape, setup, alpha, matrix4f, scissor, textureUV);
	}

	private static class Vertex {
		Vertex(@Nullable Matrix3x2f matrix, float x, float y, float z) {
			if (matrix == null) {
				this.x = x;
				this.y = y;
			} else {
				Vector2f transformed = matrix.transformPosition(x, y, new Vector2f());
				this.x = transformed.x;
				this.y = transformed.y;
			}
			this.z = z;
		}

		final float x;
		final float y;
		final float z;
		int colour = 0xFFFFFFFF;
		float u;
		float v;
		int u2;
		int v2;
	}

	private static class BufferPolyVertexList implements GuiElementRenderState {
		BufferPolyVertexList(BufferPolyBuilder builder, boolean texture, boolean lightmap) {
			this.vertices = builder.vertices;
			this.textureSetup = builder.textureSetup;
			this.scissor = builder.scissor;
			this.texture = texture;
			this.lightmap = lightmap;
			this.bounds = new ScreenRectangle((int) builder.x0, (int) builder.y0, (int) (builder.x1 - builder.x0), (int) (builder.y1 - builder.y0));
		}

		private final @NotNull List<Vertex> vertices;
		private final @NotNull TextureSetup textureSetup;
		private final @Nullable ScreenRectangle scissor;
		private final ScreenRectangle bounds;
		private final boolean texture, lightmap;

		@Override
		public void buildVertices(VertexConsumer consumer) {
			if (texture) {
				if (lightmap) {
					for (Vertex vertex : this.vertices) {
						consumer.addVertex(vertex.x, vertex.y, 0)
								.setColor(vertex.colour)
								.setUv(vertex.u, vertex.v)
								.setUv2(vertex.u2, vertex.v2);
					}
				} else {
					for (Vertex vertex : this.vertices) {
						consumer.addVertex(vertex.x, vertex.y, 0)
								.setColor(vertex.colour)
								.setUv(vertex.u, vertex.v);
					}
				}
			} else if (lightmap) {
				for (Vertex vertex : this.vertices) {
					consumer.addVertex(vertex.x, vertex.y, 0)
							.setColor(vertex.colour)
							.setUv2(vertex.u2, vertex.v2);
				}
			} else {
				for (Vertex vertex : this.vertices) {
					consumer.addVertex(vertex.x, vertex.y, 0)
							.setColor(vertex.colour);
				}
			}
		}

		@Override
		public RenderPipeline pipeline() {
			// FIXME handle colour-lightmap.
			// TODO check if texture-lightmap is correct
			if (this.lightmap && this.texture) {
				return RenderPipelines.TEXT_SEE_THROUGH;
			}
			return this.texture ? RenderPipelines.GUI_TEXTURED : RenderPipelines.GUI;
		}

		@Override
		public TextureSetup textureSetup() {
			return this.textureSetup;
		}

		@Override
		public @Nullable ScreenRectangle scissorArea() {
			return this.scissor;
		}

		@Override
		public @Nullable ScreenRectangle bounds() {
			return this.bounds;
		}
	}
}
