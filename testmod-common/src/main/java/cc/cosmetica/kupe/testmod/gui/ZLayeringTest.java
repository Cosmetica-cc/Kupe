/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.gui;

import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.OptionalInt;

public class ZLayeringTest extends Screen {
	public ZLayeringTest() {
		super(ID);
	}

	@Override
	protected Component[] buildScreen() {
		return new Component[] {
				new Div(
						new Label(Text.literal("Green")),
						new Div(
								new Label(Text.literal("Purple")),
								new Div(
										new Label(Text.literal("Red"))
								).withStyle(Style.create().set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(0xFF0000))),
								new Label(Text.literal("Purple"))
						).withStyle(Style.create().set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(0xDD00DD))),
						new Label(Text.literal("Green")),
						new Button(Text.GUI_DONE, Screens::closeCurrentScreen)
				).withStyle(Style.create().set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(0x00BB33)))
		};
	}

	public static final ResourceLocation ID = new ResourceLocation("kupe_test", "z_layering");
}
