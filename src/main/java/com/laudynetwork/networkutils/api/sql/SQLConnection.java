package com.laudynetwork.networkutils.api.sql;

import lombok.SneakyThrows;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SQLConnection {

    private final Connection connection;
    private final Logger logger;

    @SneakyThrows
    public SQLConnection(String jdbcUrl, String user, String pwd) {

        Class.forName("org.mariadb.jdbc.Driver").getDeclaredConstructor().newInstance();


        logger = LoggerFactory.getLogger("SQLConnection");
        logger.info("Creating data source to db with user " + user);

        connection = DriverManager.getConnection(jdbcUrl, user, pwd);

//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl(jdbcUrl);
//        config.setUsername(user);
//        config.setPassword(pwd);
//        config.setDriverClassName("org.mariadb.jdbc.Driver");
//        config.addDataSourceProperty("cachePrepStmts", "false");
//        config.addDataSourceProperty("prepStmtCacheSize", "250");
//        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//        config.setMaximumPoolSize(1);
//
//        source = new HikariDataSource(config);

        logger.info("Created db connection to mysql database");
    }

    /**
     * @return current sql connection
     */
    public Connection getMySQLConnection() {
        return connection;
    }

    /**
     * creates a table with specified params
     *
     * @param table      table name to create
     * @param primaryKey key that is used to search
     * @param columns    columns in the table (also primaryKey!)
     */
    public void createTableWithPrimaryKey(String table, String primaryKey, TableColumn... columns) {
        logger.info("Trying to create sql table " + table);
        var columnBuilder = new StringBuilder();

        for (TableColumn tableColumn : columns) {
            columnBuilder.append(",`").append(tableColumn.name).append("` ").append(tableColumn.type).append("(").append(tableColumn.length).append(")");
        }

        var tableColumns = columnBuilder.substring(1);

        try {

            var prepareStmt = "CREATE TABLE IF NOT EXISTS " + table + "(" + tableColumns + ", PRIMARY KEY (" + primaryKey + "))";

            var ps = getMySQLConnection().prepareStatement(prepareStmt);
            ps.executeUpdate();
            ps.close();
            logger.info("sql table was successfully created!");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void createTable(String table, TableColumn... columns) {
        logger.info("Trying to create sql table " + table);
        var columnBuilder = new StringBuilder();

        for (TableColumn tableColumn : columns) {
            columnBuilder.append(",`").append(tableColumn.name).append("` ").append(tableColumn.type).append("(").append(tableColumn.length).append(")");
        }

        var tableColumns = columnBuilder.substring(1);

        try {

            var prepareStmt = "CREATE TABLE IF NOT EXISTS `" + table + "`(" + tableColumns + ")";

            var ps = getMySQLConnection().prepareStatement(prepareStmt);
            ps.executeUpdate();
            ps.close();
            logger.info("sql table was successfully created!");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * get the DataColumn(compact ResultSet) from sql db with the specified params
     *
     * @param tableName      table to search in
     * @param conditionKey   key that contains keyValue (table header)
     * @param conditionValue value from key (table data)
     * @return DataColumn with the keyValue and key from given params as string
     */
    public DataColumn getStringResultColumn(String tableName, String conditionKey, Object conditionValue, String key) {
        logger.info("Trying to get StringResult from " + tableName + " key: " + conditionValue + " ...");
        DataColumn column = null;

        try {
            var ps = getMySQLConnection().createStatement();
            ps.setQueryTimeout(30);
            var resultSet = ps.executeQuery("SELECT * FROM `" + tableName + "` WHERE `" + conditionKey + "`='" + conditionValue + "'");
            while (resultSet.next()) {
                val string = resultSet.getString(key);
                logger.warn(string);
                column = new DataColumn(key, string);
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

    /**
     * get the DataColumn(compact ResultSet) from sql db with the specified params
     *
     * @param tableName table to search in
     * @param key       key that contains keyValue (table header)
     * @param keyValue  value from key (table data)
     * @return DataColumn with the keyValue and key from given params as integer
     */
    public DataColumn getIntResultColumn(String tableName, String key, int keyValue) {
        logger.info("Trying to get IntResult from " + tableName + " key: " + key + " ...");
        DataColumn column = null;

        try {
            var ps = getMySQLConnection().prepareStatement("SELECT * FROM `" + tableName + "` WHERE `" + key + "` = '" + keyValue + "'");
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

    /**
     * checks if given column exist
     *
     * @param tableName           name that contains columnName and the expected value
     * @param columnName          key that contains keyValue (table header)
     * @param expectedColumnValue value from key (table data)
     * @return if column exists in db
     */
    public boolean existsColumn(String tableName, String columnName, Object expectedColumnValue) {
        logger.info("Trying to check if " + columnName + " in " + tableName + " exists...");

        val future = new CompletableFuture<Boolean>();

        try {
            var ps = getMySQLConnection().createStatement();
            val resultSet = ps.executeQuery("SELECT * FROM " + tableName + " WHERE " + columnName + " IS NOT NULL AND " + columnName + " LIKE '" + expectedColumnValue + "'");
            logger.info("Successfully checked if " + columnName + " exists in " + tableName);

            future.complete(resultSet.next());

            ps.close();

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * creates the given data in db
     *
     * @param tableName the table name that contains the new data
     * @param columns   keys and values that where created in the table
     */
    public void insert(String tableName, DataColumn... columns) {

        logger.info("Trying to insert DataColumns in " + tableName + " ...");

        var columnNames = new StringBuilder();
        var values = new StringBuilder();

        for (DataColumn column : columns) {
            columnNames.append(",").append("`").append(column.name()).append("`");
            values.append(",").append("'").append(column.value()).append("'");
        }

        try {

            val string = "INSERT INTO " + tableName + "(" + columnNames.substring(1) + ") VALUES (" + values.substring(1) + ")";

            logger.warn(string);

            var ps = getMySQLConnection().createStatement();
            ps.setQueryTimeout(30);
            ps.executeUpdate(string);
            ps.close();
            logger.info("Successfully updated table " + tableName);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * updates given columns that all have the same conditionValue
     *
     * @param tableName      the table name where all data where updated
     * @param conditionKey   key that says at wich row the data should update
     * @param conditionValue value that is expected to be similar to other key value so it can be updated
     * @param columns        columns with key and value that should be updated
     */
    public void update(String tableName, String conditionKey, Object conditionValue, DataColumn... columns) {

        logger.info("Trying to update DataColumns in " + tableName + " ...");

        var updateColumns = new StringBuilder();

        for (DataColumn column : columns) {
            updateColumns.append(",").append(column.name).append(" = ").append(column.value);
        }

        try {
            var ps = getMySQLConnection()
                    .prepareStatement("UPDATE " + tableName + " SET " + updateColumns.substring(1) + " WHERE " + conditionKey + " = " + conditionValue);
            ps.executeUpdate();
            ps.close();
            logger.info("Successfully updated table " + tableName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * row that should be deleted
     * @param tableName      the table name where the row where deleted
     * @param conditionKey key that says at wich row the data should deleted
     * @param conditionValue value that is expected to be similar to other key value so it can be deleted
     */
    public void delete(String tableName, String conditionKey, Object conditionValue) {
        logger.info("Trying to delete " + conditionValue + " in " + tableName + " column: " + conditionKey);

        try {
            var ps = getMySQLConnection()
                    .prepareStatement("DELETE FROM `" + tableName + "` WHERE `" + conditionKey + "`='" + conditionValue+"'");

            ps.executeUpdate();

            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * help will be later added!
     * @param table later
     * @param columnRow later
     * @return pls wait later
     */
    public List<DataSchema> getAllColumnsFromTable(String table, String columnRow) {

        var list = new ArrayList<DataSchema>();

        try {
                var ps = getMySQLConnection().prepareStatement("SELECT "+columnRow+" FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = "+table+" ORDER BY ORDINAL_POSITION");

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {

                String tableSchema = resultSet.getString("TABLE_SCHEMA");
                String tableName = resultSet.getString("TABLE_NAME");
                String columnName = resultSet.getString("COLUMN_NAME");
                int ordinalPosition = resultSet.getInt("ORDINAL_POSITION");
                String dataType = resultSet.getString("DATA_TYPE");
                list.add(new DataSchema(tableSchema, tableName, columnName, ordinalPosition, dataType));
            }

            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public enum ColumnType {
        VARCHAR,
        INTEGER
    }

    public record DataSchema(String tableSchema, String tableName, String columnName, int ordinalPosition,
                             String dataType) {
    }

    public record DataColumn(String name, Object value) {
    }

    public record TableColumn(String name, ColumnType type, int length) {
    }
}
