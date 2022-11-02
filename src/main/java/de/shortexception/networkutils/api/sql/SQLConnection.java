package de.shortexception.networkutils.api.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLConnection {

    private final HikariDataSource source;
    private final Logger logger;

    public SQLConnection(String jdbcUrl, String user, String pwd) {
        logger = LoggerFactory.getLogger("SQLConnection");
        logger.info("Creating data source to db with user " + user);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(pwd);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        source = new HikariDataSource(config);

        logger.info("Created data source with HikariCP...");
    }

    public Connection getConnection() {
        try {
            logger.info("picking connection from pool");
            return source.getConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public boolean createTableWithPrimaryKey(String table, String primaryKey, TableColumn... columns) {
        logger.info("Trying to create sql table " + table);
        var columnBuilder = new StringBuilder();

        for (TableColumn tableColumn : columns) {
            columnBuilder.append(",").append(tableColumn.name).append(" ").append(tableColumn.type).append("(").append(tableColumn.length).append(")");
        }

        var tableColumns = columnBuilder.substring(1);

        try {
            var ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + table + "(" + tableColumns + ")");
            ps.executeUpdate();
            ps.close();
            logger.info("sql table was successfully created!");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public DataColumn getStringResultColumn(String tableName, String key, String keyValue) {
        logger.info("Trying to get StringResult from " + tableName + " key: " + key + " ...");
        DataColumn column = null;

        try {
            var ps = getConnection().prepareStatement("SELECT * FROM " + tableName + " WHERE " + key + " = " + keyValue);
            var resultSet = ps.executeQuery();
            while (resultSet.next()) {
                column = new DataColumn(key, resultSet.getString(key));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        if (column == null) {
            logger.error("Something went wrong! returning empty DataColumn");
            column = new DataColumn("empty", "empty");
        }
        logger.info("Data Column was successfully got from db");
        return column;
    }

    public DataColumn getIntResultColumn(String tableName, String key, int keyValue) {
        logger.info("Trying to get IntResult from " + tableName + " key: " + key + " ...");
        DataColumn column = null;

        try {
            var ps = getConnection().prepareStatement("SELECT * FROM " + tableName + " WHERE " + key + " = " + keyValue);
            var resultSet = ps.executeQuery();
            while (resultSet.next()) {
                column = new DataColumn(key, resultSet.getInt(key));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        if (column == null) {
            logger.error("Something went wrong! returning empty DataColumn");
            column = new DataColumn("empty", -1);
        }
        logger.info("Data Column was successfully got from db");
        return column;
    }

    public boolean existsColumn(String tableName, String columnName, Object expectedColumnValue) {
        logger.info("Trying to check if " + columnName + " in " + tableName + " exists...");
        try {
            var ps = getConnection().prepareStatement("SELECT * FROM " + tableName + " WHERE EXISTS(SELECT " + columnName + " FROM " + tableName + " WHERE " + columnName + " LIKE " + expectedColumnValue + ")");
            logger.info("Successfully checked if " + columnName + " exists in " + tableName);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public void insert(String tableName, DataColumn... columns) {

        logger.info("Trying to insert DataColumns in " + tableName + " ...");

        var columnNames = new StringBuilder();
        var values = new StringBuilder();

        for (DataColumn column : columns) {
            columnNames.append(",").append(column.name());
            values.append(",").append(column.value());
        }

        try {
            var ps = getConnection().prepareStatement("INSERT INTO " + tableName + "(" + columnNames.substring(1) + ") VALUES (" + values.substring(1) + ")");
            ps.executeUpdate();
            ps.close();
            logger.info("Successfully updated table " + tableName);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

    }

    public void update(String tableName, String conditionKey, Object conditionValue, DataColumn... columns) {

        logger.info("Trying to update DataColumns in " + tableName + " ...");

        var updateColumns = new StringBuilder();

        for (DataColumn column : columns) {
            updateColumns.append(",").append(column.name).append(" = ").append(column.value);
        }

        try {
            var ps = getConnection()
                    .prepareStatement("UPDATE " + tableName + " SET " + updateColumns.substring(1) + " WHERE " + conditionKey + " = " + conditionValue);
            ps.executeUpdate();
            ps.close();
            logger.info("Successfully updated table " + tableName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    enum ColumnType {
        VARCHAR,
        INTEGER

    }

    public record DataColumn(String name, Object value) {
    }

    public record TableColumn(String name, ColumnType type, int length) {
    }
}
