package cc.cosmetica.kupe.testmod.neoforge;

import cc.cosmetica.kupe.testmod.KupeTestInit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("kupe_test")
public class KupeTestmodNeoforge {
    public KupeTestmodNeoforge() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            KupeTestInit.init();
        }
    }
}
