/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024-2025 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Grid;
import cc.cosmetica.kupe.api.gui.Image;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;

import static cc.cosmetica.kupe.api.gui.style.CommonProperties.*;

/**
 * Test case for image and box transparency.
 */
public class TransparencyTestScreen extends Screen {
    public TransparencyTestScreen() {
        super(ID);
    }

    @Override
    protected Component[] buildScreen() {
        return new Component[] {
                new Grid(
                        new Image(new ResourceKey("kupe", "icon.png")).setTransparent(1.0f).tag("block"),
                        new Image(new ResourceKey("kupe", "icon.png")).setTransparent(0.75f).tag("block"),
                        new Image(new ResourceKey("kupe", "icon.png")).setTransparent(0.50f).tag("block"),
                        new Image(new ResourceKey("kupe", "icon.png")).setTransparent(0.25f).tag("block"),
                        TransparentBlock(1.0f).tag("block"),
                        TransparentBlock(0.75f).tag("block"),
                        TransparentBlock(0.5f).tag("block"),
                        TransparentBlock(0.25f).tag("block")
                ).tag("x")
        };
    }

    private static Component TransparentBlock(float transparency) {
        return new Div()
        {
            @Override
            public void render(Canvas canvas, Region region, Margins padding, int mouseX, int mouseY) {
                canvas.setTransparency(transparency);
                super.render(canvas, region, padding, mouseX, mouseY);
                canvas.disableTransparency();
            }
        }.withStyle(Style.create()
                .set(BACKGROUND_COLOUR, OptionalInt.of(0xffffff)));
    }

    @Override
    public @NotNull Stylesheet getStylesheet() {
        return super.getStylesheet()
                .tag("x", Style.create()
                        .set(Grid.COLUMN_GAP, 2)
                        .set(Grid.ROW_GAP, 2)
                        .set(Grid.COLUMNS, 4)
                        .set(WIDTH, percent(50, 0))
                        .set(HEIGHT, percent(0, 80)))
                .tag("block", Style.create()
                        .set(WIDTH, fixed(OptionalInt.of(50)))
                        .set(HEIGHT, fixed(OptionalInt.of(50))));
    }

    public static final ResourceKey ID = new ResourceKey("kupe_test", "transparency");
}
