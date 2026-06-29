package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.PolyBuilder;

public final class NoOpPolybuilder implements PolyBuilder {
    private NoOpPolybuilder() {
    }

    private boolean current = false;

    @Override
    public PolyBuilder vertex(double x, double y, double z) {
        if (this.current) {
            throw new IllegalStateException("Cannot create new vertex without ending previous vertex!");
        }

        this.current = true;
        return this;
    }

    @Override
    public PolyBuilder colour(float r, float g, float b, float a) {
        return this;
    }

    @Override
    public PolyBuilder uv(float u, float v) {
        return this;
    }

    @Override
    public PolyBuilder lightmap(int u, int v) {
        return this;
    }

    @Override
    public PolyBuilder endVertex() {
        this.current = false;
        return this;
    }

    @Override
    public void build() {
        if (this.current) {
            throw new IllegalStateException("Cannot build polygons: vertex has not been ended.");
        }
    }

    public static final PolyBuilder INSTANCE = new NoOpPolybuilder();
}
