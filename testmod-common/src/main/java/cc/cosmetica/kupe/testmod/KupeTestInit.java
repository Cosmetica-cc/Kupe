package cc.cosmetica.kupe.testmod;

import cc.cosmetica.kupe.api.Screens;

public final class KupeTestInit {
	private KupeTestInit() {
	}

	public static void init() {
		Screens.enableDebug(); // enable debug on all kupe screens
		Screens.registerScreen(KupeTestScreen.ID, new KupeTestScreen());
	}
}
