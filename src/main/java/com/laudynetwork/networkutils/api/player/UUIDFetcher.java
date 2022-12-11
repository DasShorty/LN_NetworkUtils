package com.laudynetwork.networkutils.api.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.stream.Collectors;

public final class UUIDFetcher {

    /**
     * URL for UUID Fetching
     */
    private static final String URL = "https://playerdb.co/api/player/minecraft/%s";

    /**
     * fetch the UUID of a players name
     *
     * @param name of the player
     * @return the uuid of the player
     */

    public static @Nullable UUID getUUID(@NotNull String name) {
        try {
            JsonObject response = getResponse(name.toLowerCase());
            if (response == null) return null;
            return UUID.fromString(
                    response.get("data").getAsJsonObject().get("player").getAsJsonObject().get("id") // Grab the UUID.
                            .getAsString());
        } catch (IOException e) {
            return null;
        }
    }

    public static @Nullable String getPlayerName(String uniqueId) {
        try {
            JsonObject response = getResponse(uniqueId);
            if (response == null) return null;
            return response.get("data").getAsJsonObject().get("player").getAsJsonObject()
                    .get("username") // Grab the Username
                    .getAsString();
        } catch (IOException e) {
            return null;
        }
    }

    public static String getPlayerName(@NotNull UUID uuid) {
        return getPlayerName(uuid.toString());
    }


    private static @Nullable JsonObject getResponse(String query) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(String.format(URL, query)).openConnection();
        // Connection parameters needs to be set otherwise the API won't accept the connection
        connection.setUseCaches(false);
        connection.setDefaultUseCaches(false);
        connection.addRequestProperty("User-Agent", "Mozilla/5.0");
        connection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
        connection.addRequestProperty("Pragma", "no-cache");
        connection.setReadTimeout(5000);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response;
            response = bufferedReader.lines().collect(Collectors.joining());
            final JsonElement parsed = new JsonParser().parse(response);
            if (parsed == null || !parsed.isJsonObject()) return null;
            return parsed.getAsJsonObject();
        }

    }
}
