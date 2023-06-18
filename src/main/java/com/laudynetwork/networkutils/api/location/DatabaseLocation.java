package com.laudynetwork.networkutils.api.location;

import com.google.gson.Gson;
import com.laudynetwork.networkutils.api.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.SneakyThrows;
import lombok.val;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseLocation {

    private static final Gson gson = new Gson();
    private final String locationKey;
    private final MongoDatabase database;

    private DatabaseLocation(String locationKey, MongoDatabase database) {
        this.locationKey = locationKey;
        this.database = database;
    }

    public static DatabaseLocation fromDatabase(String locationKey, MongoDatabase database) {
        return new DatabaseLocation(locationKey, database);
    }

    public static DatabaseLocation createLocation(String locationKey, Location location, MongoDatabase database) {
        database.getDatabase()
                .getCollection("minecraft_general_locations")
                .insertOne(
                        gson.fromJson(
                                new LocationData(locationKey,
                                        location.getWorld().getUID().toString(),
                                        location.getX(),
                                        location.getY(),
                                        location.getZ(),
                                        location.getYaw(),
                                        location.getPitch()
                                ).toJson(),
                                Document.class)
                );

        return new DatabaseLocation(locationKey, database);
    }

    public static boolean existsLocation(String locationKey, MongoDatabase database) {
        return database.getDatabase()
                .getCollection("minecraft_general_locations")
                .countDocuments(Filters.eq("locationKey", locationKey)) > 0;
    }

    @SneakyThrows
    public static List<String> getAllLocationNames(MongoDatabase database) {

        val list = new ArrayList<String>();

        database.getDatabase()
                .getCollection("minecraft_general_locations")
                .find().forEach(document -> list.add(gson.fromJson(document.toJson(), LocationData.class).locationKey()));

        return list;
    }

    @SneakyThrows
    public @Nullable Location getStoredLocation() {

        val document = database.getDatabase()
                .getCollection("minecraft_general_locations")
                .find(Filters.eq("locationKey", this.locationKey)).first();

        if (document == null)
            return null;


        val locationData = gson.fromJson(document.toJson(), LocationData.class);
        return new Location(
                Bukkit.getWorld(UUID.fromString(locationData.worldID())),
                locationData.x(),
                locationData.y(),
                locationData.z(),
                locationData.yaw(),
                locationData.pitch()
        );
    }

    public void updateLocation(Location location) {
        this.database.getDatabase()
                .getCollection("minecraft_general_locations")
                .updateOne(Filters.eq("locationKey", this.locationKey), new Document("$location", location));
    }

    public void deleteLocation() {
        this.database.getDatabase()
                .getCollection("minecraft_general_locations")
                .deleteOne(Filters.eq("locationKey", this.locationKey));
    }


}
