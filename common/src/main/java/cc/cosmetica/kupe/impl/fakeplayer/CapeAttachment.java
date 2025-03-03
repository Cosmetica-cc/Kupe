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

package cc.cosmetica.kupe.impl.fakeplayer;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.gui.FakePlayer;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class CapeAttachment implements FakePlayer.Attachment<ResourceLocation> {
	@Override
	public void render(Canvas canvas, ResourceLocation configuration, Quaternion cameraOrientation, MultiBufferSource bufferSource, int packedLight) {

	}

	@Override
	public ResourceLocation getDynamicConfiguration(UUID uuid) {
		return null;
	}
}
