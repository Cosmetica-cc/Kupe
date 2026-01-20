/*
 * Kupe Testmod - Test and Example code for usage of the Kupe Library.
 * Written in 2024-2025 by Cosmetica Contributors
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package cc.cosmetica.kupe.testmod.gui;

import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.*;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static cc.cosmetica.kupe.api.gui.style.CommonProperties.*;

public class ImageListTestScreen extends Screen {
	public ImageListTestScreen() {
		super(ID);
	}

	@Override
	protected Component[] buildScreen() {
		return new Component[] {
				new Div(
						IntStream.range(1, 11).mapToObj(ImageListTestScreen::Entry).toArray(Component[]::new)
				).withStyle(Style.create()
						.set(MIN_HEIGHT, fixed(OptionalInt.of(0)))
						.set(AbstractScrollContainer.SCROLLBAR_POSITION, AbstractScrollContainer.ScrollbarPosition.OUTSIDE)
						.set(WIDTH, screen(100,0))
						.set(MAXIMUM_SIZE, (vw, vh,pw,ph) -> {
					return new Dimensions(7 * vw / 8, 3 * vh /4 );
				})),
				new Button(Text.GUI_DONE, Screens::closeCurrentScreen)
		};
	}

	@Override
	public @NotNull Stylesheet getStylesheet() {
		return super.getStylesheet() // default styles
				.tag("entry", Style.create()
						.set(PADDING, fixed(new Margins(2)))
						.set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
						.set(WIDTH, percent(100, 0))
						.set(BACKGROUND_COLOUR, OptionalInt.of(0xDDDDDD))
						.set(BORDER, Border.create(1, 0xAAAAAA))
						.set(FLEX_SHRINK, 0)
						.set(HEIGHT, fixedSize(30)))
				.component(Image.class, Style.create().set(WIDTH, fixedSize(20)).set(MARGINS, fixed(new Margins(0, 2, 0, 0))).set(HEIGHT, fixedSize(20)))
				.self(Style.create().set(CommonProperties.PADDING, fixed(new Margins(5, 0))));
	}

	public static final ResourceKey ID = new ResourceKey("kupe_test", "image_list");

	private static Component Entry(int number) {
		final int max = 10;

		return new Div(
				new Image(new ResourceKey("kupe", "icon.png")),
				new Label(Text.literal("Entry #" + number + "/" + max))
		).tag("entry");
	}
}
