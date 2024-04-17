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

import cc.cosmetica.kupe.api.QuadBuilder;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class BufferQuadBuilder implements QuadBuilder {
	public BufferQuadBuilder(BufferBuilder builder, Mode mode, @Nullable Matrix4f matrix4f) {
		this.builder = builder;
		this.builder.begin(GL11.GL_QUADS, mode.getFormat());
		this.matrix4f = matrix4f;
	}

	private final BufferBuilder builder;
	private final Matrix4f matrix4f;

	@Override
	public QuadBuilder vertex(double x, double y, double z) {
		if (this.matrix4f == null) {
			this.builder.vertex(x, y, z);
		} else {
			this.builder.vertex(this.matrix4f, (float)x, (float)y, (float)z);
		}
		return this;
	}

	@Override
	public QuadBuilder colour(float r, float g, float b, float a) {
		this.builder.color(r, g, b, a);
		return this;
	}

	@Override
	public QuadBuilder uv(float u, float v) {
		this.builder.uv(u, v);
		return this;
	}

	@Override
	public QuadBuilder lightmap(int u, int v) {
		this.builder.uv2(u, v);
		return this;
	}

	@Override
	public void build() {
		this.builder.end();
		BufferUploader.end(this.builder);
	}
}
