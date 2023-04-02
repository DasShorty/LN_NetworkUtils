package com.laudynetwork.networkutils.api.location;

import com.laudynetwork.database.mysql.MySQL;
import com.laudynetwork.database.mysql.utils.Select;
import com.laudynetwork.database.mysql.utils.UpdateValue;
import com.laudynetwork.networkutils.api.sql.SQLWrapper;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Location;

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
        sql.tableInsert("minecraft_general_locations", "locationKey, location", locationKey, SQLWrapper.fromLocationToString(location));
        return new SQLLocation(locationKey, sql);
    }

    public static boolean existsLocation(String locationKey, MySQL sql) {
        return sql.rowExist(new Select("minecraft_general_locations", "*", String.format("translationKey = '%s'", locationKey)));
    }

    @SneakyThrows
    public static List<String> getAllLocationNames(MySQL sql) {
        return sql.rowSelect(new Select("minecraft_general_locations", "*", "")).getRows().stream().map(row -> ((String) row.get("locationKey"))).toList();
    }

    @SneakyThrows
    public Location getStoredLocation() {
        if (!this.sql.rowExist(new Select("minecraft_general_locations", "*", String.format("locationKey = '%s'", this.locationKey))))
            return null;

        val result = this.sql.rowSelect(new Select("minecraft_general_locations", "*", "locationKey = '%s'"));

        return SQLWrapper.fromStringToLocation(((String) result.getRows().get(0).get("location")));
    }

    public void updateLocation(Location location) {
        this.sql.rowUpdate("minecraft_general_locations", String.format("location = '%s'", SQLWrapper.fromLocationToString(location)), new UpdateValue("location", SQLWrapper.fromLocationToString(location)));
    }

    public void deleteLocation() {
        this.sql.custom("DELETE FROM minecraft_general_locations WHERE locationKey = '" + this.locationKey + "'");
    }

}
