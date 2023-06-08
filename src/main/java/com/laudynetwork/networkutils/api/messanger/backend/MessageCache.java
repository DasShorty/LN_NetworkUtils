package com.laudynetwork.networkutils.api.messanger.backend;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Class made by DasShorty ~Anthony
 */
public class MessageCache {
    private final Map<String, Map<String, Translation>> translationMap = new HashMap<>();

    public MessageCache(Plugin plugin) {
        loadFileInCache(plugin, "translations/own/en.json");
        loadFileInCache(plugin, "translations/own/de.json");
        loadFileInCache(plugin, "translations/plugins/de.json");
        loadFileInCache(plugin, "translations/plugins/en.json");
    }

    @SneakyThrows
    private void loadFileInCache(Plugin plugin, String languageFile) {

        val file = new File("./plugins/" + plugin.getName() + "/" + languageFile);
        if (!file.exists())
            return;

        convertTranslation(file.getName().substring(0, 2), JsonParser.parseReader(new FileReader(file)).getAsJsonObject());
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
