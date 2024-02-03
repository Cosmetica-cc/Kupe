package cc.cosmetica.kupe.impl;

import cc.cosmetica.kupe.api.Text;
import net.minecraft.network.chat.Component;

/**
 * Component for plain text.
 */
public class LiteralText implements Text {
	public LiteralText(String text) {
		this.text = text;
	}

	private final String text;

	@Override
	public String getString() {
		return this.text;
	}

	@Override
	public String getDisplayString() {
		return ;
	}

	@Override
	public Component toMinecraftComponent() {
		return null;
	}
}
