package cc.cosmetica.kupe.testmod;

import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Stylesheet;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class KupeTestScreen extends Component<Stylesheet> {
	protected KupeTestScreen() {
		super(Stylesheet.DEFAULT);
	}

	@Override
	public List<Component<?>> build() {
		return Arrays.asList(
				new Button(Text.literal("Say Hello, World!"), () -> System.out.println("Hello, World!"))
		);
	}

	public static final ResourceLocation ID = new ResourceLocation("kupe_test", "screen");
}
