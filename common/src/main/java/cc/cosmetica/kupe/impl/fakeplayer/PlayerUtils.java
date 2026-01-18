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
import cc.cosmetica.kupe.util.SessionServerUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal utilities for {@link cc.cosmetica.kupe.api.gui.GUIPlayer}.
 */
public class PlayerUtils {
    private static Map<UUID, Optional<GameProfile>> cache = new ConcurrentHashMap<>();

    private static final Collection<GUIPlayer.CapeProvider> CAPE_PROVIDERS = new ArrayList<>();

    public static void createNewCache() {
        cache = new ConcurrentHashMap<>();
    }

    public static Text getUsername(UUID uuid) {
        // Get name for cached profile
        Optional<GameProfile> profile = cache.get(uuid);
        if (profile != null && profile.isPresent()) {
            return Text.literal(profile.get().name());
        }

        // Fallback 1: own username
        if (uuid.equals(Minecraft.getInstance().getUser().getProfileId())) {
            return Text.literal(Minecraft.getInstance().getUser().getName());
        }

        // Fallback 2: look up for cache
        startProfileLookup(uuid);
        return Text.literal("Player");
    }

    public static class Skin {
        public Skin(Identifier texture, boolean slim) {
            Objects.requireNonNull(texture, "No texture for skin");
            this.texture = texture;
            this.slim = slim;
        }
        public final Identifier texture;
        public final boolean slim;

        public Skin(PlayerSkin skin) {
            this.texture = skin.body().texturePath();
            this.slim = skin.model() == PlayerModelType.SLIM;
        }
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
            Optional<PlayerSkin> playerSkin = getPlayerSkin(uuid);
            return playerSkin.isPresent() ? new Skin(playerSkin.get()) : existing;
        }

        // Fallback 1: player info from server
        if (Minecraft.getInstance().getConnection() != null) {
            PlayerInfo loadedProfile = Minecraft.getInstance().getConnection().getPlayerInfo(uuid);

            if (loadedProfile != null) {
                return new Skin(loadedProfile.getSkin());
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
    public static @Nullable Identifier getTexture(UUID uuid, MinecraftProfileTexture.Type type) {
       Optional<PlayerSkin> skinNow = getPlayerSkin(uuid);

        if (skinNow.isEmpty()) {
            return null;
        }

        switch (type) {
        case SKIN:
            return skinNow.get().body().texturePath();
        case CAPE:
            return skinNow.map(PlayerSkin::cape).map(ClientAsset.Texture::texturePath).orElse(null);
        case ELYTRA:
            return skinNow.map(PlayerSkin::elytra).map(ClientAsset.Texture::texturePath).orElse(null);
        default:
            return null;
        }
    }

    private static Optional<PlayerSkin> getPlayerSkin(UUID uuid) {
        // Get skin for cached profile
        Optional<GameProfile> profile = cache.get(uuid);

        if (profile != null && profile.isPresent()) {
            // get texture
            Minecraft minecraft = Minecraft.getInstance();
            CompletableFuture<Optional<PlayerSkin>> skinFuture = minecraft.getSkinManager().get(profile.get());
            return skinFuture.getNow(Optional.empty());
        }

        return Optional.empty();
    }

    private static void startProfileLookup(UUID uuid) {
        if (cache.size() > 50) {
            createNewCache();
        }
        Map<UUID, Optional<GameProfile>> map = cache;
        if (!map.containsKey(uuid)) {
            map.put(uuid, Optional.empty()); // looking up!c

            // look up new profile async
            CompletableFuture.runAsync(() -> {
                SessionServerUtils.Response result;
                try {
                    result = SessionServerUtils.makeRequest(uuid);
                } catch (IOException | SessionServerUtils.ApiException e) {
                    throw new CompletionException(e);
                }

                if (result.textures().isPresent()) {
                    map.put(uuid, Optional.of(result.createProfile()));
                } // else prefer fallback 1. Don't spam the session server.
            }, Util.backgroundExecutor())
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        }
    }
}
