package com.laudynetwork.networkutils.api.location;

import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.val;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class LocationCache {

    private final Map<String, Location> locationMap = new HashMap<>();

    public LocationCache(SQLConnection connection) {
        loadAllLocationsInCache(connection);
    }

    private void loadAllLocationsInCache(SQLConnection connection) {

        SQLLocation.getAllLocationNames(connection).forEach(locationKey -> {
            val sqlLocation = SQLLocation.fromSQL(locationKey, connection);

            locationMap.put(locationKey, sqlLocation.getStoredLocation());

        });

    }

    public Location getLocationFromCache(String locationKey) {
        return locationMap.get(locationKey);
    }


}
