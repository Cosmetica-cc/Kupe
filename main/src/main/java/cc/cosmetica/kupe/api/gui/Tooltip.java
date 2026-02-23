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
import cc.cosmetica.kupe.api.Renderable;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.impl.MathsImpl;
import cc.cosmetica.kupe.impl.PoseCanvas;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * A tooltip which can be drawn. Does not currently support colour tint.
 */
public class Tooltip implements Renderable {
	public Tooltip(Text text) {
		this.text = text.toMinecraftComponent();
	}

	private final Component text;

	/**
	 * Get the text wrap width for the given context. Can be overridden for custom behaviour,
	 * or defines a default behaviour dependent on the platform.
	 * @param context the context to wrap text in.
	 * @return the width at which to wrap text.
	 */
	public int getTextWrapWidth(Context context) {
		return MathsImpl.defaultTooltipWidth(context.getViewWidth());
	}

	@Override
	public void render(Canvas canvas, int x, int y, int colour) {
		if (canvas instanceof PoseCanvas) {
			((PoseCanvas) canvas).renderFloating((x_, y_) -> this._render(canvas, x_, y_), x, y);
		} else {
			this._render(canvas, x, y);
		}
	}

	private void _render(Canvas canvas, int x, int y) {
		Minecraft.getInstance().screen.renderTooltip(
				canvas.getStack().getMinecraftStack(),
				Minecraft.getInstance().font.split(this.text, getTextWrapWidth(canvas.getDrawingContext())),
				x, y);
	}
}
