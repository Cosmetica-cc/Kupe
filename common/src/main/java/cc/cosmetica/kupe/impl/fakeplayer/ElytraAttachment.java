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
import cc.cosmetica.kupe.api.gui.GUIPlayer.ElytraProperties;
import cc.cosmetica.kupe.impl.ExtendedPlayerModel;
import cc.cosmetica.kupe.mixin.fakeplayer.ElytraModelAccessor;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ElytraAttachment implements GUIPlayer.Attachment<ElytraProperties> {
	@Override
	public void render(GUIPlayer component, PlayerModel model, GUIPlayer.Posture posture, Canvas canvas, ElytraProperties configuration, Quaternion cameraOrientation, MultiBufferSource bufferSource, int packedLight) {
		if (model instanceof ExtendedPlayerModel) {
			ElytraModel<?> elytraModel = ((ExtendedPlayerModel<?>) model).getElytra();

			MatrixStack stack = canvas.getStack();

			stack.push();
			stack.scale(2,2,2);
			stack.translate(0.0, -24/32.0, 0.125/2);

			RenderType renderType = configuration.translucent ? RenderType.entityTranslucent(configuration.texture)
					: RenderType.armorCutoutNoCull(configuration.texture);

			this.setupAnim(posture, (ElytraModelAccessor) elytraModel, 0, 0, 0, posture.yRotHead, posture.xRot);
			VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(bufferSource, renderType, false, configuration.glint);
			elytraModel.renderToBuffer(stack.getMinecraftStack(), vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
			stack.pop();
		}
 	}

	// based on ElytraModel#setupAnim
	private void setupAnim(GUIPlayer.Posture posture, ElytraModelAccessor elytra, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float xRot = 0.2617994f;
		float zRot = -0.2617994f;
		float wingY = 0.0f;
		float yRot = 0.0f;

		if (posture.sneaking) {
			xRot = 0.6981317f;
			zRot = -0.7853982f;
			wingY = 3.0f;
			yRot = 0.08726646f;
		}

		ModelPart leftWing = elytra.getLeftWing();
		ModelPart rightWing = elytra.getRightWing();

		leftWing.x = 5.0f;
		leftWing.y = wingY;

		leftWing.xRot = xRot;
		leftWing.zRot = zRot;
		leftWing.yRot = yRot;

		// copy left wing to right wing
		rightWing.x = -leftWing.x;
		rightWing.yRot = -leftWing.yRot;
		rightWing.y = leftWing.y;
		rightWing.xRot = leftWing.xRot;
		rightWing.zRot = -leftWing.zRot;
	}

	@Override
	public ElytraProperties getDynamicConfiguration(UUID uuid) {
		// Check cape providers first
		for (GUIPlayer.CapeProvider provider : PlayerUtils.getCapeProviders()) {
			@Nullable GUIPlayer.ElytraProperties texture = provider.getElytraTexture(uuid);

			if (texture != null) {
				return texture;
			}
		}

		// Default
		ResourceLocation customElytra = PlayerUtils.getTexture(uuid, MinecraftProfileTexture.Type.CAPE);
		if (customElytra != null) {
			return new ElytraProperties(customElytra, false, false);
		}
		return ElytraProperties.DEFAULT;
	}

	@Override
	public boolean defaultEnable() {
		return false;
	}
}
