package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.impl.LeavesSandbox;
import cc.cosmetica.kupe.impl.text.LiteralText;
import cc.cosmetica.kupe.impl.text.TranslatableText;
import net.minecraft.network.chat.Component;

/**
 * Base class for a text component.
 */
public interface Text {
	/**
	 * Get the internal raw string of this component. This does not include any children.
	 */
	String getString();

	/**
	 * Get the string that will be visually displayed to the user.
	 * @return the string that will be displayed to the user.
	 */
	String getDisplayString();

	static Text literal(String text) {
		return new LiteralText(text);
	}

	static Text translatable(String text, String... format) {
		return new TranslatableText(text, (Object[])format);
	}

	/**
	 * Convert this component to its minecraft version.
	 * @return the minecraft component from this text.
	 */
	@LeavesSandbox
	Component toMinecraftComponent();
}
