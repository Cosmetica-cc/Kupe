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

import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * A layout which positions components in a grid, left to right.
 * Components will wrap to the first column and next row once space runs out.
 */
public class Grid extends Component {
    public Grid(Component... components) {
        this.components = Arrays.asList(components);
    }

    private final List<Component> components;

    @Override
    public List<Component> build() {
        return this.components;
    }

    @Override
    public Dimensions minimumSize(List<? extends SizedElement> children, Margins padding, int vw, int vh) {
        return Dimensions.NONE;
    }

    @Override
    public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
        return Dimensions.NONE;
    }

    private Dimensions size(List<? extends SizedElement> children, Function<? extends SizedElement, Dimensions> getDimensions, Margins padding) {
        final int columns = this.getStyle().get(COLUMNS);
        int column = 0;
        return null;
    }

    @Override
    public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children, Context context) {
        // this is gonna be fun
    }

    /**
     * Determine the number of columns to divide the grid into. Leave as 0 for auto, where the largest item determines the columns size.
     */
    public static final Style.Property<Integer> COLUMNS = new Style.Property<>("columns", 0, false);
    /**
     * Extra space in pixels between each column.
     */
    public static final Style.Property<Integer> COLUMN_GAP = new Style.Property<>("columnGap", 0, false);
    /**
     * Extra space in pixels between each row.
     */
    public static final Style.Property<Integer> ROW_GAP = new Style.Property<>("rowGap", 0, false);
    /**
     * How are the rows spaced vertically?
     */
    public static final Style.Property<Justify> JUSTIFY_ROWS = new Style.Property<>("justifyRows", Justify.START, false);
    /**
     * How are the columns distributed horizontally?
     */
    public static final Style.Property<Justify> JUSTIFY_COLUMNS = new Style.Property<>("justifyColumns", Justify.CENTRE, false);
    /**
     * Align each component in its grid space horizontally.
     */
    public static final Style.Property<Align> ALIGN_HORIZONTAL = new Style.Property<>("alignHorizontal", Align.CENTRE, false);
    /**
     * Align each component in its grid space vertically.
     */
    public static final Style.Property<Align> ALIGN_VERTICAL = new Style.Property<>("alignHorizontal", Align.CENTRE, false);
}
