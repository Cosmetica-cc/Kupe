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
import cc.cosmetica.kupe.util.Cache;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Arrays;

/**
 * Component for plain text.
 */
public class TranslatableText extends MinecraftText implements Text {
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
