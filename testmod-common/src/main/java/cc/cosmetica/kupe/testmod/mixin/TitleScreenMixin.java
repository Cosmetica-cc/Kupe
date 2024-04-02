package cc.cosmetica.kupe.testmod.mixin;

import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.testmod.KupeTestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
	@Inject(at = @At("RETURN"), method = "init")
	private void onInit(CallbackInfo info) {
		System.out.println("Title Screen Mixin");
		Minecraft.getInstance().tell(() -> Screens.setScreen(KupeTestScreen.ID));
	}
}
