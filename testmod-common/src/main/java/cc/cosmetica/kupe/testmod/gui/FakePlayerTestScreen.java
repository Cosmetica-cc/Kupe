/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.gui;

import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.*;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * Test screen for the {@link FakePlayer} component.
 */
public class FakePlayerTestScreen extends Screen {
	public FakePlayerTestScreen() {
		super(ID);
	}

	private final State<Boolean> frozen = new State<>(false);

	@Override
	protected Component[] buildScreen() {
		boolean frozen = this.frozen.acquire(this);

		return new Component[] {
				new FakePlayer(UUID.fromString("8ea1da2f-0efa-4044-9e6f-4a3bf4e8a9a5"), !frozen)
						.withStyle(Style.create()
										.set(CommonProperties.BORDER_SIZE, 1)),
				new Button(Text.literal(frozen ? "Unfreeze Player" : "Freeze Player"), () -> this.frozen.set(!frozen)),
				new Button(Text.GUI_DONE, Screens::closeCurrentScreen)
		};
	}

	public static final ResourceLocation ID = new ResourceLocation("kupe_test", "fakeplayer");
}
