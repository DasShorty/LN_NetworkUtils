package com.laudynetwork.networkutils.api.messanger.backend;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Class made by DasShorty ~Anthony
 */
public class MessageCache {
    private final Map<String, Map<String, Translation>> translationMap = new HashMap<>();

    public MessageCache(Plugin plugin) {
        loadFileInCache(plugin.getResource("translations/own/de.json"), "de");
        loadFileInCache(plugin.getResource("translations/own/en.json"), "en");
        loadFileInCache(plugin.getResource("translations/plugins/de.json"), "de");
        loadFileInCache(plugin.getResource("translations/plugins/en.json"), "en");
    }

    @SneakyThrows
    private void loadFileInCache(InputStream languageFile, String lang) {
        convertTranslation(lang, JsonParser.parseReader(new InputStreamReader(languageFile)).getAsJsonObject());
    }

    public Translation getTranslation(String language, String key) {
        if (!translationMap.containsKey(language)) {
            return new Translation("Language " + language + " not found!");
        }

        if (!translationMap.get(language).containsKey(key)) {
            return new Translation("Key " + key + " not found in " + language + "!");
        }
        return translationMap.get(language).get(key);
    }

    public boolean existTranslation(String key) {
        return translationMap.get("en").containsKey(key);
    }

    private void convertTranslation(String language, JsonObject json) {
        val strings = json.keySet();

        if (!this.translationMap.containsKey(language))
            this.translationMap.put(language, new HashMap<>());

        val map = this.translationMap.get(language);

        strings.forEach(key -> {
            map.put(key, new Translation(json.get(key).getAsString()));
        });
    }
}
