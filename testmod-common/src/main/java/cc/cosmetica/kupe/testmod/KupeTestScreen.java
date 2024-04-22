/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod;

import cc.cosmetica.kupe.api.Image;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.*;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

public class KupeTestScreen extends Component {
	@Override
	public List<Component> build() {
		return Arrays.asList(
				new Div(
						new Label(Text.literal("You can add some text like this!")),
						new Button(Text.literal("Say Hello, World!"), () -> System.out.println("Hello, World!")),
						new Button(Text.literal("Say Ok, World!"), () -> System.out.println("Ok, World!")),
						new Image(new ResourceLocation("kupe", "icon.png")),
						new Button(Text.literal("Say Goodbye, World!"), () -> System.out.println("Goodbye, World!")),
						new Button(Text.GUI_DONE, Screens::closeCurrentScreen)
				).withStyle(new Stylesheet()
						.self(Style.create()
								.set(Div.JUSTIFY_CONTENT, Justify.CENTRE)
								.set(Div.ALIGN_ITEMS, Align.CENTRE)
								.set(CommonProperties.WIDTH, (vw, vh) -> OptionalInt.of(vw))
								.set(CommonProperties.HEIGHT, (vw, vh) -> OptionalInt.of(vh)))
						.component(Image.class, Style.create()
								.setFixed(CommonProperties.HEIGHT, OptionalInt.of(200))))
				// note the image may still be shrunk further due to FLEX_SHRINK.
				// You can set minimum size or remove the flex shrink to handle this.
		);
	}

	public static final ResourceLocation ID = new ResourceLocation("kupe_test", "screen");
}
