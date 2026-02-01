/*
 * Minecraft Code from which this is derived is All Rights Reserved, Mojang AB.
 * Modifications to said code are Copyright 2024, 2025 Cosmetica
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
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.MatrixStack;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.impl.PoseCanvas;
import cc.cosmetica.kupe.mixin.fakeplayer.HumanoidModelAccessor;
import cc.cosmetica.kupe.mixin.fakeplayer.PlayerModelAccessor;
import cc.cosmetica.kupe.util.GlobalMatrixStack;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * Renderer for {@link GUIPlayer}.
 * This is largely adapted from code used in the vanilla game to render players. So take the licensing of this file with
 * many grains of salt (this file should not be treated as under the same license as the project).
 */
public final class FakePlayerRenderer {
	// Lazy Model Loading
	// Important on newer mc versions
	private PlayerModel<AbstractClientPlayer> model;
	private boolean slimModel;

	private boolean lazyLoadModel() {
		if (this.model == null || (this.slimModel != this.skin.slim)) {
			this.model = new PlayerModel<>(0.0f, this.slimModel = this.skin.slim);
		}

		return true;
	}

	// properties, exposed
	public PlayerUtils.Skin skin;

	public GUIPlayer.Nametag username;
	public List<GUIPlayer.Nametag> nametags = new ArrayList<>();
	public boolean showNametag = false;

	// properties, internal
	private PlayerRenderMode renderMode = PlayerRenderMode.NORMAL;

	private Quaternion cameraOrientation = Quaternion.ONE;
	public Set<PlayerModelPart> shownParts = Sets.newHashSet(PlayerModelPart.values());

	public void render(GUIPlayer player, Context context, int left, int top, float extraScale, float lookX, float lookY) {
		// lazy load model (important on newer mc versions)
		if (!this.lazyLoadModel()) {
			return;
		}

		Objects.requireNonNull(this.skin, "No skin provided to Fake Player renderer!");

		float h = (float)Math.atan(lookX / 40.0F);
		float l = (float)Math.atan(lookY / 40.0F);
		MatrixStack stack = GlobalMatrixStack.INSTANCE;

		stack.push();
		stack.translate(left, top, 1050.0D);
		stack.scale(2.0F, 2.0F, -1.0F);
		//RenderSystem.applyModelViewMatrix();

		// view
		PoseStack viewStack = new PoseStack();
		viewStack.translate(0.0D, 0.0D, 1000.0D);
		viewStack.scale(extraScale, extraScale, extraScale);
		Quaternion zRotation = Vector3f.ZP.rotationDegrees(180.0F);
		Quaternion xRotation = Vector3f.XP.rotationDegrees(l * 20.0F);
		zRotation.mul(xRotation);
		viewStack.mulPose(zRotation);

		float rotationBody = 180.0F + h * 20.0F;
		float rotationMain = 180.0F + h * 40.0F;

		// rotate player to face lookX, lookY
		player.pose.yRotBody += rotationBody;
		player.pose.yRotHead += rotationMain;// yRotHead = yRot = getYRot(0);
		float xRotOld = player.pose.xRot;
		player.pose.xRot += -l * 20.0F;
		//Lighting.setupForEntityInInventory();

		xRotation.conj();
		this.cameraOrientation = xRotation;
		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

		RenderSystem.runAsFancy(() -> {
			// Above 1.16.5 we need to do an extra step here
			this.render(player, context, viewStack, bufferSource, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, 15728880);
		});
		bufferSource.endBatch();

		// restore
		player.pose.yRotBody -= rotationBody;
		player.pose.yRotHead -= rotationMain;
		player.pose.xRot = xRotOld;

		stack.pop();
		//RenderSystem.applyModelViewMatrix();
		Lighting.setupFor3DItems();
	}

	// EntityRenderDispatcher#render
	private void render(GUIPlayer player, Context context, PoseStack stack, MultiBufferSource bufferSource, double xOffset, double yOffset, double zOffset, float rotation, float delta, int light) {
		try {
			Vec3 vec3 = getRenderOffset(player.pose);
			double x = xOffset + vec3.x();
			double y = yOffset + vec3.y();
			double z = zOffset + vec3.z();
			stack.pushPose();
			stack.translate(x, y, z);

			// PlayerRenderer#render
			this.setModelProperties(player.pose);
			this.drawLivingEntity(player, context, rotation, delta, stack, bufferSource, light);
			// </PlayerRenderer#render>

			stack.popPose();
		} catch (Throwable var24) {
			CrashReport crashReport = CrashReport.forThrowable(var24, "Rendering GUI player in menu");
			crashReport.addCategory("GUI Player being rendered");

			CrashReportCategory category = crashReport.addCategory("Renderer details");
			category.setDetail("Location", xOffset + "," + yOffset + "," + zOffset);
			category.setDetail("Rotation", rotation);
			category.setDetail("Delta", delta);
			throw new ReportedException(crashReport);
		}
	}

	private Vec3 getRenderOffset(GUIPlayer.Posture posture) {
		return posture.sneaking ? new Vec3(0.0D, -0.125D, 0.0D) : Vec3.ZERO;
	}

	// PlayerRenderer#setModelProperties()
	private void setModelProperties(GUIPlayer.Posture posture) {
		PlayerModel playerModel = this.model;

		playerModel.setAllVisible(true);
		playerModel.hat.visible = this.shownParts.contains(PlayerModelPart.HAT);
		playerModel.jacket.visible = this.shownParts.contains(PlayerModelPart.JACKET);
		playerModel.leftPants.visible = this.shownParts.contains(PlayerModelPart.LEFT_PANTS_LEG);
		playerModel.rightPants.visible = this.shownParts.contains(PlayerModelPart.RIGHT_PANTS_LEG);
		playerModel.leftSleeve.visible = this.shownParts.contains(PlayerModelPart.LEFT_SLEEVE);
		playerModel.rightSleeve.visible = this.shownParts.contains(PlayerModelPart.RIGHT_SLEEVE);
		playerModel.crouching = posture.sneaking;

		playerModel.rightArmPose = posture.leftArmRaised ? HumanoidModel.ArmPose.ITEM : HumanoidModel.ArmPose.EMPTY;
		playerModel.leftArmPose = posture.rightArmRaised ? HumanoidModel.ArmPose.ITEM : HumanoidModel.ArmPose.EMPTY;
	}

	// LivingEntityRenderer#render
	private void drawLivingEntity(GUIPlayer player, Context context, float rotation, float delta, PoseStack stack, MultiBufferSource bufferSource, int light) {
		stack.pushPose();
		PlayerModel<AbstractClientPlayer> model = this.model;

		GUIPlayer.Posture posture = player.pose;

		model.attackTime = 0;
		model.riding = false;
		model.young = false;

		float yRotBody = posture.yRotBody; //player.getYRotBody(delta);
		float yRotHead = posture.yRotHead; //player.getYRotHead(delta);
		float yRotDiff = yRotHead - yRotBody;
		float bob = delta;

		float xRot = posture.xRot; //player.getXRot(delta);

		// Upside Down
		if (player.pose.upsideDown) {
			xRot *= -1.0F;
			yRotDiff *= -1.0F;
		}

		this.setupRotations(posture, stack, bob, yRotBody, delta);

		stack.scale(-1.0F, -1.0F, 1.0F);
		stack.scale(0.9375F, 0.9375F, 0.9375F); // PlayerRenderer#scale
		stack.translate(0.0D, -1.5010000467300415D, 0.0D);

		float animationSpeed = 0.0f;//Mth.lerp(delta, player.animationSpeedOld, player.animationSpeed);
		float animationPosition = 0.0f;//player.animationPosition - player.animationSpeed * (1.0F - delta);

		if (animationSpeed > 1.0F) {
			animationSpeed = 1.0F;
		}

		//model.prepareMobModel(player, o, n, delta); only does swim stuff, not necessary
		this.modelSetupAnim(player.pose.sneaking, player.pose.isLeftHanded, model, animationPosition, animationSpeed, bob, yRotDiff, xRot);

		RenderType renderType = this.getRenderType(this.renderMode);

		if (renderType != null) {
			VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
			int packedOverlayCoords = getOverlayCoords(0.0f);
			model.renderToBuffer(stack, vertexConsumer, light, packedOverlayCoords, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		// render layers

		// TODO refactor so it doesn't leave sandbox? Or refactor so it's fully out of context cause why force people
		// if they're just going to leave the sandbox every time anyway

		Canvas canvas = new PoseCanvas(stack, Minecraft.getInstance(), context, delta);
		Iterator<GUIPlayer.Attachment<?>> iterator = player.getRenderingAttachments();

		while (iterator.hasNext()) {
			GUIPlayer.Attachment<?> layer = iterator.next();
			Object configuration = player.getConfiguration(layer);
			if (configuration != null) {
				((GUIPlayer.Attachment)layer).render(player, this.model, posture, canvas, configuration, cameraOrientation, bufferSource, light);
			}
		}

		stack.popPose();

		// nametag
		if (this.showNametag) {
			stack.pushPose();
			for (int i = this.nametags.size() - 1; i >= 0; i--) {
				GUIPlayer.Nametag nametag = this.nametags.get(i);

				if (!nametag.text.isEmpty()) {
					stack.pushPose();
					this.renderNametag(nametag, canvas, bufferSource, light);
					stack.popPose();
					stack.translate(0, 0.25875f, 0);
				}
			}
			stack.popPose();
		}
	}

	private void renderNametag(GUIPlayer.Nametag nametag, Canvas canvas, MultiBufferSource bufferSource, int packedLight) {
		final Component name = nametag.text.toMinecraftComponent();
		final float scale = nametag.scale;
		PoseStack stack = canvas.getStack().getMinecraftStack();

		float yPosition = EntityType.PLAYER.getDimensions().height + 0.5F;

		stack.translate(0.0D, yPosition, 0.0D);
		stack.mulPose(cameraOrientation);
		stack.scale(-0.025F, -0.025F, 0.025F);
		stack.scale(scale, scale, scale);

		boolean fullyRender = true; // TODO !player.renderDiscreteNametag();
		int offsetForDeadmau5 = "deadmau5".equals(name.getString()) ? -10 : 0;

		Matrix4f pose = stack.last().pose();
		float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
		int k = (int)(backgroundOpacity * 255.0F) << 24;
		Font font = Minecraft.getInstance().font;
		float h = (float)(-font.width(name) / 2);
		font.drawInBatch(name, h, (float)offsetForDeadmau5, 0x20FFFFFF, false, pose, bufferSource, fullyRender, k, packedLight);

		if (fullyRender) {
			font.drawInBatch(name, h, (float)offsetForDeadmau5, -1, false, pose, bufferSource, false, 0, packedLight);
		}
	}

	public enum PlayerRenderMode {
		INVISIBLE,
		NORMAL,
		GLOWING,
		NO_RENDER
	}

	private RenderType getRenderType(PlayerRenderMode mode) {
		switch (mode) {
		case INVISIBLE:
			return RenderType.itemEntityTranslucentCull(this.skin.texture);
		case NORMAL:
			return this.model.renderType(this.skin.texture);
		case GLOWING:
			return RenderType.outline(this.skin.texture);
		case NO_RENDER:
		default:
			return null;
		}
	}

	private void modelSetupAnim(boolean sneaking, boolean leftHanded, PlayerModel<AbstractClientPlayer> model, float f, float g, float bob, float yRotDiff, float xRot) {
		model.head.yRot = yRotDiff * 0.017453292F;

		if (model.swimAmount > 0.0F) {
			model.head.xRot = ((HumanoidModelAccessor) model).invokeRotlerpRad(model.swimAmount, model.head.xRot, xRot * 0.017453292F);
		} else {
			model.head.xRot = xRot * 0.017453292F;
		}

		model.body.yRot = 0.0F;
		model.rightArm.z = 0.0F;
		model.rightArm.x = -5.0F;
		model.leftArm.z = 0.0F;
		model.leftArm.x = 5.0F;
		float k = 1.0F;

		if (k < 1.0F) {
			k = 1.0F;
		}

		model.rightArm.xRot = Mth.cos(f * 0.6662F + 3.1415927F) * 2.0F * g * 0.5F / k;
		model.leftArm.xRot = Mth.cos(f * 0.6662F) * 2.0F * g * 0.5F / k;
		model.rightArm.zRot = 0.0F;
		model.leftArm.zRot = 0.0F;
		model.rightLeg.xRot = Mth.cos(f * 0.6662F) * 1.4F * g / k;
		model.leftLeg.xRot = Mth.cos(f * 0.6662F + 3.1415927F) * 1.4F * g / k;
		model.rightLeg.yRot = 0.0F;
		model.leftLeg.yRot = 0.0F;
		model.rightLeg.zRot = 0.0F;
		model.leftLeg.zRot = 0.0F;
		ModelPart currentModel;

		if (model.riding) {
			currentModel = model.rightArm;
			currentModel.xRot += -0.62831855F;
			currentModel = model.leftArm;
			currentModel.xRot += -0.62831855F;
			model.rightLeg.xRot = -1.4137167F;
			model.rightLeg.yRot = 0.31415927F;
			model.rightLeg.zRot = 0.07853982F;
			model.leftLeg.xRot = -1.4137167F;
			model.leftLeg.yRot = -0.31415927F;
			model.leftLeg.zRot = -0.07853982F;
		}

		model.rightArm.yRot = 0.0F;
		model.leftArm.yRot = 0.0F;
		boolean rightHanded = !leftHanded;
		boolean bl4;

		bl4 = rightHanded ? model.leftArmPose.isTwoHanded() : model.rightArmPose.isTwoHanded();

		if (rightHanded != bl4) {
			poseLeftArm(model);
			poseRightArm(model);
		} else {
			poseRightArm(model);
			poseLeftArm(model);
		}

		if (model.crouching) {
			model.body.xRot = 0.5F;
			currentModel = model.rightArm;
			currentModel.xRot += 0.4F;
			currentModel = model.leftArm;
			currentModel.xRot += 0.4F;
			model.rightLeg.z = 4.0F;
			model.leftLeg.z = 4.0F;
			model.rightLeg.y = 12.2F;
			model.leftLeg.y = 12.2F;
			model.head.y = 4.2F;
			model.body.y = 3.2F;
			model.leftArm.y = 5.2F;
			model.rightArm.y = 5.2F;
		} else {
			model.body.xRot = 0.0F;
			model.rightLeg.z = 0.1F;
			model.leftLeg.z = 0.1F;
			model.rightLeg.y = 12.0F;
			model.leftLeg.y = 12.0F;
			model.head.y = 0.0F;
			model.body.y = 0.0F;
			model.leftArm.y = 2.0F;
			model.rightArm.y = 2.0F;
		}

		AnimationUtils.bobArms(model.rightArm, model.leftArm, 1.0F);

		if (model.swimAmount > 0.0F) {
			float l = f % 26.0F;
			float m = !leftHanded && model.attackTime > 0.0F ? 0.0F : model.swimAmount;
			float n = leftHanded && model.attackTime > 0.0F ? 0.0F : model.swimAmount;
			float o;

			if (l < 14.0F) {
				model.leftArm.xRot = ((HumanoidModelAccessor) model).invokeRotlerpRad(n, model.leftArm.xRot, 0.0F);
				model.rightArm.xRot = Mth.lerp(m, model.rightArm.xRot, 0.0F);
				model.leftArm.yRot = ((HumanoidModelAccessor) model).invokeRotlerpRad(n, model.leftArm.yRot, 3.1415927F);
				model.rightArm.yRot = Mth.lerp(m, model.rightArm.yRot, 3.1415927F);
				model.leftArm.zRot = ((HumanoidModelAccessor) model).invokeRotlerpRad(n, model.leftArm.zRot, 3.1415927F + 1.8707964F * ((HumanoidModelAccessor) model).invokeQuadraticArmUpdate(l) / ((HumanoidModelAccessor) model).invokeQuadraticArmUpdate(14.0F));
				model.rightArm.zRot = Mth.lerp(m, model.rightArm.zRot, 3.1415927F - 1.8707964F * ((HumanoidModelAccessor) model).invokeQuadraticArmUpdate(l) / ((HumanoidModelAccessor) model).invokeQuadraticArmUpdate(14.0F));
			} else if (l >= 14.0F && l < 22.0F) {
				o = (l - 14.0F) / 8.0F;
				model.leftArm.xRot = ((HumanoidModelAccessor) model).invokeRotlerpRad(n, model.leftArm.xRot, 1.5707964F * o);
				model.rightArm.xRot = Mth.lerp(m, model.rightArm.xRot, 1.5707964F * o);
				model.leftArm.yRot = ((HumanoidModelAccessor) model).invokeRotlerpRad(n, model.leftArm.yRot, 3.1415927F);
				model.rightArm.yRot = Mth.lerp(m, model.rightArm.yRot, 3.1415927F);
				model.leftArm.zRot = ((HumanoidModelAccessor) model).invokeRotlerpRad(n, model.leftArm.zRot, 5.012389F - 1.8707964F * o);
				model.rightArm.zRot = Mth.lerp(m, model.rightArm.zRot, 1.2707963F + 1.8707964F * o);
			} else if (l >= 22.0F && l < 26.0F) {
				o = (l - 22.0F) / 4.0F;
				model.leftArm.xRot = ((HumanoidModelAccessor) model).invokeRotlerpRad(n, model.leftArm.xRot, 1.5707964F - 1.5707964F * o);
				model.rightArm.xRot = Mth.lerp(m, model.rightArm.xRot, 1.5707964F - 1.5707964F * o);
				model.leftArm.yRot = ((HumanoidModelAccessor) model).invokeRotlerpRad(n, model.leftArm.yRot, 3.1415927F);
				model.rightArm.yRot = Mth.lerp(m, model.rightArm.yRot, 3.1415927F);
				model.leftArm.zRot = ((HumanoidModelAccessor) model).invokeRotlerpRad(n, model.leftArm.zRot, 3.1415927F);
				model.rightArm.zRot = Mth.lerp(m, model.rightArm.zRot, 3.1415927F);
			}

			model.leftLeg.xRot = Mth.lerp(model.swimAmount, model.leftLeg.xRot, 0.3F * Mth.cos(f * 0.33333334F + 3.1415927F));
			model.rightLeg.xRot = Mth.lerp(model.swimAmount, model.rightLeg.xRot, 0.3F * Mth.cos(f * 0.33333334F));
		}

		model.hat.copyFrom(model.head);

		model.leftPants.copyFrom(model.leftLeg);
		model.rightPants.copyFrom(model.rightLeg);
		model.leftSleeve.copyFrom(model.leftArm);
		model.rightSleeve.copyFrom(model.rightArm);
		model.jacket.copyFrom(model.body);

		ModelPart cloak = ((PlayerModelAccessor) model).getCloak();

		if (sneaking) {
			cloak.z = 1.4F;
			cloak.y = 1.85F;
		} else {
			cloak.z = 0.0F;
			cloak.y = 0.0F;
		}
	}

	private static void poseLeftArm(PlayerModel model) {
		switch(model.leftArmPose) {
		case EMPTY:
			model.leftArm.yRot = 0.0F;
			break;
		case BLOCK:
			model.leftArm.xRot = model.leftArm.xRot * 0.5F - 0.9424779F;
			model.leftArm.yRot = 0.5235988F;
			break;
		case ITEM:
			model.leftArm.xRot = model.leftArm.xRot * 0.5F - 0.31415927F;
			model.leftArm.yRot = 0.0F;
			break;
		}
	}

	private static void poseRightArm(PlayerModel model) {
		switch (model.rightArmPose) {
		case EMPTY:
			model.rightArm.yRot = 0.0F;
			break;
		case BLOCK:
			model.rightArm.xRot = model.rightArm.xRot * 0.5F - 0.9424779F;
			model.rightArm.yRot = -0.5235988F;
			break;
		case ITEM:
			model.rightArm.xRot = model.rightArm.xRot * 0.5F - 0.31415927F;
			model.rightArm.yRot = 0.0F;
			break;
		}
	}

	private void setupRotations(GUIPlayer.Posture posture, PoseStack stack, float f, float g, float h) {
		stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - g));

		// Upside Down
		if (posture.upsideDown) {
			stack.translate(0.0D, EntityType.PLAYER.getDimensions().height + 0.1, 0.0D);
			stack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		}
	}

	private static int getOverlayCoords(float u) {
		return OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(false));
	}
}
