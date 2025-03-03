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

package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Function;

/**
 * A layout which positions components in a grid, left to right.
 * Components will wrap to the first column and next row once space runs out.
 */
public class Grid extends AbstractScrollContainer {
    public Grid(Component... components) {
        this.components = Arrays.asList(components);
    }

    private final List<Component> components;

    @Override
    protected boolean hasVerticalOverflow() {
        // overflow is always vertical for Grid.
        return this.overflow;
    }

    @Override
    public List<Component> build() {
        return this.components;
    }

    // ==========
    //   SIZING
    // ==========

    @Override
    public Dimensions minimumSize(List<? extends SizedElement> children, Margins padding, int vw, int vh) {
        return this.size(children, SizedElement::getMinimumSize, padding, vw, vh);
    }

    @Override
    public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
        return this.size(children, SizedElement::getPreferredSize, padding, context.getViewWidth(), context.getViewHeight());
    }

    private Dimensions size(List<? extends SizedElement> children, Function<SizedElement, Dimensions> getDimensions, Margins padding, int vw, int vh) {
        int elementWidth = 0;
        int elementHeight = 0;

        // find largest size
        for (SizedElement element : children) {
            Dimensions dimensions = getDimensions.apply(element);

            if (dimensions.getHeight() > elementHeight) {
                elementHeight = dimensions.getHeight();
            }
            if (dimensions.getWidth() > elementWidth) {
                elementWidth = dimensions.getWidth();
            }
        }

        final OptionalInt fixedWidth = this.getStyle().get(CommonProperties.WIDTH).apply(vw, vh, 0, 0);
        final int columnGap = this.getStyle().get(COLUMN_GAP);

        int columns = this.getStyle().get(COLUMNS);

        // constrain by the number of elements that can fit into the fixed width
        if (elementWidth > 0 && fixedWidth.isPresent()) {
            final int W = fixedWidth.getAsInt();

            // the proof is left as an exercise to the reader
            // (w+g)(n-1)+w=W
            int fixedWidthColumns = (W-elementWidth)/(elementWidth+columnGap) + 1;

            if (columns == 0 || fixedWidthColumns < columns) {
                columns = fixedWidthColumns;
            }
        }

        if (columns == 0) {
            final int n = Math.max(1,children.size());
            return new Dimensions(elementWidth*n + columnGap * (n-1), elementHeight);
        }

        int rows = Math.max(1, (int)Math.ceil((double) children.size() / columns));
        final int rowGap = this.getStyle().get(ROW_GAP);
        return new Dimensions(elementWidth * columns + columnGap * (columns-1), elementHeight * rows + rowGap * (rows-1));
    }

    // Layout
    // ======

    @Override
    public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children, Context context) {
        // this is gonna be fun
    }

    /**
     * Determine the number of columns to divide the grid into. Leave as 0 for auto.
     * If this is specified it can be overridden by width constraints.
     */
    public static final Style.Property<Integer> COLUMNS = new Style.Property<>("columns", 0, false);
    /**
     * If true, columns should stretch to take up as much space as possible.
     * If false, column width is determined by the largest element.
     */
    public static final Style.Property<Boolean> STRETCH_COLUMNS = new Style.Property<>("stretchColumns", false, false);
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
