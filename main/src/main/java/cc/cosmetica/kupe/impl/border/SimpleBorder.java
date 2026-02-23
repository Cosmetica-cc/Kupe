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

package cc.cosmetica.kupe.impl.border;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.gui.Border;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Region;

public class SimpleBorder implements Border {
    public SimpleBorder(int size, int colour) {
        this.size = size;
        this.colour = colour;
    }

    private final int size;
    private final int colour;

    @Override
    public void paint(Canvas canvas, Region region, Style style) {
        if (this.size <= 0) return;

        float r = ((this.colour >> 16) & 0xFF) / 255.0f;
        float g = ((this.colour >> 8) & 0xFF) / 255.0f;
        float b = (this.colour & 0xFF) / 255.0f;

        // ======
        // |    |
        // ======

        // border with transparent centre
        canvas.drawRect( // top
                region.getX(), region.getY(),
                region.getWidth(), this.size,
                0,
                r, g, b);

        canvas.drawRect( // bottom
                region.getX(), region.getY() + region.getHeight() - this.size,
                region.getWidth(), this.size,
                0,
                r, g, b);

        canvas.drawRect( // left
                region.getX(), region.getY() + this.size,
                this.size, region.getHeight() - this.size * 2,
                0,
                r, g, b);

        canvas.drawRect( // right
                region.getX() + region.getWidth() - this.size, region.getY() + this.size,
                this.size, region.getHeight() - this.size * 2,
                -0.5f,
                r, g, b);
    }
}
