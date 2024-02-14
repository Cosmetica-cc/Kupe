package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.impl.LeavesSandbox;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

/**
 * It builds the quads.
 */
public interface QuadBuilder {
	default QuadBuilder vertex(double x, double y) {
		return this.vertex(x, y, 0.0);
	}

	QuadBuilder vertex(double x, double y, double z);

	QuadBuilder colour(float r, float g, float b, float a);

	/**
	 * The first lot of uv coordinates. Corresponds to the texture coordinates.
	 * @param u the x position in the texture, between 0 and 1.
	 * @param v the y position in the texture, between 0 and 1.
	 * @return this.
	 */
	QuadBuilder uv(float u, float v);

	/**
	 * The second lot of uv coordinates. Corresponds to the lightmap.
	 * @param u the x position in the lightmap texture.
	 * @param v the y position in the lightmap texture.
	 * @return this.
	 */
	QuadBuilder lightmap(int u, int v);

	/**
	 * Build this quad and dispatch it to be rendered.
	 */
	void build();

	/**
	 * The modes this QuadBuilder can run in.
	 */
	enum Mode {
		POSITION(DefaultVertexFormat.POSITION, false, 1),
		POSITION_COLOUR(DefaultVertexFormat.POSITION_COLOR, false, 2),
		POSITION_TEXTURE(DefaultVertexFormat.POSITION_TEX, true, 2),
		POSITION_COLOUR_TEXTURE(DefaultVertexFormat.POSITION_COLOR_TEX, false, 3),
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

		/**
		 * Get the number of parameters this vertex mode has.
		 * @return the number of things that need to be specified for vertices in this mode.
		 */
		public int getSize() {
			return this.size;
		}
	}
}
