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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
        String target = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString();

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

            for (JsonElement element : jsonElement.getAsJsonArray("properties")) {
                JsonObject jo = element.getAsJsonObject();
                if ("textures".equals(jo.get("name").getAsString())) {
                    textures = Optional.of(jo.get("value").getAsString());
                }
            }

            return new Response(id, name, textures);
        } else {
            throw new ApiException(connection.getResponseCode());
        }
    }

    public record Response(String id, String name, Optional<String> textures) {
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
