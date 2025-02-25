/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.gui;

import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Margins;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

import static cc.cosmetica.kupe.api.gui.style.CommonProperties.*;

public class DivSpacingsScreen extends Screen {
	public DivSpacingsScreen() {
		super(ID);
	}

	@Override
	protected Component[] buildScreen() {
		return new Component[] {
				new Label(Text.literal("Space Around")),
				new Div(Block(), Block()).tag("space_around", "block"),
				new Label(Text.literal("Space Between")),
				new Div(Block(), Block()).tag("space_between", "block"),
				new Label(Text.literal("Space Evenly")),
				new Div(Block(), Block()).tag("space_evenly", "block")
		};
	}

	@Override
	public @Nullable Stylesheet getStylesheet() {
		return super.getStylesheet()
				.tag("space_around", Style.create().set(Div.JUSTIFY_CONTENT, Justify.SPACE_AROUND))
				.tag("space_between", Style.create().set(Div.JUSTIFY_CONTENT, Justify.SPACE_BETWEEN))
				.tag("space_evenly", Style.create().set(Div.JUSTIFY_CONTENT, Justify.SPACE_EVENLY))
				.tag("block", Style.create()
						.set(WIDTH, screen(90, 0))
						.set(BACKGROUND_COLOUR, OptionalInt.of(0))
						.set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
						.set(MARGINS, fixed(new Margins(0, 0, 20, 0))));
	}

	public static final ResourceKey ID = new ResourceKey("kupe_test", "div_spacings");

	private static Component Block() {
		return new Div().withStyle(
				Style.create()
						.set(WIDTH, fixed(OptionalInt.of(100)))
						.set(HEIGHT, fixed(OptionalInt.of(50)))
						.set(BACKGROUND_COLOUR, OptionalInt.of(0xdb9751))
		);
	}
}
