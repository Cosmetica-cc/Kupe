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
import cc.cosmetica.kupe.api.gui.FakePlayer;
import cc.cosmetica.kupe.api.gui.FakePlayer.ElytraProperties;
import cc.cosmetica.kupe.mixin.fakeplayer.ElytraModelAccessor;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ElytraAttachment implements FakePlayer.Attachment<ElytraProperties> {
	private final ElytraModel<?> elytraModel = new ElytraModel<>();

	@Override
	public void render(FakePlayerRenderer renderer, Canvas canvas, ElytraProperties configuration, Quaternion cameraOrientation, MultiBufferSource bufferSource, int packedLight) {
		MatrixStack stack = canvas.getStack();

		stack.push();
		stack.translate(0.0, 0.0, 0.125);

//		((EntityModel)this.getParentModel()).copyPropertiesTo(this.elytraModel);
//		{entityModel.attackTime = this.attackTime;
//		entityModel.riding = this.riding;
//		entityModel.young = this.young;}

		RenderType renderType = configuration.translucent ? RenderType.entityTranslucent(configuration.texture)
				: RenderType.armorCutoutNoCull(configuration.texture);

		this.setupAnim(renderer, (ElytraModelAccessor) elytraModel, 0, 0, 0, renderer.yRotHead, renderer.xRot);
		VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(bufferSource, renderType, false, configuration.glint);
		this.elytraModel.renderToBuffer(stack.getMinecraftStack(), vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
		stack.pop();
	}

	// based on ElytraModel#setupAnim
	private void setupAnim(FakePlayerRenderer renderer, ElytraModelAccessor elytra, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float f = 0.2617994f;
		float g = -0.2617994f;
		float h = 0.0f;
		float i = 0.0f;
		if (false /* fall flying */) {
			float j = 1.0f;
			Vec3 velocity = Vec3.ZERO;
			if (velocity.y < 0.0) {
				Vec3 vec32 = velocity.normalize();
				j = 1.0f - (float)Math.pow(-vec32.y, 1.5);
			}
			f = j * 0.34906584f + (1.0f - j) * f;
			g = j * -1.5707964f + (1.0f - j) * g;
		} else if (renderer.sneaking) {
			f = 0.6981317f;
			g = -0.7853982f;
			h = 3.0f;
			i = 0.08726646f;
		}

		ModelPart leftWing = elytra.getLeftWing();
		ModelPart rightWing = elytra.getRightWing();

		leftWing.x = 5.0f;
		leftWing.y = h;

		leftWing.xRot = f;
		leftWing.zRot = g;
		leftWing.yRot = i;

		// copy left wing to right wing
		rightWing.x = -leftWing.x;
		rightWing.yRot = -leftWing.yRot;
		rightWing.y = leftWing.y;
		rightWing.xRot = leftWing.xRot;
		rightWing.zRot = -leftWing.zRot;
	}

	@Override
	public ElytraProperties getDynamicConfiguration(UUID uuid) {
		return ElytraProperties.DEFAULT;
	}

	@Override
	public boolean defaultEnable() {
		return false;
	}
}
