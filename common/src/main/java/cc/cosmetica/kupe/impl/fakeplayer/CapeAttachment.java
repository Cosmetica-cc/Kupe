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
import cc.cosmetica.kupe.api.MatrixStack;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.maths.Vec3;
import cc.cosmetica.kupe.impl.ExtendedPlayerModel;
import cc.cosmetica.kupe.impl.KupePoseStack;
import cc.cosmetica.kupe.mixin.fakeplayer.PlayerCapeModelAccessor;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.UUID;

public class CapeAttachment implements GUIPlayer.Attachment<GUIPlayer.CapeProperties> {
	@Override
	public void render(GUIPlayer player, PlayerModel playerModel, GUIPlayer.Posture posture, PoseStack stackIn, GUIPlayer.CapeProperties configuration, Quaternionf cameraOrientation, MultiBufferSource bufferSource, int packedLight) {
		if (configuration.getTexture().isPresent()) {
			MatrixStack stack = new KupePoseStack(stackIn);
			stack.push();
			stack.translate(0.0D, 0.0D, 0.125D);
			double d = 0;
			double e = 0;
			double m = 0;
			float n = posture.yRotBody;
			double o = Mth.sin(n * 0.017453292F);
			double p = -Mth.cos(n * 0.017453292F);
			float q = (float)e * 10.0F;
			q = Mth.clamp(q, -6.0F, 32.0F);
			float r = (float)(d * o + m * p) * 100.0F;
			r = Mth.clamp(r, 0.0F, 150.0F);
			float s = (float)(d * p - m * o) * 100.0F;
			s = Mth.clamp(s, -20.0F, 20.0F);
			if (r < 0.0F) {
				r = 0.0F;
			}

			if (posture.sneaking) {
				q += 25.0F;
			}

			stack.rotate(Vec3.XP, 6.0F + r / 2.0F + q, true);
			stack.rotate(Vec3.ZP, s / 2.0F, true);
			stack.rotate(Vec3.YP, 180.0F - s / 2.0F, true);
			stack.scale(-1, 1, -1);
			VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(configuration.getTexture().get()));
			((PlayerCapeModelAccessor)((ExtendedPlayerModel) playerModel).getCape()).getCape().render(stackIn, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
			stack.pop();
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
