/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024-2025 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.mixin;

import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.testmod.gui.AllKupeTestsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
	@Inject(at = @At("RETURN"), method = "init")
	private void onInit(CallbackInfo info) {
		if (kupe_testmod$firstTime)
		{
			System.out.println("Setting debug kupe screen");
			kupe_testmod$firstTime = false;
			Minecraft.getInstance().schedule(() -> Screens.setScreen(AllKupeTestsScreen.ID));
		}
	}

	@Inject(at = @At("RETURN"), method = "render")
	private void onRender(CallbackInfo ci) {
		if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS) {
			Minecraft.getInstance().schedule(() -> Screens.setScreen(AllKupeTestsScreen.ID));
		}
	}

	@Unique
	private static boolean kupe_testmod$firstTime = true;
}
