/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024-2025 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod;

import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.testmod.gui.*;

public final class KupeTestInit {
	private KupeTestInit() {
	}

	public static void init() {
		Screens.setDebugInspect(true); // have it on by default. this is a testmod after all
		Screens.registerScreen(AllKupeTestsScreen.ID, new AllKupeTestsScreen());
		Screens.registerScreen(BorderBoxTest.ID, new BorderBoxTest());
		Screens.registerScreen(FakePlayerTestScreen.ID, new FakePlayerTestScreen());
		Screens.registerScreen(OverlayTestScreen.ID, new OverlayTestScreen());
		// factory register demonstration
		Screens.registerScreen(OversizeAndMaxHeightTestCase.ID, OversizeAndMaxHeightTestCase::new);
		Screens.registerScreen(ScrollTestScreen.ID, new ScrollTestScreen());
		Screens.registerScreen(StateTestScreen.ID, new StateTestScreen());
		Screens.registerScreen(TextWrapTextboxSizingTest.ID, new TextWrapTextboxSizingTest());
		Screens.registerScreen(ZLayeringTest.ID, new ZLayeringTest());
		Screens.registerScreen(GridTestScreen.ID, new GridTestScreen());
		Screens.registerScreen(FontInheritanceTest.ID, new FontInheritanceTest());
		Screens.registerScreen(NestedScrollTestScreen.ID, new NestedScrollTestScreen());
		Screens.registerScreen(TextBoxTestScreen.ID, TextBoxTestScreen::new);
	}
}
