/*
 * Copyright 2024, 2025 Cosmetica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.cosmetica.kupe.api;

import cc.cosmetica.kupe.impl.LeavesSandbox;
import cc.cosmetica.kupe.impl.text.EmptyText;
import cc.cosmetica.kupe.impl.text.LiteralText;
import cc.cosmetica.kupe.impl.text.TranslatableText;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

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

	static Text empty() {
		return EmptyText.INSTANCE;
	}

	/**
	 * Get a text instance that contains the provided text as-is.
	 * @param text the text to display.
	 * @return a text object with the given text literal.
	 */
	static Text literal(String text) {
		return new LiteralText(text);
	}

	/**
	 * Get a text instance that uses the given key as a translation key, formatted with the given format items.
	 * @param key the translation key.
	 * @param format the format items.
	 * @return a text object which translates based on the provided key and format.
	 */
	static Text translatable(String key, String... format) {
		return new TranslatableText(key, (Object[])format);
	}

	/**
	 * Convert this component to its minecraft version.
	 * @return the minecraft component from this text.
	 */
	@LeavesSandbox
	Component toMinecraftComponent();

	/*
	 * Common translation keys used in GUI development.
	 */

	/**
	 * "ON"
	 */
	Text OPTION_ON = translatable("options.on");
	/**
	 * "OFF"
	 */
	Text OPTION_OFF = translatable("options.off");
	/**
	 * "Done"
	 */
	Text GUI_DONE = translatable("gui.done");
	/**
	 * "Cancel"
	 */
	Text GUI_CANCEL = translatable("gui.cancel");
	/**
	 * "Yes"
	 */
	Text GUI_YES = translatable("gui.yes");
	/**
	 * "No"
	 */
	Text GUI_NO = translatable("gui.no");
	/**
	 * "Proceed"
	 */
	Text GUI_PROCEED = translatable("gui.proceed");
	/**
	 * "Back"
	 */
	Text GUI_BACK = translatable("gui.back");
	/**
	 * "Failed to connect to the server"
	 */
	Text CONNECT_FAILED = translatable("connect.failed");
}
