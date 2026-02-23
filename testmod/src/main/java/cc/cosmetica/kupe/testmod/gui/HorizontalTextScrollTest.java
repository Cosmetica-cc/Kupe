/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024-2025 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.gui;

import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Axis2D;

import java.util.OptionalInt;

import static cc.cosmetica.kupe.api.gui.style.CommonProperties.*;

/**
 * Horizontal Text Scroll Test (nowrap + horizontal div).
 */
public class HorizontalTextScrollTest extends Screen {
    public HorizontalTextScrollTest() {
        super(ID);
    }

    @Override
    protected Component[] buildScreen() {
        return new Component[] {
                new Div(new Label(Text.literal("Lorem ipsum dolor sit amet sadspicing Lorem ipsum dolor sit amet sadspicing Lorem ipsum dolor sit amet sadspicing Lorem ipsum dolor sit amet sadspicing Lorem ipsum dolor sit amet sadspicing"))
                        .withStyle(Style.create().set(Label.TEXT_WRAP, fixed(OptionalInt.empty())).set(FLEX_SHRINK, 0)))
                        .withStyle(Style.create().set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)),
                new Button(Text.GUI_DONE, Screens::closeCurrentScreen)
        };
    }

    public static final ResourceKey ID = new ResourceKey("kupe_test", "horizontal_text_scroll");
}
