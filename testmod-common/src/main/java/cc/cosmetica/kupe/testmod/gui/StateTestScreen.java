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
import cc.cosmetica.kupe.api.gui.Image;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import net.minecraft.resources.ResourceLocation;

import java.util.OptionalInt;

/**
 * The example/test screen.
 */
public class StateTestScreen extends Screen {
	public StateTestScreen() {
		super(ID);
	}

	@Override
	public Component[] build(Style.MutableStyle rootStyle) {
		// note the image may still be shrunk further due to FLEX_SHRINK.
		// You can set minimum size or remove the flex shrink to handle this.

		return new Component[] {
				new Label(Text.literal("You can add some text like this!")),
				new HelloHolaComponent(),
				new Button(Text.literal("Fake Player Test"), () -> {
					System.out.println("Ok, World!");
					Screens.setScreen(FakePlayerTestScreen.ID);
				}),
				new Image(new ResourceLocation("kupe", "icon.png"))
						.withStyle(
								new Stylesheet()
										.self(Style.create().setFixed(CommonProperties.HEIGHT, OptionalInt.of(150)))
						),
				new Button(Text.literal("Say Goodbye, World!"), () -> System.out.println("Goodbye, World!")),
				new Button(Text.GUI_DONE, Screens::closeCurrentScreen)
		};
	}

	public static final ResourceLocation ID = new ResourceLocation("kupe_test", "screen");
}
