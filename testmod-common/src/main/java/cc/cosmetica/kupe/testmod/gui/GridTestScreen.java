/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024-2025 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.gui;

import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Grid;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

import static cc.cosmetica.kupe.api.gui.style.CommonProperties.*;

/**
 * Test case for {@link cc.cosmetica.kupe.api.gui.Grid}.
 */
public class GridTestScreen extends Screen {
    public GridTestScreen() {
        super(ID);
    }

    @Override
    protected Component[] buildScreen() {
        return new Component[] {
                new Grid(
                        Box(), Box2(), Box(), Box(), Box2()
                ).tag("x")
        };
    }

    @Override
    public @Nullable Stylesheet getStylesheet() {
        return super.getStylesheet()
                .tag("x", Style.create()
                        .set(Grid.COLUMN_GAP, 2)
                        .set(Grid.ROW_GAP, 2)
                        .set(WIDTH, percent(50, 0))
                        .set(HEIGHT, percent(0, 80)))
                .tag("box", Style.create()
                        .set(BACKGROUND_COLOUR, OptionalInt.of(0xd3c5a2))
                        .set(WIDTH, fixed(OptionalInt.of(50)))
                        .set(HEIGHT, fixed(OptionalInt.of(50))))
                .tag("box2", Style.create()
                        .set(BACKGROUND_COLOUR, OptionalInt.of(0x7d87e8))
                        .set(WIDTH, fixed(OptionalInt.of(30)))
                        .set(HEIGHT, fixed(OptionalInt.of(30))));
    }

    private static Component Box() {
        return new Div().tag("box");
    }
    private static Component Box2() {
        return new Div().tag("box2");
    }

    public static final ResourceKey ID = new ResourceKey("kupe_test", "grid");
}
