package com.laudynetwork.networkutils.api;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;

/**
 * Class made by DasShorty ~Anthony
 */
public class MongoDatabase {

    private final MongoClient client;
    @Getter
    private final com.mongodb.client.MongoDatabase database;

    public MongoDatabase(String connectionString) {
        client = MongoClients.create(connectionString);
        this.database = client.getDatabase("laudynetwork");
    }

    public void shutdown() {
        this.client.close();
    }
}
