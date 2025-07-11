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

import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.impl.text.VanillaText;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Internal utilities for {@link cc.cosmetica.kupe.api.gui.GUIPlayer}.
 */
public class PlayerUtils {
    private static Map<UUID, @Nullable GameProfile> cache = new HashMap<>();
    private static MinecraftSessionService sessionService;

    public static void createNewCache(YggdrasilAuthenticationService authService) {
        sessionService = authService.createMinecraftSessionService();
        cache = new HashMap<>();
    }

    public static Text getNameTag(UUID uuid) {
//        GameProfile profile = new GameProfile();

        if (Minecraft.getInstance().level == null) {
            if (uuid.equals(UUIDTypeAdapter.fromString(Minecraft.getInstance().getUser().getUuid()))) {
                // TODO (see below. this way doesn't preserve formatting)
                return Text.literal(Minecraft.getInstance().getUser().getName());
            } else {
                return Text.literal("Player");
            }
        } else {
            Level level = Minecraft.getInstance().level;
            AbstractClientPlayer player = (AbstractClientPlayer) level.getPlayerByUUID(uuid);

            if (player == null) {
                return Text.literal("Player");
            } else {
                // this way preserves formatting.
                return new VanillaText(player.getDisplayName());
            }
        }
    }

    /**
     * Get the skin of the given UUID.
     * @param uuid the uuid of the player.
     * @return the resource location of the skin.
     */
    public static ResourceLocation getSkin(UUID uuid, ResourceLocation existing) {
        // Get skin for cached profile
        GameProfile profile = cache.get(uuid);

        if (profile != null) {
            // get texture
            Minecraft minecraft = Minecraft.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> properties = minecraft.getSkinManager().getInsecureSkinInformation(profile);
            if (properties.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                return minecraft.getSkinManager().registerTexture(properties.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }
        }

        // Fallback 1: player info from server
        if (Minecraft.getInstance().getConnection() != null) {
            PlayerInfo loadedProfile = Minecraft.getInstance().getConnection().getPlayerInfo(uuid);

            if (loadedProfile != null) {
                return loadedProfile.getSkinLocation();
            }
        }

        // Fallback 2: start profile look up, use existing default skin
        if (!cache.containsKey(uuid)) {
            cache.put(uuid, null);

            // look up new profile
            // TODO async
            GameProfile profile1 = new GameProfile(uuid, null);
            sessionService.fillProfileProperties(profile1, true);
            Property property = Iterables.getFirst(profile1.getProperties().get("textures"), null);
            if (property != null) {
                cache.put(uuid, profile1);
            } // else prefer fallback 1. Don't spam the session server.
        }

        return existing;
    }
}
