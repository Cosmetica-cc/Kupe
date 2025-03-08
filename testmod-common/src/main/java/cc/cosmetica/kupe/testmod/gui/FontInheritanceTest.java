/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024-2025 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.gui;

import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.style.Style;

public class FontInheritanceTest extends Screen {
    public FontInheritanceTest() {
        super(ID);
    }

    @Override
    protected Component[] buildScreen() {
        return new Component[] {
                new Div(
                        new Div(
                                new Label(Text.literal("I am aligned right.")),
                                new Div(
                                        new Label(Text.literal("In the beginning there was the Word. And the Word was with God. And the Word was God.\n~ John 1"))
                                )
                        )
                ).withStyle(Style.create().set(Label.ALIGN_TEXT, Align.END))
        };
    }

    public static final ResourceKey ID = new ResourceKey("kupe_test", "text_align_inheritance");
}
