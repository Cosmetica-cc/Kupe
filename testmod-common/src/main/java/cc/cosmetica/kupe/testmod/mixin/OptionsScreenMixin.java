/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024-2025 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.mixin;

import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.testmod.gui.AllKupeTestsScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.client.gui.screens.options.OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
	protected OptionsScreenMixin(Component component) {
		super(component);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		boolean result = super.keyPressed(event);
		if (event.key() == InputConstants.KEY_RCONTROL) {
			Minecraft.getInstance().schedule(() -> Screens.setScreen(AllKupeTestsScreen.ID));
			result = true;
		}
		return result;
	}
}
