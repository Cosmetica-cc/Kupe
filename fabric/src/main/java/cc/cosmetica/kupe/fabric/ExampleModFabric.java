package cc.cosmetica.kupe.fabric;

import cc.cosmetica.kupe.Kupe;
import net.fabricmc.api.ModInitializer;

public class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Kupe.init();
    }
}
