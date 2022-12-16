package com.laudynetwork.networkutils.api.messanger.backend;

import com.laudynetwork.networkutils.api.messanger.api.Translation;
import com.laudynetwork.networkutils.api.messanger.api.TranslationLanguage;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MessageBackend {

    private final SQLConnection connection;
    private final String project;
    private final Logger logger;
    private Map<TranslationLanguage, Map<String, Translation>> translationMap;

    public MessageBackend(SQLConnection connection, @NotNull String project) {
        this.connection = connection;
        this.project = project;

        logger = LoggerFactory.getLogger(getClass());

        logger.info("Starting MessageBackend...");

        connection.createTableWithPrimaryKey("translations", "key", new SQLConnection.TableColumn("key", SQLConnection.ColumnType.VARCHAR, 255),
                new SQLConnection.TableColumn("project", SQLConnection.ColumnType.VARCHAR, 255), new SQLConnection.TableColumn("de", SQLConnection.ColumnType.VARCHAR,
                        255), new SQLConnection.TableColumn("en", SQLConnection.ColumnType.VARCHAR, 255), new SQLConnection.TableColumn("ru",
                        SQLConnection.ColumnType.VARCHAR, 255), new SQLConnection.TableColumn("jp", SQLConnection.ColumnType.VARCHAR, 255));

        logger.info("MessageBackend started!");
    }

    public Translation getTranslation(TranslationLanguage language, @NotNull String key) {
        if (!translationMap.get(language).containsKey(key))
            return new Translation(key, TranslationLanguage.ENGLISH, key);
        return translationMap.get(language).get(key);
    }

    public void updateMessages() {
        logger.info("Loading all Messages into Cache ...");
        translationMap = new HashMap<>();

        var german = new HashMap<String, Translation>();
        var english = new HashMap<String, Translation>();
        var japanese = new HashMap<String, Translation>();
        var russian = new HashMap<String, Translation>();

        try {
            val prepareStatement = connection.getMySQLConnection().prepareStatement("SELECT * FROM `translations` WHERE `project` = '" + this.project + "'");

            val resultSet = prepareStatement.executeQuery();

            while (resultSet.next()) {

                val key = resultSet.getString("key");
                german.put(key, new Translation(key, TranslationLanguage.GERMAN, resultSet.getString("de")));
                english.put(key, new Translation(key, TranslationLanguage.ENGLISH, resultSet.getString("en")));
                japanese.put(key, new Translation(key, TranslationLanguage.JAPANESE, resultSet.getString("jp")));
                russian.put(key, new Translation(key, TranslationLanguage.RUSSIAN, resultSet.getString("ru")));

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        translationMap.put(TranslationLanguage.GERMAN, german);
        translationMap.put(TranslationLanguage.ENGLISH, english);
        translationMap.put(TranslationLanguage.JAPANESE, japanese);
        translationMap.put(TranslationLanguage.RUSSIAN, russian);

        logger.info("Messages where updated!");
    }
}
