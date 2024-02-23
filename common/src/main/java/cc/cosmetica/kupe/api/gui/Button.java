package cc.cosmetica.kupe.api.gui;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Region;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Button extends Component<Stylesheet> {
	protected Button() {
		super(Stylesheet.DEFAULT);
	}

	@Override
	public List<Component<?>> build() {
		return ImmutableList.of();
	}

	@Override
	public void render(Canvas canvas, Region region) {
		// TODO
	}
}
