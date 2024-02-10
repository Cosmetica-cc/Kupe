package cc.cosmetica.kupe.api;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.lwjgl.system.CallbackI;

/**
 * It builds the quads.
 */
public interface QuadBuilder {
	default QuadBuilder vertex(double x, double y) {
		return this.vertex(x, y, 0.0);
	}

	QuadBuilder vertex(double x, double y, double z);

	QuadBuilder colour(int r, int g, int b, int a);

	QuadBuilder uv(float u, float v);

	/**
	 * The second lot of uv coordinates. Corresponds to the lightmap.
	 * @param u the x position in the lightmap texture.
	 * @param v the y position in the lightmap texture.
	 * @return
	 */
	QuadBuilder lightmap(float u, float v);

	/**
	 * Build this quad and dispatch it to be rendered.
	 */
	void build();

	/**
	 * The modes this QuadBuilder can run in.
	 */
	enum Mode {
		POSITION(DefaultVertexFormat.POSITION, 1),
		POSITION_COLOUR(DefaultVertexFormat.POSITION_COLOR, 2),
		POSITION_TEXTURE(DefaultVertexFormat.POSITION_TEX, 2),
		POSITION_COLOUR_TEXTURE(DefaultVertexFormat.POSITION_COLOR_TEX, 3),
		POSITION_COLOUR_LIGHTMAP(DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, 3),
		POSITION_COLOUR_TEXTURE_LIGHTMAP(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, 4);

		Mode(VertexFormat format, int size) {
			this.format = format;
			this.size = size;
		}

		private final VertexFormat format;
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
		 * Get the number of parameters this vertex mode has.
		 * @return the number of things that need to be specified for vertices in this mode.
		 */
		public int getSize() {
			return this.size;
		}
	}
}
