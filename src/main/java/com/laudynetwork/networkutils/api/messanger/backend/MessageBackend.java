package com.laudynetwork.networkutils.api.messanger.backend;

import com.laudynetwork.database.mysql.MySQL;
import com.laudynetwork.database.mysql.utils.Select;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MessageBackend {

    @Getter
    private final MySQL sql;
    private final Logger logger;
    private Map<TranslationLanguage, Map<String, Translation>> translationMap;

    public MessageBackend(MySQL sql, @NotNull String project) {

        this.sql = sql;

        logger = LoggerFactory.getLogger(getClass());

        logger.info("Starting MessageBackend with project: " + project + " ...");

        logger.info("MessageBackend started!");

        updateMessages(project);
    }

    public Translation getTranslation(TranslationLanguage language, @NotNull String key) {

        if (!translationMap.get(language).containsKey(key)) {
            return new Translation(key, language, key);
        }

        return translationMap.get(language).get(key);
    }

    @SneakyThrows
    public void updateMessages(@NotNull String project) {
        translationMap = new HashMap<>();

        var german = new HashMap<String, Translation>();
        var english = new HashMap<String, Translation>();
        var japanese = new HashMap<String, Translation>();
        var russian = new HashMap<String, Translation>();

        val result = this.sql.rowSelect(new Select("translations", "*", "project = '" + project + "' OR project = 'plugins'"));

        result.getRows().forEach(row -> {
            val key = ((String) row.get("translationKey"));
            val germanTranslation = ((String) row.get("de"));
            german.put(key, new Translation(key, TranslationLanguage.GERMAN, germanTranslation));
            val englishTranslation = ((String) row.get("en"));
            english.put(key, new Translation(key, TranslationLanguage.ENGLISH, englishTranslation));
            val japaneseTranslation = ((String) row.get("jp"));
            japanese.put(key, new Translation(key, TranslationLanguage.JAPANESE, japaneseTranslation));
            val russianTranslation = ((String) row.get("ru"));
            russian.put(key, new Translation(key, TranslationLanguage.RUSSIAN, russianTranslation));
        });

        translationMap.put(TranslationLanguage.GERMAN, german);
        translationMap.put(TranslationLanguage.ENGLISH, english);
        translationMap.put(TranslationLanguage.JAPANESE, japanese);
        translationMap.put(TranslationLanguage.RUSSIAN, russian);

        logger.info("Messages where updated!");
    }

    public boolean existsTranslation(String key) {
        return this.sql.rowExist(new Select("translations", "*", String.format("translationKey = '%s'", key)));
    }
}
