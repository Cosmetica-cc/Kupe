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

import java.util.regex.Pattern;

/**
 * A text that will display with Minecraft colour code rules.
 */
public abstract class MinecraftText implements Text {
    private static final Pattern COLOUR_CODE_PATTERN = Pattern.compile("§[0-9A-Fa-fklmnorKLMNOR]");

    public boolean isEmpty() {
        String displayString = this.getDisplayString();
        displayString = COLOUR_CODE_PATTERN.matcher(displayString).replaceAll("");
        return displayString.trim().isEmpty();
    }
}
