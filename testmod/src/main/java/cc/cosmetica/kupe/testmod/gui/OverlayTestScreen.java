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
import cc.cosmetica.kupe.api.gui.*;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Margins;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cc.cosmetica.kupe.api.gui.style.CommonProperties.*;

/**
 * Test screen for an overlay that can pop up and hide.
 */
public class OverlayTestScreen extends Screen {
	public OverlayTestScreen() {
		super(ID);
	}

	@Override
	protected Component[] buildScreen() {
		return new Component[] {
				new Div()
						.withStyle(Style.create()
							.set(CommonProperties.PADDING, fixed(new Margins(1)))
							.set(CommonProperties.BORDER, Border.create(Border.BorderConfig.split(1, 0xDDDDDD, 0x666666)))
							.set(CommonProperties.WIDTH, screen(200/3f, 0))
							.set(CommonProperties.FLEX, 1)),
				new Button(Text.GUI_DONE, Screens::closeCurrentScreen)
		};
	}

	@Override
	public @NotNull Stylesheet getStylesheet() {
		return new Stylesheet()
				.tag("body", Style.create()
						// default
						.set(CommonProperties.WIDTH, SCREEN_WIDTH)
						.set(CommonProperties.HEIGHT, SCREEN_HEIGHT)
						.set(Div.JUSTIFY_CONTENT, Justify.CENTRE)
						.set(Div.ALIGN_ITEMS, Align.CENTRE)
						// added
						.set(CommonProperties.MARGINS, fixed(new Margins(15, 0))))
				.tag("title", TITLE_DEFAULT_STYLE);
	}

	public static final ResourceKey ID = new ResourceKey("kupe_test", "overlay");
}
