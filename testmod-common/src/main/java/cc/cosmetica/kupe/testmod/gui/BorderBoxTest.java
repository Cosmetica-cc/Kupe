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
import cc.cosmetica.kupe.api.gui.Image;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Margins;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;

import static cc.cosmetica.kupe.api.gui.style.CommonProperties.*;

public class BorderBoxTest extends Screen {
	public BorderBoxTest() {
		super(ID);
	}

	@Override
	protected Component[] buildScreen() {
		return new Component[] {
				new Div(
						new Div(
								new Image(new ResourceKey("kupe", "icon.png"))
										.withStyle(Style.create()
												.set(PADDING, fixed(new Margins(5, 5, 5, 5)))
												.set(BACKGROUND_COLOUR, OptionalInt.of(0x00AAAA)))
						).tag("inner")
				).tag("outer")
		};
	}

	@Override
	public @NotNull Stylesheet getStylesheet() {
		return super.getStylesheet()
				.tag("outer", Style.create()
						.set(CommonProperties.WIDTH, fixed(OptionalInt.of(120)))
						.set(HEIGHT, fixed(OptionalInt.of(60)))
						.set(CommonProperties.PADDING, fixed(new Margins(5, 5, 5, 5)))
						.set(BACKGROUND_COLOUR, OptionalInt.of(0x00FF00)))
				.tag("inner", Style.create()
						.set(CommonProperties.WIDTH, percent(100, 0))
						.set(HEIGHT, percent(0, 100))
						.set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
						.set(BACKGROUND_COLOUR, OptionalInt.of(0x0088FF)));
	}

	public static final ResourceKey ID = new ResourceKey("kupe_test", "border_box");
}
