package cc.cosmetica.kupe.impl.text;

import cc.cosmetica.kupe.api.Text;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public enum EmptyText implements Text {
	INSTANCE;

	@Override
	public String getString() {
		return "";
	}

	@Override
	public String getDisplayString() {
		return "";
	}

	@Override
	public Component toMinecraftComponent() {
		return TextComponent.EMPTY;
	}
}
