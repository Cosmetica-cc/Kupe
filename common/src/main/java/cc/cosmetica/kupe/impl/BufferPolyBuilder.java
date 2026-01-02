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
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class BufferPolyBuilder implements PolyBuilder {
	private BufferPolyBuilder(Tesselator tesselator, VertexFormat.Mode shape, Mode mode, @Nullable Matrix4f matrix4f) {
		this.builder = tesselator.begin(shape, mode.getFormat());
		this.matrix4f = matrix4f;
	}

	private final BufferBuilder builder;
	private final Matrix4f matrix4f;
	private boolean clean = true;

	@Override
	public PolyBuilder vertex(double x, double y, double z) {
		if (!this.clean) {
			throw new IllegalStateException("Cannot create new vertex without ending previous vertex!");
		}
		this.clean = false;

		if (this.matrix4f == null) {
			this.builder.addVertex((float)x, (float)y, (float)z);
		} else {
			this.builder.addVertex(this.matrix4f, (float)x, (float)y, (float)z);
		}
		return this;
	}

	@Override
	public PolyBuilder colour(float r, float g, float b, float a) {
		this.builder.setColor(r, g, b, a);
		return this;
	}

	@Override
	public PolyBuilder uv(float u, float v) {
		this.builder.setUv(u, v);
		return this;
	}

	@Override
	public PolyBuilder lightmap(int u, int v) {
		this.builder.setUv2(u, v);
		return this;
	}

	@Override
	public PolyBuilder endVertex() {
		this.clean = true;
		return this;
	}

	@Override
	public void build() {
		BufferUploader.drawWithShader(this.builder.buildOrThrow());
	}

	public static BufferPolyBuilder create(Tesselator tesselator, VertexFormat.Mode shape, Mode mode, @Nullable Matrix4f matrix4f) {
		if (mode == Mode.POSITION_COLOUR_TEXTURE) {
			return new PositionColourTexAdapter(tesselator, shape, matrix4f);
		} else {
			return new BufferPolyBuilder(tesselator, shape, mode, matrix4f);
		}
	}

	private static class PositionColourTexAdapter extends BufferPolyBuilder {
		private PositionColourTexAdapter(Tesselator tesselator, VertexFormat.Mode shape, @Nullable Matrix4f matrix4f) {
			super(tesselator, shape, Mode.POSITION_COLOUR_TEXTURE, matrix4f);
		}

		@Nullable
		private Vector4f colour;

		@Override
		public PolyBuilder uv(float u, float v) {
			if (this.colour == null) {
				throw new IllegalStateException("Must declare Colour before UV for POSITION_COLOUR_TEXTURE");
			}

			// reinterpret order to match minecraft order (Position-Tex-Colour)
			super.uv(u, v);
			super.colour(this.colour.x, this.colour.y, this.colour.z, this.colour.w);
			return this;
		}

		@Override
		public PolyBuilder colour(float r, float g, float b, float a) {
			this.colour = new Vector4f(r, g, b, a);
			return this;
		}
	}
}
