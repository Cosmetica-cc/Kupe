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
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Internal utilities for {@link cc.cosmetica.kupe.api.gui.GUIPlayer}.
 */
public class PlayerUtils {
    private static Map<UUID, @Nullable GameProfile> cache = new HashMap<>();
    private static MinecraftSessionService sessionService;

    public static void createNewCache(MinecraftSessionService minecraftSessionService) {
        sessionService = minecraftSessionService;
        cache = new HashMap<>();
    }

    public static Text getUsername(UUID uuid) {
        // Get name for cached profile
        GameProfile profile = cache.get(uuid);
        if (profile != null) {
            return Text.literal(profile.getName());
        }

        // Fallback 1: own username
        if (uuid.equals(UUIDTypeAdapter.fromString(Minecraft.getInstance().getUser().getUuid()))) {
            return Text.literal(Minecraft.getInstance().getUser().getName());
        }

        // Fallback 2: look up for cache
        startProfileLookup(uuid);
        return Text.literal("Player");
    }

    /**
     * Get the skin of the given UUID.
     * @param uuid the uuid of the player.
     * @param existing the fallback skin.
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
        startProfileLookup(uuid);
        return existing;
    }

    /**
     * Get the resource location for a custom cape or elytra. Can also be used to load skin, but {@link PlayerUtils#getSkin(UUID, ResourceLocation)} is preferred.
     * Unlike other methods, this does not automatically start a look-up.
     * @param uuid the player's uuid.
     * @param type the texture type.
     * @return the texture, or null if one could not be loaded.
     */
    public static @Nullable ResourceLocation getTexture(UUID uuid, MinecraftProfileTexture.Type type) {
        // Get skin for cached profile
        GameProfile profile = cache.get(uuid);

        if (profile != null) {
            // get texture
            Minecraft minecraft = Minecraft.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> properties = minecraft.getSkinManager().getInsecureSkinInformation(profile);
            if (properties.containsKey(type)) {
                return minecraft.getSkinManager().registerTexture(properties.get(type), type);
            }
        }

        return null;
    }

    private static void startProfileLookup(UUID uuid) {
        if (!cache.containsKey(uuid)) {
            cache.put(uuid, null);

            // look up new profile
            // TODO async
            GameProfile profile = new GameProfile(uuid, null);
            profile = sessionService.fillProfileProperties(profile, true);
            Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);
            if (property != null) {
                Objects.requireNonNull(profile.getName(), "Filled Game Profile is missing username?");
                cache.put(uuid, profile);
            } // else prefer fallback 1. Don't spam the session server.
        }
    }
}
