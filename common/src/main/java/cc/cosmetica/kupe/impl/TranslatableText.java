package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.util.Cache;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * Component for plain text.
 */
public class TranslatableText implements Text {
	public TranslatableText(String text, Object... format) {
		this.text = text;
		this.format = format;
	}

	private final String text;
	private final Object[] format;
	private final Cache<String> display = new Cache<>();

	@Override
	public String getString() {
		return this.text;
	}

	@Override
	public String getDisplayString() {
		return this.display.get(
				() -> this.toMinecraftComponent().getVisualOrderText().toString() // TODO is this right
		);
	}

	@Override
	public Component toMinecraftComponent() {
		return new TranslatableComponent(this.text, this.format);
	}
}
