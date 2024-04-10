package cc.cosmetica.kupe.testmod;

import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.*;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

public class KupeTestScreen extends Component {
	@Override
	public List<Component> build() {
		return Arrays.asList(
				new Div(
						new Button(Text.literal("Say Hello, World!"), () -> System.out.println("Hello, World!")),
						new Button(Text.literal("Say Ok, World!"), () -> System.out.println("Ok, World!")),
						new Button(Text.literal("Say Goodbye, World!"), () -> System.out.println("Goodbye, World!"))
				).withStyle(new Stylesheet()
						.self(Style.create()
								.set(Div.JUSTIFY_CONTENT, Justify.CENTRE)
								.set(Div.ALIGN_ITEMS, Align.CENTRE)
								.set(CommonProperties.WIDTH, (vw, vh) -> OptionalInt.of(vw))
								.set(CommonProperties.HEIGHT, (vw, vh) -> OptionalInt.of(vh))))
		);
	}

	public static final ResourceLocation ID = new ResourceLocation("kupe_test", "screen");
}
