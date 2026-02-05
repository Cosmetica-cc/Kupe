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

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Region;
import org.jetbrains.annotations.Nullable;

/**
 * Base for a container that can scroll.
 */
public abstract class AbstractScrollContainer extends Component {
    // set by container
    protected boolean overflow = false;

    /**
     * Determine whether this scroll container has vertical overflow.
     * @return whether there is vertical overflow (and thus a scrollbar should be drawn).
     */
    protected abstract boolean hasVerticalOverflow();

    /////////////////////////
    // Scrolling Behaviour //
    /////////////////////////

    // grab
    protected boolean grabbed;
    private float grabOffset;
    // scrollbar properties
    private float scrollbarTopY, scrollbarLeftX; // passed from render to click/paintDecorations
    private int scrollbarSize;
    // scrolling state
    protected float maxScroll = 0;
    private float scrollPercent;

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (this.overflow) {
            // scroll% = amount Scrolled / maxScroll
            // add to exising scroll%
            int scrollSpeed = this.getStyle().get(SCROLL_SPEED);
            float newScroll = this.scrollPercent - (float) ((delta * scrollSpeed) / this.maxScroll);
            // clamp
            if (newScroll > 1) newScroll = 1;
            else if (newScroll < 0) newScroll = 0;

            boolean scrollChanged = this.scrollPercent != newScroll;
            this.scrollPercent = newScroll;
            return scrollChanged;
        } else {
            return false;
        }
    }

    @Override
    public void mouseClicked(Element target, double x, double y, int button) {
        if (this.shouldDrawVerticalScrollbar()) {
            if (
                y >= this.scrollbarTopY && y < (this.scrollbarTopY + this.scrollbarSize)
                        && x >= this.scrollbarLeftX && x < (this.scrollbarLeftX + DEFAULT_SCROLLBAR_THICKNESS)
            ) {
                this.grabbed = true;
                this.grabOffset = (float) y - this.scrollbarTopY;
            }
        }
    }

    @Override
    public void mouseReleased(double x, double y, int button) {
        if (this.grabbed) {
            this.grabbed = false;
        }
    }

    @Override
    public boolean isOccluding(Region region, Region scissorRegion, int x, int y, boolean decorations) {
        boolean outsideScrollbar = this.getStyle().get(SCROLLBAR_POSITION) == ScrollbarPosition.OUTSIDE;

        if (outsideScrollbar && decorations && this.shouldDrawVerticalScrollbar()) {
            if (!region.grow(0, DEFAULT_SCROLLBAR_THICKNESS, 0, 0).intersect(scissorRegion).contains(x, y)) {
                return false;
            }
        } else {
            if (!region.intersect(scissorRegion).contains(x, y)) {
                return false;
            }
        }

        if (decorations) {
            if (this.shouldDrawVerticalScrollbar()) {
                // scrollbar
                return x >= region.getEndX() - (outsideScrollbar ? 0 : DEFAULT_SCROLLBAR_THICKNESS);
            }

            return false;
        } else {
            return this.getStyle().get(CommonProperties.BACKGROUND_COLOUR).isPresent();
        }
    }

    /////////////////////
    // Render Methods //
    ////////////////////

    @Override
    public @Nullable Region getScissorRegion(Region region) {
        return this.overflow ? region : null;
    }

    @Override
    public void paintDecorations(Canvas canvas, Region region, Region scissorRegion, int mouseX, int mouseY) {
        if (this.shouldDrawVerticalScrollbar()) {
            boolean inside = this.getStyle().get(SCROLLBAR_POSITION) == ScrollbarPosition.INSIDE;
            final int outsideShift = DEFAULT_SCROLLBAR_THICKNESS;

            if (!inside) {
                Region sciss = this.getScissorRegion(region);
                if (sciss != null) {
                    // scissor is popped after paintDecorations()
                    canvas.useScissor(sciss.translate(outsideShift, 0), false);
                }
            }
            this.drawScrollbar(canvas, inside ? region :
                    region.translate(outsideShift, 0), mouseY);
        }

        super.paintDecorations(canvas, region, scissorRegion, mouseX, mouseY);
    }

    @Override
    protected void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
        // scroll interaction and visual pan (doesn't do anything if no overflow)
        if (this.overflow) {
            // Dragging Vertical Scrollbar
            if (this.hasVerticalOverflow() && this.shouldDrawVerticalScrollbar()) {
                // measurements
                final float divVH = region.getHeight();
                float pageCover = divVH / (divVH + this.maxScroll);
                this.scrollbarSize = (int) Math.max(10, pageCover * divVH);// minimum height of 10 pixels

                // update scroll position for drag
                if (this.grabbed) {
                    this.scrollPercent = (mouseY - this.grabOffset - region.getY()) / (divVH - this.scrollbarSize);
                    // clamp
                    if (this.scrollPercent > 1) this.scrollPercent = 1;
                    else if (this.scrollPercent < 0) this.scrollPercent = 0;
                }
            }

            //shift contents by scroll amount
            if (this.hasVerticalOverflow()) {
                canvas.scroll(0, -this.scrollPercent * this.maxScroll);
            } else {
                canvas.scroll(-this.scrollPercent * this.maxScroll, 0);
            }
        }
    }

    protected boolean shouldDrawVerticalScrollbar() {
        switch (this.getStyle().get(SCROLLBAR)) {
        case ALWAYS:
            return true;
        case NEVER:
            return false;
        case AUTO:
        default:
            return this.hasVerticalOverflow();
        }
    }

    protected void drawScrollbar(Canvas canvas, Region region, int mouseY) {
        // height of the div's 'view'. But not of all its contents.
        final float divVH = region.getHeight();

        float scrollbarTopY = region.getY() + this.scrollPercent * (divVH - this.scrollbarSize);
        // pass to click
        this.scrollbarLeftX = region.getEndX() - DEFAULT_SCROLLBAR_THICKNESS;
        this.scrollbarTopY = scrollbarTopY;

        canvas.drawRect(
                region.getEndX() - DEFAULT_SCROLLBAR_THICKNESS, region.getY(),
                DEFAULT_SCROLLBAR_THICKNESS, region.getHeight(),
                50.0f, 0, 0, 0
        );
        canvas.drawRect(
                region.getEndX() - DEFAULT_SCROLLBAR_THICKNESS, (int)scrollbarTopY,
                DEFAULT_SCROLLBAR_THICKNESS, this.scrollbarSize,
                50.0f, 0.5f, 0.5f, 0.5f
        );

        final float scrollBarColour = 192.0f/255.0f;
        canvas.drawRect(
                region.getEndX() - DEFAULT_SCROLLBAR_THICKNESS, (int)scrollbarTopY,
                DEFAULT_SCROLLBAR_THICKNESS - 1, this.scrollbarSize - 1,
                50.0f, scrollBarColour, scrollBarColour, scrollBarColour
        );
    }

    /**
     * Controls when the vertical scrollbar should be shown. By default, shows on content overflow. Horizontal scrollbar is not implemented yet.
     */
    public static final Style.Property<Visibility> SCROLLBAR = new Style.Property<>("scrollbar", Visibility.AUTO, false);
    /**
     * Set where the scrollbar should be placed.
     */
    public static final Style.Property<ScrollbarPosition> SCROLLBAR_POSITION = new Style.Property<>("scrollbarPosition", ScrollbarPosition.INSIDE, false);
    /**
     * Scroll speed, in gui-scaled pixels per scroll unit.
     */
    public static final Style.Property<Integer> SCROLL_SPEED = new Style.Property<>("scrollSpeed", 14, false);

    private static final int DEFAULT_SCROLLBAR_THICKNESS = 6;

    public enum Visibility {
        /**
         * Show the scrollbar always.
         */
        ALWAYS,
        /**
         * Show the scrollbar automatically when there is overflow.
         */
        AUTO,
        /**
         * Never show the scrollbar.
         */
        NEVER
    }

    public enum ScrollbarPosition {
        /**
         * Place the scrollbar inside at the right of the component
         */
        INSIDE,
        /**
         * Place the scrollbar outside, to the right of the component.
         */
        OUTSIDE
    }
}
