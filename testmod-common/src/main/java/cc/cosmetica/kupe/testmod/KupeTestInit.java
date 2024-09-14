/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024 by Cosmetica Contributors
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
		Screens.enableDebug(); // enable debug on all kupe screens
		Screens.registerScreen(KupeTestsScreen.ID, new KupeTestsScreen());
		Screens.registerScreen(StateTestScreen.ID, new StateTestScreen());
		Screens.registerScreen(FakePlayerTestScreen.ID, new FakePlayerTestScreen());
		Screens.registerScreen(OversizeAndMaxHeightTestCase.ID, OversizeAndMaxHeightTestCase::new); // factory register
		Screens.registerScreen(ZLayeringTest.ID, new ZLayeringTest());
		Screens.registerScreen(ScrollTestScreen.ID, new ScrollTestScreen());
	}
}
