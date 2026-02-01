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

package cc.cosmetica.kupe.impl.text;

import cc.cosmetica.kupe.api.Text;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

/**
 * Component for plain text.
 */
public class LiteralText extends MinecraftText implements Text {
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
