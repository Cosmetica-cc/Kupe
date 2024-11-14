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
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Dimensions;
import net.minecraft.resources.ResourceLocation;

import java.util.OptionalInt;

public class OversizeAndMaxHeightTestCase extends Screen {

	public OversizeAndMaxHeightTestCase() {
		super(ID);
	}

	private final State<Axis2D> axis = new State<>(Axis2D.POSITIVE_X);

	@Override
	protected Component[] buildScreen() {
		Axis2D axis = this.axis.acquire(this);
		System.out.println(axis);

		return new Component[] {
				// vertical div
				new Div(
						// horizontal div
						new Div(
								new Image(new ResourceLocation("kupe", "icon.png")), // oversize by default. should be capped
								new Label(Text.literal("Line 1"))
						).withStyle(Style.create().set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
										.set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(0x8800DD))
										.setFixed(CommonProperties.MAXIMUM_SIZE, new Dimensions(Integer.MAX_VALUE, 40)))
				).withStyle(Style.create().set(Div.FLOW_DIRECTION, axis)),

				new Button(Text.literal("Change Flow of Wrapper"), () -> {
					if (axis == Axis2D.POSITIVE_X) {
						this.axis.set(Axis2D.POSITIVE_Y);
					} else {
						this.axis.set(Axis2D.POSITIVE_X);
					}
				}).addHoverText(new Tooltip(Text.literal("This shouldn't change anything, visually"))),

				new Button(Text.GUI_DONE, Screens::closeCurrentScreen)
		};
	}

	public static final ResourceLocation ID = new ResourceLocation("kupe_test", "edge_case_1");
}
