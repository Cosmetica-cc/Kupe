/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024 by Cosmetica Contributors
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
				new Button(Text.literal("Div Spacings Test"), () -> Screens.setScreen(new DivSpacingsScreen(), DivSpacingsScreen.ID))
		};
	}

	public static final ResourceKey ID = new ResourceKey("kupe_test", "root");
}
