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
        database.getDatabase().getCollection("minecraft_general_locations").insertOne(gson.fromJson(new LocationData(locationKey, location).toJson(), Document.class));
        return new DatabaseLocation(locationKey, database);
    }

    public static boolean existsLocation(String locationKey, MongoDatabase database) {
        return database.getDatabase().getCollection("minecraft_general_locations").countDocuments(Filters.eq("locationKey", locationKey)) > 0;
    }

    @SneakyThrows
    public static List<String> getAllLocationNames(MongoDatabase database) {

        val list = new ArrayList<String>();

        database.getDatabase().getCollection("minecraft_general_locations").find().forEach(document -> list.add(gson.fromJson(document.toJson(), LocationData.class).locationKey()));

        return list;
    }

    private static Location fromStringToLocation(String str) {

        String[] split = str.split(";");
        var world = Bukkit.getWorld(split[0]);
        var x = Double.parseDouble(split[1]);
        var y = Double.parseDouble(split[2]);
        var z = Double.parseDouble(split[3]);
        var yaw = Float.parseFloat(split[4]);
        var pitch = Float.parseFloat(split[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    private static String fromLocationToString(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }

    @SneakyThrows
    public @Nullable Location getStoredLocation() {
        val document = database.getDatabase().getCollection("minecraft_general_locations").find(Filters.eq("locationKey", this.locationKey)).first();
        return document == null ? null : gson.fromJson(document.toJson(), LocationData.class).location();
    }

    public void updateLocation(Location location) {
        this.database.getDatabase().getCollection("minecraft_general_locations").updateOne(Filters.eq("locationKey", this.locationKey), new Document("$location", location));
    }

    public void deleteLocation() {
        this.database.getDatabase().getCollection("minecraft_general_locations").deleteOne(Filters.eq("locationKey", this.locationKey));
    }


}
