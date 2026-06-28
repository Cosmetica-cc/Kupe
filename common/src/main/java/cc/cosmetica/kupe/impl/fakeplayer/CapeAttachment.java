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

import cc.cosmetica.kupe.api.MatrixStack;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.maths.Vec3;
import cc.cosmetica.kupe.impl.ExtendedPlayerModel;
import cc.cosmetica.kupe.impl.KupePoseStack;
import cc.cosmetica.kupe.mixin.fakeplayer.PlayerCapeModelAccessor;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Optional;
import java.util.UUID;

public class CapeAttachment implements GUIPlayer.Attachment<GUIPlayer.CapeProperties> {
	@Override
	public void submitToRenderState(GUIPlayer component, GUIPlayer.CapeProperties configuration, Quaternionf cameraOrientation, AvatarRenderState renderState) {
		if (configuration.getTexture().isPresent()) {
			renderState.showCape = true;
			renderState.skin = renderState.skin.with(new PlayerSkin.Patch(
					Optional.empty(),
					Optional.of(),
					Optional.empty(),
					Optional.empty()
			));
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
