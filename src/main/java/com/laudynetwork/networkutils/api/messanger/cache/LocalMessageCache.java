package com.laudynetwork.networkutils.api.messanger.cache;

import com.laudynetwork.networkutils.NetworkUtils;
import com.laudynetwork.networkutils.api.messanger.SQLTextHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class LocalMessageCache {

    private final Map<SQLTextHandler.Language, Map<String, LanguageKeyCache>> messageCache;
    private final SQLTextHandler textHandler;

    public LocalMessageCache() {
        messageCache = new HashMap<>();
        textHandler = new SQLTextHandler(NetworkUtils.getInstance());

        try {
            var ps = textHandler.getConnection().getMySQLConnection().prepareStatement("SELECT * FROM 'translations'");
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String key = resultSet.getString("translationKey");
                var language = SQLTextHandler.Language.valueOf(resultSet.getString("language"));
                String raw = resultSet.getString("raw");
                if (messageCache.containsKey(language)) {
                    var map = messageCache.get(language);
                    if (!map.containsKey(key))
                        map.put(key, new LanguageKeyCache(key, language, raw));
                    messageCache.put(language, map);
                } else {
                    var map = new HashMap<String, LanguageKeyCache>();
                    map.put(key, new LanguageKeyCache(key, language, raw));
                    messageCache.put(language, map);
                }
            }

            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
