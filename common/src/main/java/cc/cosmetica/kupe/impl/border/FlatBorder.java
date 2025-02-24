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
import cc.cosmetica.kupe.api.QuadBuilder;
import cc.cosmetica.kupe.api.gui.Border;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Region;

public class FlatBorder implements Border {
    public FlatBorder(int[] sizes, int[] colours) {
        this.sizes = sizes;
        this.colours = new float[colours.length][3];
        this.corners = new float[colours.length][3];

        for (int i = 0; i < colours.length; i++) {
            int borderColour = colours[i];

            float r = ((borderColour >> 16) & 0xFF) / 255.0f;
            float g = ((borderColour >> 8) & 0xFF) / 255.0f;
            float b = (borderColour & 0xFF) / 255.0f;

            this.colours[i][0] = r;
            this.colours[i][1] = g;
            this.colours[i][2] = b;
        }

        // fill corner colours
        // these will only be used if blending is needed to reduce drawing stress.
        this.blend = false;

        // clockwise from top right
        for (int i = 0; i < this.corners.length; i++) {
            final int ii = (i + 1) & 3;

            // inner loop for component (r,g,b)
            for (int j = 0; j < 3; j++) {
                float c1 = this.colours[i][j];
                float c2 = this.colours[ii][j];

                if (c1 == c2 || this.sizes[i] == 0) {
                    this.corners[i][j] = c2;
                } else if (this.sizes[ii] == 0) {
                    this.corners[i][j] = c1;
                } else {
                    this.corners[i][j] = blend(c1, c2);
                    this.blend = true;
                }
            }
        }
    }

    private final int[] sizes;
    private final float[][] colours;
    private final float[][] corners;
    private boolean blend;

    @Override
    public void paint(Canvas canvas, Region region, Style style) {
        // O====O
        // |    |
        // O====O

        // border with transparent centre
        // draw in batch
        QuadBuilder builder = canvas.drawQuads(QuadBuilder.Mode.POSITION_COLOUR);

        // top
        if (this.sizes[TOP] > 0)
             addRect(builder, region.getX(), region.getY(), region.getWidth(), this.sizes[TOP], this.colours[TOP]);

        // bottom
        if (this.sizes[BOTTOM] > 0)
            addRect(builder, region.getX(), region.getY() + region.getHeight() - this.sizes[BOTTOM],
                region.getWidth(), this.sizes[BOTTOM],
                this.colours[BOTTOM]);

        // left
        if (this.sizes[LEFT] > 0)
            addRect(
                builder, region.getX(), region.getY() + this.sizes[TOP],
                this.sizes[LEFT], region.getHeight() - this.sizes[TOP] - this.sizes[BOTTOM],
                this.colours[LEFT]);

        // right
        if (this.sizes[RIGHT] > 0)
            addRect(
                builder, region.getX() + region.getWidth() - this.sizes[RIGHT], region.getY() + this.sizes[TOP],
                this.sizes[RIGHT], region.getHeight() - this.sizes[TOP] - this.sizes[BOTTOM],
                this.colours[RIGHT]);

        // corners
        if (this.blend){
            // top right
            if (this.sizes[TOP] > 0 || this.sizes[RIGHT] > 0) {
                addRect(builder, region.getX() + region.getWidth() - this.sizes[RIGHT], region.getY(),
                        this.sizes[RIGHT], this.sizes[TOP],
                        this.corners[0]);
            }
            // bottom right
            if (this.sizes[BOTTOM] > 0 || this.sizes[RIGHT] > 0) {
                addRect(builder, region.getX() + region.getWidth() - this.sizes[RIGHT], region.getY() + region.getHeight() - this.sizes[BOTTOM],
                        this.sizes[RIGHT], this.sizes[BOTTOM],
                        this.corners[1]);
            }
            // bottom left
            if (this.sizes[BOTTOM] > 0 || this.sizes[LEFT] > 0) {
                addRect(builder, region.getX(), region.getY() + region.getHeight() - this.sizes[BOTTOM],
                        this.sizes[LEFT], this.sizes[BOTTOM],
                        this.corners[2]);
            }
            // top left
            if (this.sizes[TOP] > 0 || this.sizes[LEFT] > 0) {
                addRect(builder, region.getX(), region.getY(),
                        this.sizes[LEFT], this.sizes[TOP],
                        this.corners[3]);
            }
        }

        // finish rendering and upload
        builder.build();
    }

    private static void addRect(QuadBuilder builder, int x0, int y0, int width, int height, float[] colour) {
        int x1 = x0 + width;
        int y1 = y0 + height;

        builder.vertex((float)x0, (float)y1, -0.5f).colour(colour[0], colour[1], colour[2], 1).endVertex();
        builder.vertex((float)x1, (float)y1, -0.5f).colour(colour[0], colour[1], colour[2], 1).endVertex();
        builder.vertex((float)x1, (float)y0, -0.5f).colour(colour[0], colour[1], colour[2], 1).endVertex();
        builder.vertex((float)x0, (float)y0, -0.5f).colour(colour[0], colour[1], colour[2], 1).endVertex();
    }

    /**
     * Blend two colour components.
     */
    private static float blend(float a, float b) {
        // n.b. squaring assumes gamma of 2, but it's typically 2.2
        // see also: https://stackoverflow.com/questions/726549/algorithm-for-additive-color-mixing-for-rgb-values
        return (float) Math.sqrt((a * a + b * b) * 0.5);
    }
}
