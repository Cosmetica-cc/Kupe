/*
 * Copyright 2024 Cosmetica
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
