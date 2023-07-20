package com.laudynetwork.networkutils.api.location;

import com.laudynetwork.networkutils.api.MongoDatabase;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class LocationCache {

    private final Map<String, Location> locationMap = new HashMap<>();
    private final MongoDatabase database;

    public LocationCache(MongoDatabase database) {
        this.database = database;
        loadAllLocationsInCache(database);
    }

    public void updateLocations() {
        loadAllLocationsInCache(this.database);
    }

    private void loadAllLocationsInCache(MongoDatabase database) {

        DatabaseLocation.getAllLocationNames(database).forEach(locationKey -> {
            val databaseLocation = DatabaseLocation.fromDatabase(locationKey, this.database);

            if (databaseLocation.getStoredLocation() != null) {
                locationMap.put(locationKey, databaseLocation.getStoredLocation());
            } else {
                Bukkit.getLogger().warning("Skipping location " + locationKey + " because location is scuffed");
            }

        });
    }

    public boolean existLocation(String locationName) {
        return locationMap.containsKey(locationName);
    }

    public Location getLocationFromCache(String locationKey) {
        return locationMap.get(locationKey);
    }


}
