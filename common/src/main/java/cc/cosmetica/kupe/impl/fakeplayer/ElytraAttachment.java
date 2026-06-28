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
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ElytraAttachment implements GUIPlayer.Attachment<ElytraProperties> {
	@Override
	public void submitToRenderState(GUIPlayer component, ElytraProperties configuration, Quaternionf cameraOrientation, AvatarRenderState renderState) {
		// TODO reimplement Kupe transparent control?
		renderState.chestEquipment = new ItemStack(Items.ELYTRA);
		if (configuration.glint) {
			renderState.chestEquipment.enchant(Holder.direct(new Enchantment(
					Component.empty(),
					null,
					HolderSet.empty(),
					// DataComponentMap.EMPTY crashes
					new DataComponentMap() {
						@Override
						public Set<DataComponentType<?>> keySet() {
							return Collections.emptySet();
						}

						@Override
						public <T> @org.jspecify.annotations.Nullable T get(DataComponentType<? extends T> type) {
							return null;
						}
					}
			)), 1);
		}
		renderState.skin = renderState.skin.with(new PlayerSkin.Patch(
				Optional.empty(),
				Optional.empty(),
				Optional.of(new ClientAsset.ResourceTexture(configuration.texture)),
				Optional.empty()
		));
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
}
