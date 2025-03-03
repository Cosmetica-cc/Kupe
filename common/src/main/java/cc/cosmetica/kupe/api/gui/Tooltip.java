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
import cc.cosmetica.kupe.api.Renderable;
import cc.cosmetica.kupe.api.Text;
import com.mojang.blaze3d.systems.RenderSystem;
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

	@Override
	public void render(Canvas canvas, int x, int y, int colour) {
		Minecraft.getInstance().screen.renderTooltip(
				canvas.getStack().getMinecraftStack(),
				this.text, // TODO ability to split lines
				x, y);
	}
}
