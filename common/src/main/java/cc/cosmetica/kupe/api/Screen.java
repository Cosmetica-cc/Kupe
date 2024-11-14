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

import cc.cosmetica.kupe.api.gui.*;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Margins;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * An extension to Component that implements common behaviour for screens.
 * <br>
 * Children are provided through buildScreen(), and are placed in a div that encompasses the screen space, with
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
	private boolean showTitle = true;

	protected void setShowDefaultTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

	@Override
	public final List<Component> build() {
		// default style
		List<Component> components = new ArrayList<>();
		components.add(
				new Div(this.buildScreen()).tag("body")
		);

		if (this.showTitle) {
			components.add(
					new Label(this.key).tag("title")
			);
		}

		return components;
	}

	@Override
	public @Nullable Stylesheet getStylesheet() {
		// the child screen may want to override the style
		// they can do that with this stylesheet
		return new Stylesheet()
				.tag("body", BODY_DEFAULT_STYLE)
				.tag("title", TITLE_DEFAULT_STYLE);
	}

	/**
	 * Build the child components of this screen. These will be placed in the div.
	 * @return the child components of this screen.
	 */
	protected abstract Component[] buildScreen();

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			Screens.closeCurrentScreen();
		}

		return false;
	}

	protected static final CommonProperties.DimensionsOperator<OptionalInt> FULL_WIDTH = (vw, vh) -> OptionalInt.of(vw);

	// make immutable with Style.merge. Default styles shouldn't be modifiable. Override stylesheet instead.
	protected static final Style BODY_DEFAULT_STYLE = Style.merge(
			Collections.singletonList(Style.create()
					.set(CommonProperties.WIDTH, FULL_WIDTH)
					.set(CommonProperties.HEIGHT, (vw, vh) -> OptionalInt.of(vh))
					.set(Div.JUSTIFY_CONTENT, Justify.CENTRE)
					.set(Div.ALIGN_ITEMS, Align.CENTRE))
	);
	protected static final Style TITLE_DEFAULT_STYLE = Style.merge(
			Collections.singletonList(Style.create()
					.set(CommonProperties.WIDTH, FULL_WIDTH)
					.set(Label.ALIGN_TEXT, Align.CENTRE)
					.setFixed(CommonProperties.MARGINS, new Margins(15, 0, 0, 0)))
	);
}
