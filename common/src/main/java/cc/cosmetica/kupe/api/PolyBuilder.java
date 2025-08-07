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

package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.impl.LeavesSandbox;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

/**
 * Draw polygons. Typically used for {@linkplain Canvas#drawQuads(Mode) quadrilaterals}.
 */
public interface PolyBuilder {
	default PolyBuilder vertex(double x, double y) {
		return this.vertex(x, y, 0.0);
	}

	PolyBuilder vertex(double x, double y, double z);

	PolyBuilder colour(float r, float g, float b, float a);

	/**
	 * The first lot of uv coordinates. Corresponds to the texture coordinates.
	 * @param u the x position in the texture, between 0 and 1.
	 * @param v the y position in the texture, between 0 and 1.
	 * @return this.
	 */
	PolyBuilder uv(float u, float v);

	/**
	 * The second lot of uv coordinates. Corresponds to the lightmap.
	 * @param u the x position in the lightmap texture.
	 * @param v the y position in the lightmap texture.
	 * @return this.
	 */
	PolyBuilder lightmap(int u, int v);

	/**
	 * End the current vertex and prepare for the next.
	 * @return this.
	 */
	PolyBuilder endVertex();

	/**
	 * Build these quads and dispatch them to be rendered.
	 */
	void build();

	/**
	 * The modes this QuadBuilder can run in.
	 */
	enum Mode {
		POSITION(DefaultVertexFormat.POSITION, false, 1),
		POSITION_COLOUR(DefaultVertexFormat.POSITION_COLOR, false, 2),
		POSITION_TEXTURE(DefaultVertexFormat.POSITION_TEX, true, 2),
		POSITION_COLOUR_TEXTURE(DefaultVertexFormat.POSITION_COLOR_TEX, true, 3),
		POSITION_COLOUR_LIGHTMAP(DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, false, 3),
		POSITION_COLOUR_TEXTURE_LIGHTMAP(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, true, 4);

		Mode(VertexFormat format, boolean texture, int size) {
			this.format = format;
			this.texture = texture;
			this.size = size;
		}

		private final VertexFormat format;
		private final boolean texture;
		private final int size;

		/**
		 * Get the minecraft VertexFormat corresponding to this QuadBuilder mode.
		 * @return the minecraft {@link VertexFormat} corresponding to this mode.
		 */
		@LeavesSandbox
		public VertexFormat getFormat() {
			return this.format;
		}

		/**
		 * Get the number of components to this vertex.
		 * @return the number of components to this vertex.
		 */
		public int getSize() {
			return this.size;
		}

		/**
		 * Apply the shader this Mode is associated with.
		 */
		public void applyShader() {
			// Does not directly apply to 1.16.5. Set texture instead.
			if (this.texture) {
				RenderSystem.enableTexture();
			} else {
				RenderSystem.disableTexture();
			}
		}
	}
}
