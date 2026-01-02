/*
 * Copyright 2024, 2025 Cosmetica
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
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.impl.MinecraftBuiltinComponent;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;

/**
 * A button in the menu, with text and an action.
 */
public class Button extends MinecraftBuiltinComponent implements Input {
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

	@Override
	public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
		return this.tryFixed(DEFAULT_DIMENSIONS, padding, context);
	}

	@Override
	public Button setDisabled(boolean disabled) {
		return (Button) super.setDisabled(disabled);
	}

	public Text getText() {
		return this.text;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		// TODO implement tab/shift-tab focus with auto-scroll - interface Input.
		if (this.minecraftWidget.isFocused()) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		} else {
			return false;
		}
	}

	@Override
	public AbstractWidget createMinecraftWidget(Region region, Context context) {
		return new net.minecraft.client.gui.components.Button.Builder(
				this.text.toMinecraftComponent(),
				bn -> this.onClicked.run())
				.bounds(region.getX(),
						region.getY(),
						region.getWidth(),
						region.getHeight())
				.build();
	}

	// Minecraft button uses render(mouseX, mouseY) for highlight
	@Override
	public void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
		// temporarily use focus to give same visual effect as hover
		// This also affects button tooltips. However, Kupe provides its own tooltip system.
		boolean originalFocused = this.minecraftWidget.isFocused();
		if (this.isOccluding(region, canvas.getScissor().orElse(region), mouseX, mouseY, false)) {
			this.minecraftWidget.setFocused(true);
		}
		super.paint(canvas, region, mouseX, mouseY);
		this.minecraftWidget.setFocused(originalFocused);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "(" + this.text.toString() + ")";
	}

	private static final Dimensions DEFAULT_DIMENSIONS = new Dimensions(200, 20);
}
