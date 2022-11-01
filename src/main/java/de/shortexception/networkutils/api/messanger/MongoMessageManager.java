package de.shortexception.networkutils.api.messanger;

import de.shortexception.networkutils.api.mongo.MongoConnection;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoMessageManager {

    private final MongoConnection connection;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SneakyThrows
    public MongoMessageManager(MongoConnection connection) {
        this.connection = connection;

        if (!connection.existsCollection("chatTranslations")) {
            logger.info("creating chat collection in mongodb");
            this.connection.getDatabase().createCollection("chatTranslations");
        }

        if (!connection.existsCollection("scoreboardTranslations")) {
            logger.info("creating scoreboard collection in mongodb");
            this.connection.getDatabase().createCollection("scoreboardTranslations");
        }

        if (!connection.existsCollection("tablistTranslations")) {
            logger.info("creating tablist collection in mongodb");
            this.connection.getDatabase().createCollection("tablistTranslations");
        }

        if (!connection.existsCollection("guiTranslations")) {
            logger.info("creating gui collection in mongodb");
            this.connection.getDatabase().createCollection("guiTranslations");
        }

    }

    public MongoConnection getConnection() {
        return connection;
    }

    public MongoTextHandler createTextHandler(String key) {
        return new MongoTextHandler(this, key);
    }
}
