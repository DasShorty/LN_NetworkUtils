package com.laudynetwork.networkutils.api.location;

import com.laudynetwork.database.mysql.MySQL;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class LocationCache {

    private final Map<String, Location> locationMap = new HashMap<>();
    private final MySQL sql;

    public LocationCache(MySQL sql) {
        this.sql = sql;
        loadAllLocationsInCache(sql);
    }

    public void updateLocations() {
        loadAllLocationsInCache(sql);
    }

    private void loadAllLocationsInCache(MySQL sql) {

        SQLLocation.getAllLocationNames(sql).forEach(locationKey -> {
            val sqlLocation = SQLLocation.fromSQL(locationKey, sql);

            if (sqlLocation.getStoredLocation() != null) {
                locationMap.put(locationKey, sqlLocation.getStoredLocation());
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
