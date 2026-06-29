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

import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.impl.DirectTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.UUID;

public class CapeAttachment implements GUIPlayer.Attachment<GUIPlayer.CapeProperties> {
	@Override
	public void submitToRenderState(GUIPlayer component, GUIPlayer.CapeProperties configuration, Quaternionf cameraOrientation, AvatarRenderState renderState) {
		if (configuration.getTexture().isPresent()) {
			renderState.showCape = true;
			renderState.skin = new PlayerSkin(
					renderState.skin.body(),
					new DirectTexture(configuration.getTexture().get()),
					renderState.skin.elytra(),
					renderState.skin.model(),
					true
			);
		}
	}

	@Override
	public GUIPlayer.CapeProperties getDynamicConfiguration(UUID uuid) {
		// Check cape providers first
		for (GUIPlayer.CapeProvider provider : PlayerUtils.getCapeProviders()) {
			@Nullable GUIPlayer.CapeProperties texture = provider.getCapeTexture(uuid);

			if (texture != null) {
				return texture;
			}
		}

		// Default
		return new GUIPlayer.CapeProperties(PlayerUtils.getTexture(uuid, MinecraftProfileTexture.Type.CAPE));
	}
}
