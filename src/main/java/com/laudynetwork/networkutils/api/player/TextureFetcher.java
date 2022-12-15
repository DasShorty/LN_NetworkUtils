package com.laudynetwork.networkutils.api.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.stream.Collectors;

@UtilityClass
public class TextureFetcher {
    private final static String URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    public static String getSkinUrl(String uniqueId) {
        try {
            JsonObject response = getResponse(uniqueId);
            if (response == null) return null;
            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(new String(Base64.getDecoder()
                    .decode(response.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value")
                            .getAsString()))).getAsJsonObject();
            return o.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
        } catch (IOException e) {
            return null;
        }
    }

    public static JsonObject getResponse(String query) throws IOException {
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
