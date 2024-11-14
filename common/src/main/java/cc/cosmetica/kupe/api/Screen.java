/*
 * Copyright 2024 Cosmetica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.Kupe;
import cc.cosmetica.kupe.api.gui.*;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Margins;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

/**
 * An extension to Component that implements common behaviour for screens.
 * <br>
 * Children are provided through build(Style rootStyle), and are placed in a div that encompasses the screen space, with
 * {@linkplain Justify justify} and {@linkplain Align align} both set to CENTRE by default.
 */
public abstract class Screen extends Component {
	protected Screen(ResourceLocation location) {
		this.key = Text.translatable("screens." + location.getNamespace() + "." + location.getPath());
	}

	protected Screen(Text title) {
		this.key = title;
	}

	private final Text key;

	@Override
	public final List<Component> build() {
		// default style
		Style.MutableStyle style = Style.create()
				.set(CommonProperties.WIDTH, FULL_WIDTH)
				.set(CommonProperties.HEIGHT, (vw, vh) -> OptionalInt.of(vh))
				.set(Div.JUSTIFY_CONTENT, Justify.CENTRE)
				.set(Div.ALIGN_ITEMS, Align.CENTRE);

		return Arrays.asList(
				new Div(this.build(style)) // when building components the child screen may want to override the style
						.withStyle(style), // TODO is there a better way to allow overriding these properties for screen?
				new Label(this.key)
						.withStyle(Style.create()
								.set(CommonProperties.WIDTH, FULL_WIDTH)
								.set(Label.ALIGN_TEXT, Align.CENTRE))
		);
	}

	@Override
	public @Nullable Stylesheet getStylesheet() {
		return new Stylesheet()
				.self(Style.create().setFixed(CommonProperties.PADDING, DEFAULT_SCREEN_BORDERS));
	}

	/**
	 * Build the child components of this screen. These will be placed in the div.
	 * @param rootStyle the style used for the root div of the component. Can be modified.
	 *              The default properties are WIDTH: vw, HEIGHT: vh, JUSTIFY_CONTENT: centre, ALIGN_ITEMS: centre.
	 * @return the child components of this screen.
	 */
	protected abstract Component[] build(Style.MutableStyle rootStyle);

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			Screens.closeCurrentScreen();
		}

		return false;
	}

	private static final CommonProperties.DimensionsOperator<OptionalInt> FULL_WIDTH = (vw, vh) -> OptionalInt.of(vw);

	/**
	 * Margins for the contents of the whole screen. Used to keep the title label from while keeping everything else
	 * properly centered. You can override these by overriding getStylesheet().
	 */
	protected static final Margins DEFAULT_SCREEN_BORDERS = new Margins(15, 0);
}
