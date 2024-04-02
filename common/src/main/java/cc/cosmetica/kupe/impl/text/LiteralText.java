package cc.cosmetica.kupe.impl.text;

import cc.cosmetica.kupe.api.Text;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

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
		return this.text;
	}

	@Override
	public Component toMinecraftComponent() {
		return new TextComponent(this.text);
	}

	@Override
	public String toString() {
		return "LiteralText(\"" + text + "\")";
	}
}
