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

package cc.cosmetica.kupe.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

/**
 * Make requests to Mojang's session server.
 */
public class SessionServerUtils {
    public static Response makeRequest(UUID uuid) throws IOException, ApiException {
        String target = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString() + "?unsigned=false";

        URL url = new URL(target);

        // Open a connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Check if the request was successful
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonObject jsonElement = new JsonParser().parse(reader).getAsJsonObject();

            final String id = jsonElement.get("id").getAsString();
            final String name = jsonElement.get("name").getAsString();
            Optional<String> textures = Optional.empty();
            Optional<String> signature = Optional.empty();

            for (JsonElement element : jsonElement.getAsJsonArray("properties")) {
                JsonObject jo = element.getAsJsonObject();
                if ("textures".equals(jo.get("name").getAsString())) {
                    textures = Optional.of(jo.get("value").getAsString());
                    signature = jo.has("signature") ? Optional.empty() : Optional.of(jo.get("signature").getAsString());
                }
            }

            return new Response(id, name, textures, signature);
        } else {
            throw new ApiException(connection.getResponseCode());
        }
    }

    public record Response(String id, String name, Optional<String> textures, Optional<String> signature) {
        public GameProfile createProfile() {
            if (textures.isPresent()) {
                return new GameProfile(
                        uuidFromString(id),
                        name,
                        new PropertyMap(
                                ImmutableMultimap.<String, Property>builder()
                                        .put("textures", new Property("textures", textures.get(), signature.orElse(null)))
                                        .build()
                    ));
            } else {
                return new GameProfile(uuidFromString(id), name);
            }
        }
    }

    private static UUID uuidFromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("UUID is null");
        }

        String s = value.trim();

        if (s.length() == 36) {
            return UUID.fromString(s);
        }

        if (s.length() == 32) {
            return UUID.fromString(
                    s.substring(0, 8) + "-" +
                            s.substring(8, 12) + "-" +
                            s.substring(12, 16) + "-" +
                            s.substring(16, 20) + "-" +
                            s.substring(20)
            );
        }

        throw new IllegalArgumentException("Invalid UUID: " + value);
    }

    public static class ApiException extends Exception {
        public ApiException(int code) {
            this.code = code;
        }

        private final int code;

        public int getCode() {
            return this.code;
        }
    }
}
