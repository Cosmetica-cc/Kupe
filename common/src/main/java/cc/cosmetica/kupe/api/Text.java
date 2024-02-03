package cc.cosmetica.kupe.api;

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

	/**
	 * Convert this component to its minecraft version.
	 * @return the minecraft component from this text.
	 */
	net.minecraft.network.chat.Component toMinecraftComponent();
}
