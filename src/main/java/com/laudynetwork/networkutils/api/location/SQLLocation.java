package com.laudynetwork.networkutils.api.location;

import com.laudynetwork.networkutils.api.sql.SQLConnection;
import com.laudynetwork.networkutils.api.sql.SQLWrapper;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class SQLLocation {

    private final String locationKey;
    private final SQLConnection connection;

    private SQLLocation(String locationKey, SQLConnection connection) {
        this.locationKey = locationKey;
        this.connection = connection;
    }

    public static SQLLocation fromSQL(String locationKey, SQLConnection connection) {
        return new SQLLocation(locationKey, connection);
    }

    public static SQLLocation createLocation(String locationKey, Location location, SQLConnection connection) {

        checkTable(connection);

        connection.insert("minecraft_general_locations", new SQLConnection.DataColumn("locationKey", locationKey), new SQLConnection.DataColumn("location", SQLWrapper.fromLocationToString(location)));

        return new SQLLocation(locationKey, connection);
    }

    private static void checkTable(SQLConnection connection) {
        connection.createTableWithPrimaryKey("minecraft_general_locations", "locationKey", new SQLConnection.TableColumn("locationKey", SQLConnection.ColumnType.VARCHAR, 255),
                new SQLConnection.TableColumn("location", SQLConnection.ColumnType.VARCHAR, 255));
    }

    public static boolean existsLocation(String locationKey, SQLConnection connection) {
        checkTable(connection);
        return connection.existsColumn("minecraft_general_locations", "locationKey", locationKey);
    }

    @SneakyThrows
    public static List<String> getAllLocationNames(SQLConnection connection) {

        checkTable(connection);

        val statement = connection.getMySQLConnection().createStatement();
        statement.setQueryTimeout(30);
        val resultSet = statement.executeQuery("SELECT * FROM `minecraft_general_locations`");

        val list = new ArrayList<String>();

        while (resultSet.next()) {
            list.add(resultSet.getString("locationKey"));
        }

        return list;
    }

    public Location getStoredLocation() {
        checkTable(this.connection);

        if (!this.connection.existsColumn("minecraft_general_locations", "locationKey", this.locationKey))
            return null;

        val stringResultColumn = this.connection.getStringResultColumn("minecraft_general_locations", "locationKey", this.locationKey, "location");
        return SQLWrapper.fromStringToLocation(stringResultColumn.value().toString());
    }

    public void updateLocation(Location location) {
        checkTable(this.connection);
        this.connection.update("minecraft_general_locations", "locationKey", this.locationKey, new SQLConnection.DataColumn("location", SQLWrapper.fromLocationToString(location)));
    }

    public void deleteLocation() {
        checkTable(this.connection);

        this.connection.delete("minecraft_general_locations", "locationKey", this.locationKey);
    }

}
