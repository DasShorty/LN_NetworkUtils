package com.laudynetwork.networkutils.api.messanger.cache;

import com.laudynetwork.networkutils.api.messanger.SQLTextHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public record LanguageKeyCache(String key, SQLTextHandler.Language language, String raw) {

    public Component translateColors() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(raw);
    }

    public String translateReplacements(Replacement... replacements) {
        StringBuilder rawBuilder = new StringBuilder(raw);
        for (Replacement replacement : replacements) {
            rawBuilder.append(rawBuilder.toString().replaceAll(replacement.placeholder(), replacement.replaced().toString()));
        }
        return rawBuilder.toString();
    }


    record Replacement(String placeholder, Object replaced){}

}
