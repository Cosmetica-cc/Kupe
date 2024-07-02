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

package cc.cosmetica.kupe.impl.fakeplayer;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.FakePlayer;

import java.util.UUID;

public class NameTagAttachment implements FakePlayer.Attachment<Text> {
	@Override
	public void render(Canvas canvas, Text configuration, int packedLight) {

	}

	@Override
	public Text getUserConfiguration(UUID uuid) {
		return null;
	}

	@Override
	public boolean isNameTag() {
		return true;
	}
}
