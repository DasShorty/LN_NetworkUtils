package com.laudynetwork.networkutils.api.messanger;

import com.laudynetwork.networkutils.api.messanger.cache.LanguageKeyCache;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
public class SQLTextHandler {

    @Getter
    private final SQLConnection connection;

    public SQLTextHandler(Plugin plugin) {
        connection = new SQLConnection(plugin.getConfig().getString("language.jdbc"), plugin.getConfig().getString("language.user"), plugin.getConfig().getString("language.pwd"));
    }

    public void insertTranslation(LanguageKeyCache cache) {
        connection.insert("translations", new SQLConnection.DataColumn(cache.key(), cache.raw()));
    }

    public void removeTranslation(String key, Language language) {
        connection.delete("translations", "translationKey", key);
    }

    public enum Language {
        GERMAN,
        ENGLISH,
        RUSSIAN,
        JAPANESE,
        FRENCH,
        CHINESE,
        SPANISH
    }
}
