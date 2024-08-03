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

package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.RootStylesheet;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.impl.MinecraftBuiltinComponent;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * A button in the menu, with text and an action.
 */
public class Button extends MinecraftBuiltinComponent {
	/**
	 * Create a new button with the given text.
	 * @param text the text to provide.
	 */
	public Button(Text text, Runnable onClicked) {
		this.text = text;
		this.onClicked = onClicked;
	}

	// properties
	private final Text text;
	public Runnable onClicked;

	public Text getText() {
		return this.text;
	}

	@Override
	public AbstractWidget createMinecraftWidget(Region region, Context context) {
		return new net.minecraft.client.gui.components.Button(
				region.getX(),
				region.getY(),
				region.getWidth(),
				region.getHeight(),
				this.text.toMinecraftComponent(),
				bn -> this.onClicked.run()
		);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "(" + this.text.toString() + ")";
	}

	private static final Dimensions DEFAULT_DIMENSIONS = new Dimensions(200, 20);
	private static final Style DEFAULT_STYLE = Style.create()
			.set(CommonProperties.WIDTH, (vw, vh) -> OptionalInt.of(DEFAULT_DIMENSIONS.getWidth()))
			.set(CommonProperties.HEIGHT, (vw, vh) -> OptionalInt.of(DEFAULT_DIMENSIONS.getHeight()));

	static {
		RootStylesheet.setDefaultOverrides(Button.class, DEFAULT_STYLE);
	}
}
