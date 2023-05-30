package com.laudynetwork.networkutils.api.messanger.backend;

import com.google.gson.JsonObject;
import com.laudynetwork.networkutils.api.messanger.request.MessageRequestHandler;
import com.laudynetwork.networkutils.api.messanger.request.RequestLanguage;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * Class made by DasShorty ~Anthony
 */
public class MessageCache {
    private final Map<String, Map<String, Translation>> translationMap = new HashMap<>();

    public MessageCache(String translationApiKey) {
        MessageRequestHandler requestHandler = new MessageRequestHandler(translationApiKey);
        convertMap(requestHandler.getTranslationForGeneralPlugin());
        convertMap(requestHandler.getTranslationForPlugin());
    }

    public Translation getTranslation(String language, String key) {
        return translationMap.get(language).get(key);
    }
    public boolean existTranslation(String key) {
        return translationMap.get("en").containsKey(key);
    }

    private void convertMap(Map<RequestLanguage, JsonObject> map) {
        map.forEach(this::convertTranslation);
    }

    private void convertTranslation(RequestLanguage language, JsonObject json) {
        val strings = json.keySet();

        if (!this.translationMap.containsKey(language.tag()))
            this.translationMap.put(language.tag(), new HashMap<>());

        val map = this.translationMap.get(language.tag());

        strings.forEach(key -> {
            map.put(key, new Translation(json.get(key).getAsString()));
        });
    }
}
