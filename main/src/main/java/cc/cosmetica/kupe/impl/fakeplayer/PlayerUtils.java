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
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal utilities for {@link cc.cosmetica.kupe.api.gui.GUIPlayer}.
 */
public class PlayerUtils {
    private static Map<UUID, Optional<GameProfile>> cache = new ConcurrentHashMap<>();
    private static MinecraftSessionService sessionService;

    private static final Collection<GUIPlayer.CapeProvider> CAPE_PROVIDERS = new ArrayList<>();

    public static void createNewCache(MinecraftSessionService minecraftSessionService) {
        sessionService = minecraftSessionService;
        cache = new ConcurrentHashMap<>();
    }

    public static Text getUsername(UUID uuid) {
        // Get name for cached profile
        Optional<GameProfile> profile = cache.get(uuid);
        if (profile != null && profile.isPresent()) {
            return Text.literal(profile.get().getName());
        }

        // Fallback 1: own username
        if (uuid.equals(UUIDTypeAdapter.fromString(Minecraft.getInstance().getUser().getUuid()))) {
            return Text.literal(Minecraft.getInstance().getUser().getName());
        }

        // Fallback 2: look up for cache
        startProfileLookup(uuid);
        return Text.literal("Player");
    }

    public static class Skin {
        public Skin(ResourceLocation texture, boolean slim) {
            Objects.requireNonNull(texture, "No texture for skin");
            this.texture = texture;
            this.slim = slim;
        }
        public final ResourceLocation texture;
        public final boolean slim;
    }

    /**
     * Get the skin of the given UUID.
     * @param uuid the uuid of the player.
     * @param existing the fallback skin.
     * @return the resource location of the skin.
     */
    public static Skin getSkin(UUID uuid, Skin existing) {
        // Get skin for cached profile
        Optional<GameProfile> profile = cache.get(uuid);

        if (profile != null && profile.isPresent()) {
            // get texture
            Minecraft minecraft = Minecraft.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> properties = minecraft.getSkinManager().getInsecureSkinInformation(profile.get());
            if (properties.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                MinecraftProfileTexture profileTexture = properties.get(MinecraftProfileTexture.Type.SKIN);
                boolean slim = "slim".equals(profileTexture.getMetadata("model"));
                ResourceLocation texture = minecraft.getSkinManager().registerTexture(properties.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
                return new Skin(texture, slim);
            }
        }

        // Fallback 1: player info from server
        if (Minecraft.getInstance().getConnection() != null) {
            PlayerInfo loadedProfile = Minecraft.getInstance().getConnection().getPlayerInfo(uuid);

            if (loadedProfile != null) {
                return new Skin(loadedProfile.getSkinLocation(), "slim".equals(loadedProfile.getModelName()));
            }
        }

        // Fallback 2: start profile look up, use existing default skin
        startProfileLookup(uuid);
        return existing;
    }

    public static void addCapeProvider(GUIPlayer.CapeProvider provider) {
        CAPE_PROVIDERS.add(provider);
    }

    public static Iterable<GUIPlayer.CapeProvider> getCapeProviders() {
        return CAPE_PROVIDERS;
    }

    /**
     * Get the resource location for a custom cape or elytra. Can also be used to load skin, but {@link PlayerUtils#getSkin(UUID, Skin)} is preferred.
     * Unlike other methods, this does not automatically start a look-up.
     * @param uuid the player's uuid.
     * @param type the texture type.
     * @return the texture, or null if one could not be loaded.
     */
    public static @Nullable ResourceLocation getTexture(UUID uuid, MinecraftProfileTexture.Type type) {
        // Get skin for cached profile
        Optional<GameProfile> profile = cache.get(uuid);

        if (profile != null && profile.isPresent()) {
            // get texture
            Minecraft minecraft = Minecraft.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> properties = minecraft.getSkinManager().getInsecureSkinInformation(profile.get());
            if (properties.containsKey(type)) {
                return minecraft.getSkinManager().registerTexture(properties.get(type), type);
            }
        }

        return null;
    }

    private static void startProfileLookup(UUID uuid) {
        Map<UUID, Optional<GameProfile>> map = cache;
        if (!map.containsKey(uuid)) {
            map.put(uuid, Optional.empty()); // looking up!c

            // look up new profile async
            CompletableFuture.runAsync(() -> {
                GameProfile profile = new GameProfile(uuid, null);
                profile = sessionService.fillProfileProperties(profile, true);
                Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);
                if (property != null) {
                    Objects.requireNonNull(profile.getName(), "Filled Game Profile is missing username?");
                    map.put(uuid, Optional.of(profile));
                } // else prefer fallback 1. Don't spam the session server.
            }, Util.backgroundExecutor());
        }
    }
}
