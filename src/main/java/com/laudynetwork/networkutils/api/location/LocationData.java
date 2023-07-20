package com.laudynetwork.networkutils.api.location;

import com.google.gson.Gson;
import org.bukkit.Location;

/**
 * Class made by DasShorty ~Anthony
 */
public record LocationData(String locationKey, String worldID, double x, double y, double z, float yaw, float pitch) {

    public static LocationData  fromJson(String json) {
        return new Gson().fromJson(json, LocationData.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

}
