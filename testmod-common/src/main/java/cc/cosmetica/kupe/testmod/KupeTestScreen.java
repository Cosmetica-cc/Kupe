package cc.cosmetica.kupe.testmod;

import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Stylesheet;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class KupeTestScreen extends Component<Stylesheet> {
	protected KupeTestScreen() {
		super(Stylesheet.DEFAULT);
	}

	@Override
	public List<Component<?>> build() {
		return new ArrayList<>();
	}

	public static final ResourceLocation ID = new ResourceLocation("kupe_test", "screen");
}
