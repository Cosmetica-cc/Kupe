/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024-2025 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.gui;

import cc.cosmetica.kupe.api.*;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.TextBox;
import cc.cosmetica.kupe.testmod.gui.widget.SubmitButton;

public class TextBoxTestScreen extends Screen {
    public TextBoxTestScreen() {
        super(ID);
    }

    private final State<String> text = new State<>("");

    @Override
    protected Component[] buildScreen() {
        return new Component[] {
                new TextBox(Text.literal("Enter something..."), this.text, true, 32),
                new SubmitButton<>(Text.GUI_PROCEED, str -> Screens.closeCurrentScreen(), this.text)
        };
    }

    public static final ResourceKey ID = new ResourceKey("kupe_test", "text_box_test");
}
