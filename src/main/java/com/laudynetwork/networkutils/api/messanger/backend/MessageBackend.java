package com.laudynetwork.networkutils.api.messanger.backend;

import com.laudynetwork.networkutils.api.messanger.api.Translation;
import com.laudynetwork.networkutils.api.messanger.api.TranslationLanguage;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MessageBackend {

    private final SQLConnection connection;
    private final Logger logger;
    private Map<TranslationLanguage, Map<String, Translation>> translationMap;

    public MessageBackend(@NotNull SQLConnection connection, @NotNull String project) {
        this.connection = connection;

        logger = LoggerFactory.getLogger(getClass());

        logger.info("Starting MessageBackend with project: " + project + " ...");

        connection.createTableWithPrimaryKey("translations", "translationKey", new SQLConnection.TableColumn("translationKey", SQLConnection.ColumnType.VARCHAR, 255),
                new SQLConnection.TableColumn("project", SQLConnection.ColumnType.VARCHAR, 255), new SQLConnection.TableColumn("de", SQLConnection.ColumnType.VARCHAR,
                        255), new SQLConnection.TableColumn("en", SQLConnection.ColumnType.VARCHAR, 255), new SQLConnection.TableColumn("ru",
                        SQLConnection.ColumnType.VARCHAR, 255), new SQLConnection.TableColumn("jp", SQLConnection.ColumnType.VARCHAR, 255));

        logger.info("MessageBackend started!");

        updateMessages(project);
    }

    public Translation getTranslation(TranslationLanguage language, @NotNull String key) {
        if (!translationMap.get(language).containsKey(key))
            return new Translation(key, TranslationLanguage.ENGLISH, key);
        return translationMap.get(language).get(key);
    }

    @SneakyThrows
    public void updateMessages(@NotNull String project) {
        logger.info("Loading all Messages into Cache ...");
        translationMap = new HashMap<>();

        var german = new HashMap<String, Translation>();
        var english = new HashMap<String, Translation>();
        var japanese = new HashMap<String, Translation>();
        var russian = new HashMap<String, Translation>();

        val prepareStatement = connection.getMySQLConnection().prepareStatement("SELECT * FROM `translations` WHERE `project` = '" + project + "' OR `project` = 'plugins'");

        val resultSet = prepareStatement.executeQuery();

        while (resultSet.next()) {

            val key = resultSet.getString("translationKey");
            val germanTranslation = resultSet.getString("de");
            logger.info(germanTranslation);
            german.put(key, new Translation(key, TranslationLanguage.GERMAN, germanTranslation));
            val englishTranslation = resultSet.getString("en");
            english.put(key, new Translation(key, TranslationLanguage.ENGLISH, englishTranslation));
            val japaneseTranslation = resultSet.getString("jp");
            japanese.put(key, new Translation(key, TranslationLanguage.JAPANESE, japaneseTranslation));
            val russianTranslation = resultSet.getString("ru");
            russian.put(key, new Translation(key, TranslationLanguage.RUSSIAN, russianTranslation));

        }

        translationMap.put(TranslationLanguage.GERMAN, german);
        translationMap.put(TranslationLanguage.ENGLISH, english);
        translationMap.put(TranslationLanguage.JAPANESE, japanese);
        translationMap.put(TranslationLanguage.RUSSIAN, russian);

        logger.info("Messages where updated!");
    }
}
