/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024-2025 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.gui;

import cc.cosmetica.kupe.api.*;
import cc.cosmetica.kupe.api.gui.Border;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * Test screen for the {@link GUIPlayer} component.
 */
public class FakePlayerTestScreen extends Screen {
	public FakePlayerTestScreen() {
		super(ID);
	}

	private final State<Boolean> frozen = new State<>(false);
	private final State<Boolean> elytra = new State<>(false);
	private GUIPlayer p;

	@Override
	protected Component[] buildScreen() {
		boolean frozen = this.frozen.acquire(this);
		boolean elytra = this.elytra.acquire(this);

		GUIPlayer prior = p;

		try {
			return new Component[]{
					p = (GUIPlayer) new GUIPlayer(UUID.fromString("8ea1da2f-0efa-4044-9e6f-4a3bf4e8a9a5"), !frozen)
							.configureOverride(GUIPlayer.CAPE, new GUIPlayer.CapeProperties(new ResourceLocation("kupe_test", "textures/test_cape.png")))
							.hideAttachments()
							.showAttachments(elytra ? GUIPlayer.ELYTRA : GUIPlayer.CAPE)
							.showNametag(true)
							.addNametag(Text.literal("Test Player"), 0.75f)
							.withStyle(Style.create()
									.set(CommonProperties.BORDER, Border.create(1, 0xFFFFFF))),
					new Button(Text.literal(frozen ? "Unfreeze Player" : "Freeze Player"), () -> this.frozen.set(!frozen)),
					new Button(Text.literal(elytra ? "Show Cape" : "Show Elytra"), () -> this.elytra.set(!elytra)),
					new Button(Text.GUI_DONE, Screens::closeCurrentScreen)
			};
		} finally {
			if (prior != null) {
				p.pose.yRotHead = prior.pose.yRotHead;
				p.pose.yRotBody = prior.pose.yRotBody;
			}
		}
	}

	@Override
	public void render(Canvas canvas, Region region, Margins padding, int mouseX, int mouseY) {
		if (p !=null){
			p.pose.yRotBody += 1;
			p.pose.yRotHead += 1;
		}
		super.render(canvas, region, padding, mouseX, mouseY);
	}

	public static final ResourceKey ID = new ResourceKey("kupe_test", "fakeplayer");
}
