package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.QuadBuilder;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.Tesselator;
import org.lwjgl.opengl.GL11;

public class TesselatorQuadBuilder implements QuadBuilder {
	public TesselatorQuadBuilder(Tesselator tesselator, Mode mode) {
		this.builder = tesselator.getBuilder();
		this.builder.begin(GL11.GL_QUADS, mode.getFormat());
	}

	private final BufferBuilder builder;

	@Override
	public QuadBuilder vertex(double x, double y, double z) {
		this.builder.vertex(x, y, z);
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
