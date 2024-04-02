package cc.cosmetica.kupe.impl.text;

import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.util.Cache;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Arrays;

/**
 * Component for plain text.
 */
public class TranslatableText implements Text {
	public TranslatableText(String text, Object... format) {
		this.key = text;
		this.format = format;
	}

	private final String key;
	private final Object[] format;
	private final Cache<String> display = new Cache<>();

	@Override
	public String getString() {
		return this.key;
	}

	@Override
	public String getDisplayString() {
		return this.display.get(
				() -> this.toMinecraftComponent().getString() // TODO is this right
		);
	}

	@Override
	public Component toMinecraftComponent() {
		return new TranslatableComponent(this.key, this.format);
	}

	@Override
	public String toString() {
		return "TranslatableText{" +
				"key='" + key + '\'' +
				", format=" + Arrays.toString(format) +
				'}';
	}
}
