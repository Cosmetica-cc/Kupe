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
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;

import static cc.cosmetica.kupe.api.gui.style.CommonProperties.*;

public class AllKupeTestsScreen extends Screen {
	public AllKupeTestsScreen() {
		super(ID);
	}

	@Override
	protected Component[] buildScreen() {
		return new Component[] {
				new Button(Text.literal("State Test"), () -> Screens.setScreen(StateTestScreen.ID)),
				new Button(Text.literal("Fake Player Test"), () -> Screens.setScreen(FakePlayerTestScreen.ID)),
				new Button(Text.literal("Resizing Edge Case"), () -> Screens.setScreen(OversizeAndMaxHeightTestCase.ID)),
				new Button(Text.literal("Z Layering Test"), () -> Screens.setScreen(ZLayeringTest.ID)),
				new Button(Text.literal("Text Wrap Label Sizing Test"), () -> Screens.setScreen(TextWrapTextboxSizingTest.ID)),
				new Button(Text.literal("Scroll Test"), () -> Screens.setScreen(ScrollTestScreen.ID)),
				new Button(Text.literal("Overlay Test"), () -> Screens.setScreen(OverlayTestScreen.ID)),
				new Button(Text.literal("Border Box Test"), () -> Screens.setScreen(BorderBoxTest.ID)),
				// demonstrate setting an unregistered screen
				new Button(Text.literal("Div Spacings Test"), () -> Screens.setScreen(new DivSpacingsScreen(), DivSpacingsScreen.ID)),
				new Button(Text.translatable("screens.kupe_test.grid"), () -> Screens.setScreen(GridTestScreen.ID)),
				new Button(FontInheritanceTest.ID.translationKey("screens"), () -> Screens.setScreen(FontInheritanceTest.ID)),
				new Div(Region(0), Region(1), Region(0), Region(1), Region(0), Region(1)).withStyle(Style.create().set(FLEX_SHRINK, 0).set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X).set(MIN_WIDTH, fixedSize(0)).set(WIDTH, fixedSize(50)))
		};
	}

	private Component Region(int c) {
		return new Div().withStyle(
				Style.create().set(WIDTH, fixedSize(50)).set(FLEX_SHRINK, 0).set(HEIGHT, fixedSize(20))
						.set(BACKGROUND_COLOUR, OptionalInt.of(c==1?0xFF00FF:0x00FF77))
		);
	}

	@Override
	public @NotNull Stylesheet getStylesheet() {
		return super.getStylesheet()
				.tag("body", Style.create()
						.set(MIN_HEIGHT, fixed(OptionalInt.of(0)))
						.set(MARGINS, screen(0, 12, MARGINS_FACTORY))
						.set(CommonProperties.HEIGHT, screen(0, 75)))
				.component(Button.class, Style.create()
						.set(FLEX_SHRINK, 0));
	}

	public static final ResourceKey ID = new ResourceKey("kupe_test", "root");
}
