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

package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.impl.border.FlatBorder;
import cc.cosmetica.kupe.impl.border.SimpleBorder;

import java.util.Arrays;
import java.util.Optional;

/**
 * Renders a border behind the image.
 */
public interface Border {
    /**
     * Paint the border in the given region.
     * @param canvas the canvas to draw on.
     * @param region the padded region to draw on.
     * @param style the style of the component drawing this border.
     */
    void paint(Canvas canvas, Region region, Style style);

    /**
     * Create a simple border on all sides.
     * @param size The border size for this component. The border is placed at the edge of the padding, within the padding.
     * @param colour The border colour for this component. An RGB int.
     * @return the border.
     */
    static Optional<Border> create(int size, int colour) {
        return Optional.of(new SimpleBorder(size, colour));
    }

    /**
     * Create a flat border from the given config.
     * @param config the config to build a border from.
     * @return the created border.
     */
    static Optional<Border> create(BorderConfig config) {
        return Optional.of(new FlatBorder(config.sizes, config.colours));
    }

    int TOP = 0;
    int RIGHT = 1;
    int BOTTOM = 2;
    int LEFT = 3;

    class BorderConfig {
        private int[] sizes = new int[4];
        private int[] colours = new int[4];

        /**
         * Configure the border on a particular side.
         * @param side the side to set.
         * @param size the size of the border on this side.
         * @param colour the colour of the border on this side.
         * @throws IndexOutOfBoundsException for an invalid index.
         */
        public void set(int side, int size, int colour) {
            if (side < 0 || side > this.sizes.length)
                throw new IndexOutOfBoundsException("Side out of bounds: " + side);
            this.sizes[side] = size;
            this.colours[side] = colour;
        }

        /**
         * Create a border with two split colours.
         * @param size the border size.
         * @param colour1 the colour for top and left as an RGB int.
         * @param colour2 the colour for top and right as an RGB int.
         * @return the created Border Config.
         */
        public static BorderConfig split(int size, int colour1, int colour2) {
            BorderConfig result = new BorderConfig();
            Arrays.fill(result.sizes, size);
            result.colours[0] = result.colours[3] = colour1;
            result.colours[1] = result.colours[2] = colour2;
            return result;
        }
    }
}
