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

import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.impl.DirectTexture;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.*;

/**
 * Renderer for {@link GUIPlayer}.
 * This is largely adapted from code used in the vanilla game to render players. So take the licensing of this file with
 * many grains of salt (this file should not be treated as under the same license as the project).
 */
public final class FakePlayerRenderer {
	// properties, exposed
	public PlayerUtils.Skin skin;

	public GUIPlayer.Nametag username;
	public List<GUIPlayer.Nametag> nametags = new ArrayList<>();
	public boolean showNametag = false;

	// properties, internal
	private PlayerRenderMode renderMode = PlayerRenderMode.NORMAL;

	private Quaternionf cameraOrientation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
	public Set<PlayerModelPart> shownParts = Sets.newHashSet(PlayerModelPart.values());

	public static void renderNametags(@NotNull List<GUIPlayer.Nametag> nameTags, EntityRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera, int offset) {
		stack.scale(-1, 1, -1);

		// don't do 0 (username), handled by vanilla
		for (int i = 1; i < nameTags.size(); i++) {
			FakePlayerRenderer.renderNametag(nameTags.get(i), state, stack, collector, camera, offset);
		}
	}

	public static void renderNametag(GUIPlayer.Nametag nametag, EntityRenderState state, PoseStack stack,
									 SubmitNodeCollector collector, CameraRenderState camera, int offset) {
        assert state.nameTagAttachment != null;

		stack.pushPose();

        Vec3 scaledAttachment = new Vec3(state.nameTagAttachment.x, state.nameTagAttachment.y / nametag.scale, state.nameTagAttachment.z);
		stack.scale(nametag.scale, nametag.scale, nametag.scale);

		Component component = nametag.text.toMinecraftComponent();

		collector.submitNameTag(
				stack,
				scaledAttachment,
				offset,
				component,
				!state.isDiscrete,
				state.lightCoords,
				camera);

		stack.popPose();
		// Cosmetica lore is 0.15F? TODO add control to this translation to Kupe API in future.
		stack.translate(0.0F, 0.25875F * nametag.scale, 0.0F);
	}

	// inventoryScreen::extractEntityInInventoryFollowsMouse
	public void renderFakePlayer(final GUIPlayer player,
								 Quaternionf cameraOrientation,
								 final SubmitNodeCollector collector,
								 final PoseStack stack,
								 final EntityRenderDispatcher dispatcher) {
		Objects.requireNonNull(this.skin, "No skin provided to Fake Player renderer!");

		if (this.renderMode == PlayerRenderMode.NO_RENDER) {
			return;
		}
		this.cameraOrientation = cameraOrientation;

		AvatarRenderState renderState = this.extractRenderState(player);

//		float xAngle = rotation;
//
//		renderState.bodyRot = 180.0F + xAngle * 20.0F;
//		renderState.yRot = xAngle * 20.0F;
//		if (renderState.pose != Pose.FALL_FLYING) {
//			renderState.xRot = -yAngle * 20.0F;
//		} else {
//			renderState.xRot = 0.0F;
//		}
//
		renderState.boundingBoxWidth /= renderState.scale;
		renderState.boundingBoxHeight /= renderState.scale;
		renderState.scale = 1.0F;

//		Vector3f translation = new Vector3f(0.0F, renderState.boundingBoxHeight / 2.0F + yOffset, 0.0F);
		CameraRenderState cameraRenderState = new CameraRenderState();
		cameraRenderState.orientation = this.cameraOrientation; //overriddenCameraAngle.conjugate(new Quaternionf()).rotateY((float)Math.PI);

		dispatcher.submit(renderState, cameraRenderState, (double)0.0F, (double)0.0F, (double)0.0F, stack, collector);
	}

	private @NotNull AvatarRenderState extractRenderState(GUIPlayer player) {
		GuiPlayerAvatarRenderState arsTechnica = new GuiPlayerAvatarRenderState();

		// skin
		arsTechnica.skin = new PlayerSkin(
				new DirectTexture(this.skin.texture),
				null,
				null,
				this.skin.slim ? PlayerModelType.SLIM : PlayerModelType.WIDE,
				true // using this skin is intended behaviour
		);

		// shown parts
		this.setModelProperties(arsTechnica);

		// public posture flags
		final GUIPlayer.Posture pose = player.pose;

		arsTechnica.bodyRot = pose.yRotBody;
		arsTechnica.xRot = pose.xRot;
		arsTechnica.isCrouching = pose.sneaking;
		arsTechnica.yRot = pose.yRotHead - pose.yRotBody;
		arsTechnica.isUpsideDown = pose.upsideDown;

		ArmPostures armPosture = this.createArmPostures(pose);
		arsTechnica.leftArmPose = armPosture.leftArmPose;
		arsTechnica.rightArmPose = armPosture.rightArmPose;
//		pose.isLeftHanded; -- unused.

		// remaining thingamajigs from the renderer
		switch (this.renderMode) {
		case NO_RENDER:
			throw new IllegalStateException("Can't be extracting render states for NO_RENDER render mode");
		case GLOWING:
			arsTechnica.outlineColor = 0xFFFFFF;
			break;
		case INVISIBLE:
			arsTechnica.isInvisible = true;
			break;
		case NORMAL:
			break;
        }

		if (this.showNametag) {
			if (!this.nametags.isEmpty()) {
				arsTechnica.nameTagAttachment = new Vec3(0, 1.8F, 0);
				// TODO better old-kupe-style nametag handling
				arsTechnica.nameTag = this.nametags.get(0).text.toMinecraftComponent();
				arsTechnica.nameTags = this.nametags;
			}
		}

		// Attachments
		Iterator<GUIPlayer.Attachment<?>> iterator = player.getRenderingAttachments();

		while (iterator.hasNext()) {
			GUIPlayer.Attachment<?> layer = iterator.next();
			Object configuration = player.getConfiguration(layer);
			if (configuration != null) {
				((GUIPlayer.Attachment)layer).submitToRenderState(player, configuration, cameraOrientation, arsTechnica);
			}
		}

        return arsTechnica;
    }

	// PlayerRenderer#setModelProperties()
	private void setModelProperties(AvatarRenderState ars) {
		ars.showHat = this.shownParts.contains(PlayerModelPart.HAT);
		ars.showJacket = this.shownParts.contains(PlayerModelPart.JACKET);
		ars.showLeftPants = this.shownParts.contains(PlayerModelPart.LEFT_PANTS_LEG);
		ars.showRightPants = this.shownParts.contains(PlayerModelPart.RIGHT_PANTS_LEG);
		ars.showLeftSleeve = this.shownParts.contains(PlayerModelPart.LEFT_SLEEVE);
		ars.showRightSleeve = this.shownParts.contains(PlayerModelPart.RIGHT_SLEEVE);
	}

	private ArmPostures createArmPostures(GUIPlayer.Posture pose) {
		HumanoidModel.ArmPose leftArmPose = pose.leftArmRaised  ? HumanoidModel.ArmPose.ITEM : HumanoidModel.ArmPose.EMPTY;
		HumanoidModel.ArmPose rightArmPose = pose.rightArmRaised ? HumanoidModel.ArmPose.ITEM : HumanoidModel.ArmPose.EMPTY;
		return new ArmPostures(leftArmPose, rightArmPose);
	}
	///

	private Vec3 getRenderOffset(GUIPlayer.Posture posture) {
		return posture.sneaking ? new Vec3(0.0D, -0.125D, 0.0D) : Vec3.ZERO;
	}

	public enum PlayerRenderMode {
		INVISIBLE,
		NORMAL,
		GLOWING,
		NO_RENDER
	}
//
//	private RenderType getRenderType(PlayerRenderMode mode) {
//		switch (mode) {
//		case INVISIBLE:
//			return RenderTypes.entityTranslucentCullItemTarget(this.skin.texture);
//		case NORMAL:
//			return this.model.renderType(this.skin.texture);
//		case GLOWING:
//			return RenderTypes.outline(this.skin.texture);
//		case NO_RENDER:
//		default:
//			return null;
//		}
//	}

	private static int getOverlayCoords(float u) {
		return OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(false));
	}

	private record ArmPostures(HumanoidModel.ArmPose leftArmPose, HumanoidModel.ArmPose rightArmPose) {
	}
}
