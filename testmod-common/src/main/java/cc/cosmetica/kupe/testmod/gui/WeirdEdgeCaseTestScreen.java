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
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Dimensions;
import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.OptionalInt;

public class WeirdEdgeCaseTestScreen extends Screen {

	public WeirdEdgeCaseTestScreen() {
		super(ID);
	}

	@Override
	protected Component[] build(Style.MutableStyle rootStyle) {
		return new Component[] {
				// vertical div
				new Div(new SubComponentIdk())
						.withStyle(new Stylesheet()
						.self(Style.create()
								.setFixed(CommonProperties.WIDTH, OptionalInt.of(128)))),
				new Button(Text.GUI_DONE, Screens::closeCurrentScreen)
		};
	}

	public static final ResourceLocation ID = new ResourceLocation("kupe_test", "edge_case_1");

	private static class SubComponentIdk extends Component {
		@Override
		public List<Component> build() {
			return ImmutableList.of(
					// horizontal div
					new Div(
							new Label(Text.literal("Line 1")),
							new Label(Text.literal("Line 2"))
					).withStyle(new Stylesheet().self(Style.create()
							.set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
							.set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(0x8800DD))
							.setFixed(CommonProperties.MAXIMUM_SIZE, new Dimensions(Integer.MAX_VALUE, 40))))
			);
		}
	}
}
