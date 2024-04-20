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

package cc.cosmetica.kupe.impl.text;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Renderable;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;

/**
 * Renderer for FormattedCharSequence.
 */
public class FormattedCharSeqRenderer implements Renderable {
	public FormattedCharSeqRenderer(Font font, FormattedCharSequence sequence) {
		this.font = font;
		this.sequence = sequence;
	}

	private final Font font;
	private final FormattedCharSequence sequence;

	@Override
	public void render(Canvas canvas, int x, int y, int colour) {
		this.font.draw(canvas.getStack().toMinecraftStack(), this.sequence, x, y, colour);
	}

	@Override
	public int width() {
		return this.font.width(this.sequence);
	}
}
