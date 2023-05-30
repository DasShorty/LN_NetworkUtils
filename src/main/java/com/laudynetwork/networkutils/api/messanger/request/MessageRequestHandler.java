package com.laudynetwork.networkutils.api.messanger.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.val;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class made by DasShorty ~Anthony
 */
public class MessageRequestHandler {

    private final HttpClient client;
    private final String apiKey;
    private final Gson gson = new Gson();
    private final List<RequestLanguage> languages;

    public MessageRequestHandler(String apiKey) {
        this.apiKey = apiKey;
        this.client = HttpClient.newHttpClient();
        this.languages = getLanguagesFromServer();
    }

    @SneakyThrows
    private List<RequestLanguage> getLanguagesFromServer() {
        val bodyHandler = this.client.send(HttpRequest.newBuilder()
                .GET().uri(new URI("https://tolgee.laudynetwork.com/v2/languages?ak=" + this.apiKey))
                .build(), HttpResponse.BodyHandlers.ofString());

        System.out.println(bodyHandler.body());

        val jsonObject = convertStringToJson(bodyHandler.body());
        val embedded = jsonObject.getAsJsonObject("_embedded");
        if (embedded.has("languages")) {
            System.out.println("nu");
        }
        val array = embedded.getAsJsonArray("languages");
        val list = new ArrayList<RequestLanguage>();

        array.asList().forEach(jsonElement -> {
            list.add(this.gson.fromJson(jsonElement.toString(), RequestLanguage.class));
        });

        return list;
    }

    private JsonObject convertStringToJson(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }


    public Map<RequestLanguage, JsonObject> getTranslationForGeneralPlugin() {

        val languageMap = new HashMap<RequestLanguage, JsonObject>();
        String apiKey = "tgpak_gzpwgy3rnzrhk5jqg5xtenzvnvsdizlpnrrxkmbxobzhk";

        this.languages.forEach(requestLanguage -> {
            try {
                val httpResponse = this.client.send(HttpRequest.newBuilder()
                        .GET()
                        .uri(new URI("https://tolgee.laudynetwork.com/v2/projects/export?ak=" + apiKey + "?format=JSON&zip=false&structureDelimiter"))
                        .build(), HttpResponse.BodyHandlers.ofString());

                val value = convertStringToJson(httpResponse.body());
                languageMap.put(requestLanguage, value);

            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        return languageMap;
    }

    public Map<RequestLanguage, JsonObject> getTranslationForPlugin() {

        val languageMap = new HashMap<RequestLanguage, JsonObject>();

        this.languages.forEach(requestLanguage -> {
            try {
                val httpResponse = this.client.send(HttpRequest.newBuilder()
                        .GET()
                        .uri(new URI("https://tolgee.laudynetwork.com/v2/projects/export?ak=" + this.apiKey + "?format=JSON&zip=false&structureDelimiter"))
                        .setHeader("languages", requestLanguage.tag())
                        .build(), HttpResponse.BodyHandlers.ofString());

                languageMap.put(requestLanguage, convertStringToJson(httpResponse.body()));

            } catch (URISyntaxException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }

        });

        return languageMap;
    }
}
