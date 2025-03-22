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
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Function;

/**
 * A layout which positions components in a grid, left to right.
 * Components will wrap to the first column and next row once space runs out.
 */
public class Grid extends AbstractScrollContainer /*implements WrappingComponent*/ {
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


    //@Override TODO: need children
    //public int realHeight(int width, Context context) {
        //
    //}

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
            int fixedWidthColumns = findColumnsForWidth(W, elementWidth, columnGap);

            if (columns == 0 || fixedWidthColumns < columns) {
                columns = fixedWidthColumns;
            }
        }

        if (columns == 0) {
            final int n = Math.max(1,children.size());
            return new Dimensions(fixedWidth.orElse(elementWidth*n + columnGap * (n-1) + padding.horizontal()), elementHeight + padding.vertical());
        }

        int rows = calcRows(children.size(), columns);
        final int rowGap = this.getStyle().get(ROW_GAP);
        return new Dimensions(fixedWidth.orElse(elementWidth * columns + columnGap * (columns-1) + padding.horizontal()), elementHeight * rows + rowGap * (rows-1) + padding.vertical());
    }

    private static int findColumnsForWidth(int W, int elementWidth, int columnGap) {
        // the proof is left as an exercise to the reader
        // Solves (w+g)(n-1)+w=W for n
        return (W-elementWidth)/(elementWidth+columnGap) + 1;
    }

    private static int calcRows(int children, int columns) {
        return Math.max(1, (int)Math.ceil((double) children / columns));
    }

    // Layout
    // ======

    @Override
    public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children, Context context) {
        // style
        final int sColumns = this.getStyle().get(COLUMNS);
        final boolean sStretch = this.getStyle().get(STRETCH_COLUMNS);
        final int sColumnGap = this.getStyle().get(COLUMN_GAP);
        final int sRowGap = this.getStyle().get(ROW_GAP);

        final Align sAlignHorizontal = this.getStyle().get(ALIGN_HORIZONTAL);
        final Align sAlignVertical = this.getStyle().get(ALIGN_VERTICAL);

        // find box size //
        int elementWidth = 0;
        int elementHeight = 0;


        // find largest size
        for (SizedElement element : children) {
            Dimensions dimensions = element.getPreferredSize();

            if (dimensions.getHeight() > elementHeight) {
                elementHeight = dimensions.getHeight();
            }
            if (dimensions.getWidth() > elementWidth) {
                elementWidth = dimensions.getWidth();
            }
        }

        if (sStretch && sColumns > 0) {
            // instead divide space
            // solve w*n + g*(n-1) = W
            // => w = W-g(n-1) / n
            elementWidth = (region.getWidth() - sColumnGap*(sColumns-1)) / sColumns;
        }

        // calculate actual number of columns
        final int columns;
        if (sColumns == 0) {
            // figure out actual number of columns
            if (elementWidth + sColumnGap == 0) {
                elementWidth = 1; // i dont want a /0 kthx
            }

            columns = findColumnsForWidth(region.getWidth(), elementWidth, sColumnGap);
        } else {
            columns = sColumns;
        }

        // Remaining horizontal space distributed by justify
        int remainingHSpace = region.getWidth() - ( elementWidth*columns + sColumnGap*(columns-1) );

        // space-between, space-around, space-evenly simplify to centre for one entry
        // todo is this the most intuitive behaviour?
        Justify justifyColumns;
        switch (this.getStyle().get(JUSTIFY_COLUMNS)) {
        case SPACE_AROUND:
        case SPACE_BETWEEN:
        case SPACE_EVENLY:
            if (columns == 1) {
                justifyColumns = Justify.CENTRE;
                break;
            }
        default:
            justifyColumns = this.getStyle().get(JUSTIFY_COLUMNS);
            break;
        }

        // Figure out distribution of rows
        int rows = calcRows(children.size(), columns);
        Justify justifyRows; // same logic as columns
        switch (this.getStyle().get(JUSTIFY_ROWS)) {
            case SPACE_AROUND:
            case SPACE_BETWEEN:
            case SPACE_EVENLY:
                if (rows == 1) {
                    justifyRows = Justify.CENTRE;
                    break;
                }
            default:
                justifyRows = this.getStyle().get(JUSTIFY_ROWS);
                break;
        }

        // JUSTIFY_ROWS takes effect only if we aren't oversize.
        final int rowSpaceAndGapPixels = elementHeight*rows + sRowGap*(rows-1);
        int remainingVSpace = Math.max(0, region.getHeight() - rowSpaceAndGapPixels);

        // Place grid boxes and elements within these grid boxes.
        float y = region.getY();
        float x = region.getX();
        int column = 0;

        // add initial row gap
        if (remainingVSpace > 0) {
            y += initialSpace(justifyRows, remainingVSpace, rows);
        }

        Iterator<? extends ResizableElement> childIterator = children.iterator();

        while (childIterator.hasNext()) {
            ResizableElement element = childIterator.next();

            if (column == 0) {
                // add initial gap
                x += initialSpace(justifyColumns, remainingHSpace, columns);
            }

            // place at x, y
            {
                // Determine final component size
                // preferred does take into account fixed and intrinsic, and minimum
                // not maximum though (why?)
                Dimensions size = element.getPreferredSize();
                Align componentAlignHorizontal = element.getComponent().getStyle().get(CommonProperties.ALIGN_SELF)
                        .orElse(sAlignHorizontal);

                // handle stretch
                switch (componentAlignHorizontal) {
                    case STRETCH_CENTRE: case STRETCH_END: case STRETCH_START:
                        size = new Dimensions(
                                Math.max(elementWidth, size.getWidth()), // will be capped anyway; may as well shrink nicely?
                                size.getHeight()
                        );
                    default:
                        break;
                }
                switch (sAlignVertical) {
                    case STRETCH_CENTRE: case STRETCH_END: case STRETCH_START:
                        size = new Dimensions(
                                size.getWidth(),
                                Math.max(elementHeight, size.getHeight())
                        );
                    default:
                        break;
                }

                // handle max size & grid area borders
                Dimensions max = element.getMaximumSize();
                Dimensions min = element.getMinimumSize();

                int wCap = Math.min(elementWidth, max.getWidth());

                if (size.getWidth() > wCap) {
                    // clamp
                    int newWidth = Math.max(min.getWidth(), wCap);
                    // shrink other dimension
                    int h = element.shrinkHeight(newWidth, size.getHeight(), context);
                    h = Math.max(h, min.getHeight());
                    size = new Dimensions(newWidth, h);
                }

                int hCap = Math.min(elementHeight, max.getHeight());

                if (size.getWidth() > hCap) {
                    // clamp
                    int newHeight = Math.max(min.getHeight(), hCap);
                    // shrink other dimension
                    int w = element.shrinkWidth(newHeight, size.getWidth(), context);
                    w = Math.max(w, min.getWidth());
                    size = new Dimensions(w, newHeight);
                }

                // Handle alignment within space
                int componentX;
                switch (componentAlignHorizontal) {
                    case START: case STRETCH_START:
                        componentX = (int) x;
                        break;
                    case CENTRE: case STRETCH_CENTRE:
                        componentX = (int) x + (elementWidth - size.getWidth())/2;
                        break;
                    case END: case STRETCH_END: default:
                        componentX = (int) x + elementWidth - size.getWidth();
                        break;
                }

                int componentY;
                switch (sAlignVertical) {
                    case START: case STRETCH_START:
                        componentY = (int) y;
                        break;
                    case CENTRE: case STRETCH_CENTRE:
                        componentY = (int) y + (elementHeight - size.getHeight())/2;
                        break;
                    case END: case STRETCH_END: default:
                        componentY = (int) y + elementHeight - size.getHeight();
                        break;
                }

                // place component
                element.setRenderRegion(new Region(componentX, componentY, size.getWidth(), size.getHeight()));
            }

            // move forward to next item
            x += elementWidth;

            // add column gap
            // (what to do about column end gap? => we dont have horizontal overflow so we can ignore for now. doesnt matter if we go over or under)
            x += sColumnGap + gapSpace(justifyColumns, remainingHSpace, columns);

            // next column
            if (++column == columns) {
                column = 0;
                x = region.getX();
                y += elementHeight;

                // next row: add row gap
                // because remainingVSpace is 0 if overflow will happen this shouldnt be necessary but just in case,
                // to prevent accidental overflow when there is no overflow
                if (childIterator.hasNext()) {
                    y += sRowGap;
                    y += gapSpace(justifyRows, remainingVSpace, rows);
                }
            }
        }

        // set overflow flag
        this.overflow = (int)y > region.getEndY();
        this.maxScroll = y - region.getEndY();
        this.grabbed = false;
    }

    private static float initialSpace(Justify justify, int remainingSpace, int items) {
        switch (justify) {
            case END:
                return remainingSpace;
            case CENTRE:
                // only gap will be rounded anyway, int division is ok
                return remainingSpace/2;
            case SPACE_AROUND:
                return remainingSpace/(items * 2.0f);
            case SPACE_EVENLY:
                return remainingSpace/(items + 1.0f);
            case START: case SPACE_BETWEEN: default:
                return 0;
        }
    }

    private static float gapSpace(Justify justify, int remainingSpace, int items) {
        switch (justify) {
            case SPACE_AROUND:
                // right of first item, left of second item
                return remainingSpace/(float)items;
            case SPACE_BETWEEN:
                // 1 less gap than items
                return remainingSpace/(items - 1.0f);
            case SPACE_EVENLY:
                // 1 more gap than items
                return remainingSpace/(items + 1.0f);
            case START: case END: case CENTRE: default:
                return 0;
        }
    }

    /**
     * Determine the number of columns to divide the grid into. Leave as 0 for auto.
     * If this is specified it can be overridden by width constraints.
     */
    public static final Style.Property<Integer> COLUMNS = new Style.Property<>("columns", 0, false);
    /**
     * If true, columns should stretch to take up as much space as possible, instead of sizing off the largest element.
     * If false, column width is determined by the largest element.
     * Use this in combination with {@link Grid#COLUMNS}.
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
     * Align each component in its grid space horizontally. Children can override this with {@link CommonProperties#ALIGN_SELF}.
     */
    public static final Style.Property<Align> ALIGN_HORIZONTAL = new Style.Property<>("alignHorizontal", Align.CENTRE, false);
    /**
     * Align each component in its grid space vertically.
     */
    public static final Style.Property<Align> ALIGN_VERTICAL = new Style.Property<>("alignHorizontal", Align.CENTRE, false);
}
