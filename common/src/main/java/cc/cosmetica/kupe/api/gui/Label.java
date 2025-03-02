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
import cc.cosmetica.kupe.api.Renderable;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;

import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

/**
 * A plain text label. This could be multi-line.
 */
public class Label extends Component implements WrappingElement {
	public Label(Text text) {
		this.text = text;
	}

	private final Text text;

	@Override
	public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
		// - Shouldn't label have a minimum height of line height?

		final int vw = context.getViewWidth();
		final int vh = context.getViewHeight();

		OptionalInt fixedWidth = this.getStyle().get(CommonProperties.WIDTH).apply(vw, vh, 0, 0);

		if (fixedWidth.isPresent()) {
			Dimensions maxDimensions = this.getStyle().get(CommonProperties.MAXIMUM_SIZE).apply(vw, vh, 0, 0);

			int width = Math.min(fixedWidth.getAsInt(), maxDimensions.getWidth());
			// specified width in fixed/max INCLUDES padding

			return new Dimensions(width, context.getTextHeight(this.text, width - padding.horizontal()/*get content width*/) + padding.vertical());
		} else {
			return new Dimensions(context.getWidth(this.text) + padding.horizontal(), context.getLineHeight() + padding.vertical());
		}
	}

	@Override
	public int realHeight(int width, Context context) {
		return context.getTextHeight(this.text, width);
	}

	@Override
	public List<Component> build() {
		return Collections.emptyList();
	}

	// Generate text lines and render
	List<Renderable> label;

	@Override
	public void resize(Region region, SizedElement sizedElement, List<? extends ResizableElement> children, Context context) {
		this.label = context.split(this.text, region.getWidth());
	}

	@Override
	public void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
		int y = region.getY();
		final int lineHeight = canvas.getDrawingContext().getLineHeight();
		final int tint = this.getStyle().get(TEXT_COLOUR);

		switch (this.getStyle().get(ALIGN_TEXT)) {
		case START:
		case STRETCH_START:
			for (Renderable renderable : this.label) {
				renderable.render(canvas, region.getX(), y, tint);
				y += lineHeight;
			}
			break;
		case CENTRE:
		case STRETCH_CENTRE:
			final int width = region.getWidth();

			for (Renderable renderable : this.label) {
				renderable.render(canvas, region.getX() + (width-renderable.width())/2, y, tint);
				y += lineHeight;
			}
			break;
		case END:
		case STRETCH_END:
			for (Renderable renderable : this.label) {
				renderable.render(canvas, region.getFinalX() - renderable.width(), y, tint);
				y += lineHeight;
			}
			break;
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{" + this.text + "}";
	}

	/**
	 * The text colour, as an RGB integer. Default value is #FFFFFF (0xFFFFFF)
	 */
	public static final Style.Property<Integer> TEXT_COLOUR = new Style.Property<>("textColour", 0xFFFFFF, true);

	/**
	 * The alignment of text in the label.
	 */
	public static final Style.Property<Align> ALIGN_TEXT = new Style.Property<>("alignText", Align.START, true);
}
