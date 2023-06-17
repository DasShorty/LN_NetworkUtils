package com.laudynetwork.networkutils.api.player;

import com.google.gson.Gson;
import com.laudynetwork.networkutils.api.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.viaversion.viaversion.api.Via;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.bson.Document;

import java.util.UUID;

public class NetworkPlayer {

    private final MongoDatabase database;
    @Getter
    private final UUID uuid;
    private final Gson gson = new Gson();

    public NetworkPlayer(MongoDatabase database, UUID uuid) {
        this.database = database;
        this.uuid = uuid;
    }

    public ProtocolVersion getPlayerVersion() {
        return ProtocolVersion.getByProtocolVersion(Via.getAPI().getPlayerVersion(uuid));
    }

    @SneakyThrows
    public String getLanguage() {
        val document = this.database.getDatabase().getCollection("minecraft_general_playerData").find(Filters.eq("uuid", this.uuid)).first();
        assert document != null;
        return PlayerLanguage.fromJson(document.toJson()).language();
    }

    public void setLanguage(String language) {

        val collection = this.database.getDatabase().getCollection("minecraft_general_playerData");

        val iterator = collection.find(Filters.eq("uuid", this.uuid)).iterator();
        val hasNext = iterator.hasNext();
        iterator.close();

        if (!hasNext) {
            collection.insertOne(this.gson.fromJson(this.gson.toJson(new PlayerLanguage(this.uuid, language)), Document.class));
            return;
        }

        collection.updateOne(Filters.eq("uuid", this.uuid), new Document("$language", language));
    }
}
