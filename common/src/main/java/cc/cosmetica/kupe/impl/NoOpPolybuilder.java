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
