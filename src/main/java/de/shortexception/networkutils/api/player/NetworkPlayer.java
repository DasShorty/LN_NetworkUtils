package de.shortexception.networkutils.api.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.shortexception.networkutils.api.mongo.MongoConnection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class NetworkPlayer {

    private final MongoConnection connection;
    private final UUID uid;
    private final Logger logger;

    public NetworkPlayer(MongoConnection connection, UUID uid) {
        this.connection = connection;
        this.uid = uid;
        this.logger = LoggerFactory.getLogger("networkplayer");
    }

    private MongoCollection<Document> getCollection() {
        var networkplayers = connection.getDatabase().getCollection("networkplayers");

        if (!connection.existsCollection("networkplayers")) {
            connection.getDatabase().createCollection("networkplayers");
            networkplayers = connection.getDatabase().getCollection("networkplayers");
        }
        return networkplayers;
    }

    public void createNetworkData(SQLTextHandler.Language language) {
        logger.info("attempting to create network data");
        if (!existsInDatabase()) {
            logger.info("creating network data for user " + uid);
            getCollection().insertOne(new Document("uid", uid.toString()).append("language", language.name()));
        }
    }

    public boolean existsInDatabase() {
        var document = getCollection().find(Filters.eq("uid", uid.toString())).first();
        return document != null;
    }

    public SQLTextHandler.Language getSelectedLanguage() {

        var document = getCollection().find(Filters.eq("uid", uid.toString())).first();

        if (document == null)
            return SQLTextHandler.Language.ENGLISH;

        if (!document.containsKey("language"))
            return SQLTextHandler.Language.ENGLISH;

        logger.info("get selected language from user " + uid);

        return SQLTextHandler.Language.valueOf(String.valueOf(document.get("language")));
    }
}
