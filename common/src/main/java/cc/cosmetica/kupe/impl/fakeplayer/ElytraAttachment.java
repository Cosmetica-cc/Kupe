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

import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.gui.GUIPlayer.ElytraProperties;
import cc.cosmetica.kupe.impl.DirectTexture;
import cc.cosmetica.kupe.mixin.fakeplayer.ElytraModelAccessor;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.equipment.EquipmentAssets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;
import java.util.UUID;

public class ElytraAttachment implements GUIPlayer.Attachment<ElytraProperties> {
	@Override
	public void submitToRenderState(GUIPlayer component, ElytraProperties configuration, Quaternionf cameraOrientation, AvatarRenderState renderState) {
		if (renderState instanceof GuiPlayerAvatarRenderState guitar) {
			guitar.elytraProperties = configuration;
		}

		renderState.skin = new PlayerSkin(
				renderState.skin.body(),
				renderState.skin.cape(),
				new DirectTexture(configuration.texture),
				renderState.skin.model(),
				true
		);
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
		Identifier customElytra = PlayerUtils.getTexture(uuid, MinecraftProfileTexture.Type.CAPE);
		if (customElytra != null) {
			return new ElytraProperties(new ResourceKey(customElytra), false, false);
		}
		return ElytraProperties.DEFAULT;
	}

	@Override
	public boolean defaultEnable() {
		return false;
	}

	/// Render

	public static void submitWings(@NotNull GUIPlayer.ElytraProperties elytraProperties, AvatarRenderState state,
								   ElytraModel model, int outlineColour,
								   PoseStack stack, SubmitNodeCollector collector, int lightCoords,
								   EquipmentAssetManager equipmentAssets) {
		// based on ElytraModel#setupAnim
		state.elytraRotX = 0.2617994f;
		state.elytraRotZ = -0.2617994f;

		if (state.isCrouching) {
			state.elytraRotX = 0.6981317f;
			state.elytraRotZ = -0.7853982f;
			state.elytraRotY = 0.08726646f;
		}

		// based on InventoryScreen
		Identifier playerElytraTexture = elytraProperties.texture;
		stack.pushPose();
		stack.translate(0.0F, 0.0F, 0.125F);

		submitWingsLayer(
				elytraProperties,
				equipmentAssets,
				state,
				model,
				stack,
				collector,
				lightCoords,
				playerElytraTexture,
				outlineColour,
				0
		);

		stack.popPose();
	}

	// EquipmentLayerRenderer#renderLayers
	private static void submitWingsLayer(@NotNull GUIPlayer.ElytraProperties elytraProperties,
										 EquipmentAssetManager equipmentAssets,
										 AvatarRenderState state,
										 ElytraModel model,
										 PoseStack stack, SubmitNodeCollector collector, int lightCoords,
										 Identifier layerTexture, int outlineColour, final int order) {
		List<EquipmentClientInfo.Layer> layers = equipmentAssets.get(EquipmentAssets.ELYTRA).getLayers(EquipmentClientInfo.LayerType.WINGS);

		if (!layers.isEmpty()) {
			boolean renderFoil = elytraProperties.glint;
			int nextOrder = order;
			RenderType renderType = elytraProperties.translucent ? RenderTypes.entityTranslucent(layerTexture) : RenderTypes.armorCutoutNoCull(layerTexture);

			for (EquipmentClientInfo.Layer _ : layers) {
				int colour = 0xFFFFFFFF; // no tint

				if (colour != 0) {
					collector.order(nextOrder++)
							.submitModel(
									model, state, stack, renderType, lightCoords, OverlayTexture.NO_OVERLAY, colour, null, outlineColour, null
							);
					if (renderFoil) {
						collector.order(nextOrder++)
								.submitModel(model, state, stack, RenderTypes.armorEntityGlint(), lightCoords, OverlayTexture.NO_OVERLAY, colour, null, outlineColour, null);
					}

					renderFoil = false;
				}
			}
		}
	}
}
