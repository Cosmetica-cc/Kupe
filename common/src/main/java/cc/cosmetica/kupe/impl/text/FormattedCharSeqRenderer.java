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

package cc.cosmetica.kupe.impl.text;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Renderable;
import cc.cosmetica.kupe.impl.ModernCanvas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

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
		if (canvas instanceof ModernCanvas) {
			((ModernCanvas)canvas).drawCharSequence(this.font, this.sequence, x, y, colour);
		} else {
			throw new UnsupportedOperationException("This canvas type does not currently support direct FormattedCharSequence render for Minecraft >=1.21.8");
		}
	}

	@Override
	public int width() {
		return this.font.width(this.sequence);
	}
}
