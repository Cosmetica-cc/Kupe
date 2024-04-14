package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.maths.Dimensions;

import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

/**
 * A plain text label. This could be multi-line.
 */
public class Label extends Component {
	public Label(Text text) {
		this.text = text;
	}

	private final Text text;

	@Override
	public Dimensions intrinsicSize(List<? extends SizedElement> children, Context context) {
		final int vw = context.getViewWidth();
		final int vh = context.getViewHeight();

		OptionalInt fixedWidth = this.getStyle().get(CommonProperties.WIDTH).apply(vw, vh);

		if (fixedWidth.isPresent()) {
			Dimensions maxDimensions = this.getStyle().get(CommonProperties.MAXIMUM_SIZE).apply(vw, vh);

			int width = Math.min(fixedWidth.getAsInt(), maxDimensions.getWidth());

			return new Dimensions(width, context.getTextHeight(this.text, width));
		} else {
			return new Dimensions(context.getWidth(this.text), context.getLineHeight());
		}
	}

	@Override
	public List<Component> build() {
		return Collections.emptyList();
	}
}
