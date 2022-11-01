package de.shortexception.networkutils;

import de.shortexception.networkutils.api.messanger.MongoMessageManager;
import de.shortexception.networkutils.api.mongo.MongoConnection;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NetworkHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Getter
    private final MongoMessageManager mongoMessageManager;
    @Getter
    private final MongoConnection mongoConnection;

    public NetworkHandler(String connectionString) {
        this.mongoConnection = new MongoConnection(connectionString);
        this.mongoMessageManager = new MongoMessageManager(mongoConnection);
    }
}
