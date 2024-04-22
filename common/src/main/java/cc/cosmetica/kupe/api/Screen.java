package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.api.gui.*;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Dimensions;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

/**
 * An extension to Component that implements common operations for screens.
 */
public abstract class Screen extends Component {
	protected Screen(ResourceLocation location) {
		this.key = Text.translatable("screens." + location.getNamespace() + "." + location.getPath());
	}

	private final Text key;

	@Override
	public List<Component> build() {
		// default style
		Style style = Style.create()
				.set(CommonProperties.WIDTH, FULL_WIDTH)
				.set(CommonProperties.HEIGHT, (vw, vh) -> OptionalInt.of(vh))
				.set(Div.JUSTIFY_CONTENT, Justify.CENTRE)
				.set(Div.ALIGN_ITEMS, Align.CENTRE);

		return Arrays.asList(
				new Label(this.key)
						.withStyle(new Stylesheet().self(Style.create().set(CommonProperties.WIDTH, FULL_WIDTH))),
				new Div(this.buildScreen(style)) // when building components the child screen may want to override the style
						.withStyle(new Stylesheet().self(style))
		);
	}

	/**
	 * Build the child components of this screen. These will be placed in the div.
	 * @param style the style used for the root div of the component. Can be modified here.
	 * @return the child components of this screen.
	 */
	protected abstract Component[] buildScreen(Style style);

	private static final CommonProperties.DimensionsOperator<OptionalInt> FULL_WIDTH = (vw, vh) -> OptionalInt.of(vw);
}
