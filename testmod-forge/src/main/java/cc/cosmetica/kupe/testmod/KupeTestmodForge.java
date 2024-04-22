package cc.cosmetica.kupe.testmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("kupe_test")
public class KupeTestmodForge {
	public KupeTestmodForge() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
	}

	private void onClientSetup(FMLClientSetupEvent event) {
		KupeTestInit.init();
	}
}
