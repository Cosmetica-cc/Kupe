package cc.cosmetica.kupe.testmod;

import cc.cosmetica.kupe.api.Screens;
import net.fabricmc.api.ModInitializer;

public class KupeTestmodFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Screens.registerScreen(KupeTestScreen.ID, new KupeTestScreen());
	}
}
