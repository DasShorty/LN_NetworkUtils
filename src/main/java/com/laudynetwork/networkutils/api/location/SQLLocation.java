package com.laudynetwork.networkutils.api.location;

import com.laudynetwork.database.mysql.MySQL;
import com.laudynetwork.database.mysql.utils.Select;
import com.laudynetwork.database.mysql.utils.UpdateValue;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.List;

public class SQLLocation {

    private final String locationKey;
    private final MySQL sql;

    private SQLLocation(String locationKey, MySQL sql) {
        this.locationKey = locationKey;
        this.sql = sql;
    }

    public static SQLLocation fromSQL(String locationKey, MySQL sql) {
        return new SQLLocation(locationKey, sql);
    }

    public static SQLLocation createLocation(String locationKey, Location location, MySQL sql) {
        sql.tableInsert("minecraft_general_locations", "locationKey, location", locationKey, fromLocationToString(location));
        return new SQLLocation(locationKey, sql);
    }

    public static boolean existsLocation(String locationKey, MySQL sql) {
        return sql.rowExist(new Select("minecraft_general_locations", "*", String.format("locationKey = '%s'", locationKey)));
    }

    @SneakyThrows
    public static List<String> getAllLocationNames(MySQL sql) {
        return sql.rowSelect(new Select("minecraft_general_locations", "*", "1 = 1")).getRows().stream().map(row -> ((String) row.get("locationKey"))).toList();
    }

    private static Location fromStringToLocation(String str) {

        String[] split = str.split(";");
        var world = Bukkit.getWorld(split[0]);
        var x = Integer.parseInt(split[1]);
        var y = Integer.parseInt(split[2]);
        var z = Integer.parseInt(split[3]);
        var yaw = Float.parseFloat(split[4]);
        var pitch = Float.parseFloat(split[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    private static String fromLocationToString(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }

    @SneakyThrows
    public @Nullable Location getStoredLocation() {
        val select = new Select("minecraft_general_locations", "*", String.format("locationKey = '%s'", this.locationKey));
        if (!this.sql.rowExist(select))
            return null;

        val result = this.sql.rowSelect(select);

        if (result.getRows().isEmpty())
            return null;

        return fromStringToLocation(((String) result.getRows().get(0).get("location")));
    }

    public void updateLocation(Location location) {
        this.sql.rowUpdate("minecraft_general_locations", String.format("locationKey = '%s'", this.locationKey), new UpdateValue("location", fromLocationToString(location)));
    }

    public void deleteLocation() {
        this.sql.custom("DELETE FROM minecraft_general_locations WHERE locationKey = '" + this.locationKey + "'");
    }


}
