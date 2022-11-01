package de.shortexception.networkutils.api.mongo;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

@Getter
public class MongoConnection {

    private final MongoClient client;
    private final MongoDatabase database;

    public MongoConnection(String connectionString) {

        this.client = MongoClients.create(connectionString);
        this.database = client.getDatabase("networkutils");

    }

    public boolean existsCollection(String collectionName) {
        for (String existingCollectionName : getDatabase().listCollectionNames()) {
            if (existingCollectionName.equals(collectionName))
                return true;
        }

        return false;
    }
}
