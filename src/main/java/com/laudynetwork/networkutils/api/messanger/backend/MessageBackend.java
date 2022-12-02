package com.laudynetwork.networkutils.api.messanger.backend;

import com.laudynetwork.networkutils.api.messanger.api.TranslatedLanguage;
import com.laudynetwork.networkutils.api.messanger.api.Translation;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class MessageBackend {

    private final SQLConnection connection;
    private final Logger logger;

    public MessageBackend(SQLConnection connection) {
        this.connection = connection;

        logger = LoggerFactory.getLogger(getClass());

        logger.info("Starting MessageBackend...");

        Arrays.stream(TranslatedLanguage.values()).toList().forEach(translatedLanguage -> {
            connection.createTableWithPrimaryKey("translation_" + translatedLanguage.name().toLowerCase(), "languageKey",
                    new SQLConnection.TableColumn("languageKey", SQLConnection.ColumnType.VARCHAR, 20),
                    new SQLConnection.TableColumn("translation", SQLConnection.ColumnType.VARCHAR, 999));
        });

        logger.info("MessageBackend started!");
    }

    /**
     *
     * @param languageKey Key to get the message
     * @param language selected language from player
     * @return DataColumn from ResultSet
     */
    public SQLConnection.DataColumn makeMessageRequest(@NotNull String languageKey, @Nullable TranslatedLanguage language) {

        if (language == null) {
            logger.warn("language is null! | makeMessageRequest <MessageBackend.java:36> -> NetworkUtils.jar");
            language = TranslatedLanguage.ENGLISH;
        }

        SQLConnection.DataColumn msgResponse = connection.getStringResultColumn("translation-" + language.name().toLowerCase(), "languageKey", languageKey);

        if (msgResponse == null) {
            logger.error(languageKey + " not found! <MessageBackend.java:43> -> NetworkUtils.jar");
            msgResponse = new SQLConnection.DataColumn(languageKey, languageKey + " not found! | makeMessageRequest <MessageBackend.java:43> -> NetworkUtils.jar");
        }

        return msgResponse;
    }

    /**
     *
     * @param translation Translation to insert
     * @return if translation already existed (overwrites if exists)
     */
    public boolean insertMessageRequest(Translation translation) {

        if (connection.existsColumn("translation-" + translation.language().name().toLowerCase(), "languageKey", translation.key())) {
            connection.update("translation-" + translation.language().name().toLowerCase(), "languageKey", translation.key(),
                    new SQLConnection.DataColumn(translation.key(), translation.language()));
            return true;
        } else {
            connection.insert("translation-" + translation.language().name().toLowerCase(),
                    new SQLConnection.DataColumn(translation.key(), translation.raw()));
            return false;
        }

    }

    /**
     *
     * @param translationKey Key to get the message
     * @param language selected language from player
     * @return if key is deleted
     */
    public boolean deleteMessageRequest(String translationKey, TranslatedLanguage language) {

        if (connection.existsColumn("translation-" + language.name().toLowerCase(), "translationKey", translationKey)) {
            connection.delete("translation-" + language.name().toLowerCase(), "languageKey", translationKey);
            return true;
        }

        return false;
    }
}
