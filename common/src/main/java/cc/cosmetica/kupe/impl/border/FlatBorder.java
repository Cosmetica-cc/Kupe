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

package cc.cosmetica.kupe.impl.border;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.gui.Border;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Region;

public class FlatBorder implements Border {
    public FlatBorder(int[] sizes, int[] colours) {
        this.sizes = sizes;
        this.colours = new float[colours.length][3];

        for (int i : colours) {
            int borderColour = colours[i];

            float r = ((borderColour >> 16) & 0xFF) / 255.0f;
            float g = ((borderColour >> 8) & 0xFF) / 255.0f;
            float b = (borderColour & 0xFF) / 255.0f;

            this.colours[i][0] = r;
            this.colours[i][1] = g;
            this.colours[i][2] = b;
        }
    }

    private final int[] sizes;
    private final float[][] colours;

    @Override
    public void paint(Canvas canvas, Region region, Style style) {
        // ======
        // |    |
        // ======

        // border with transparent centre
        if (this.sizes[TOP] > 0)
            canvas.drawRect( // top
                region.getX(), region.getY(),
                region.getWidth(), this.sizes[TOP],
                -0.5f,
                this.colours[TOP][0], this.colours[TOP][1], this.colours[TOP][2]);

        if (this.sizes[BOTTOM] > 0)
            canvas.drawRect( // bottom
                region.getX(), region.getY() + region.getHeight() - this.sizes[BOTTOM],
                region.getWidth(), this.sizes[BOTTOM],
                -0.5f,
                this.colours[BOTTOM][0], this.colours[BOTTOM][1], this.colours[BOTTOM][2]);

        if (this.sizes[LEFT] > 0)
            canvas.drawRect( // left
                region.getX(), region.getY() + this.sizes[TOP],
                this.sizes[LEFT], region.getHeight() - this.sizes[TOP] - this.sizes[BOTTOM],
                -0.5f,
                this.colours[LEFT][0], this.colours[LEFT][1], this.colours[LEFT][2]);

        if (this.sizes[RIGHT] > 0)
            canvas.drawRect( // right
                region.getX() + region.getWidth() - this.sizes[RIGHT], region.getY() + this.sizes[TOP],
                this.sizes[RIGHT], region.getHeight() - this.sizes[TOP] - this.sizes[BOTTOM],
                -0.5f,
                this.colours[RIGHT][0], this.colours[RIGHT][1], this.colours[RIGHT][2]);
    }
}
