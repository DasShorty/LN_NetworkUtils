package com.laudynetwork.networkutils.api.sql;

import lombok.SneakyThrows;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class SQLConnection {

    private final Connection connection;
    private final Logger logger;

    @SneakyThrows
    public SQLConnection(String jdbcUrl, String user, String pwd) {

        Class.forName("org.mariadb.jdbc.Driver").getDeclaredConstructor().newInstance();


        logger = LoggerFactory.getLogger("SQLConnection");

        connection = DriverManager.getConnection(jdbcUrl, user, pwd);
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

        Executors.newCachedThreadPool().submit(() -> {
            var columnBuilder = new StringBuilder();

            for (TableColumn tableColumn : columns) {
                columnBuilder.append(",`").append(tableColumn.name).append("` ").append(tableColumn.type).append("(").append(tableColumn.length).append(")");
            }

            var tableColumns = columnBuilder.substring(1);

            try {
                val statement = prepareStatement();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + "(" + tableColumns + ", PRIMARY KEY (" + primaryKey + "))");
                statement.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        });
    }

    public void resultSet(String sql, Consumer<ResultSet> consumer) {
        Executors.newCachedThreadPool().submit(() -> {
            try {
                val statement = prepareStatement();
                consumer.accept(statement.executeQuery(sql));
                statement.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        });
    }

    public void createTable(String table, TableColumn... columns) {
        Executors.newCachedThreadPool().submit(() -> {
            var columnBuilder = new StringBuilder();

            for (TableColumn tableColumn : columns) {
                columnBuilder.append(",`").append(tableColumn.name).append("` ").append(tableColumn.type).append("(").append(tableColumn.length).append(")");
            }

            var tableColumns = columnBuilder.substring(1);

            try {
                val statement = prepareStatement();

                statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "`(" + tableColumns + ")");

                statement.close();

            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        });
    }

    public void createTableFromSQL(String sql) {
        Executors.newCachedThreadPool().submit(() -> {
            try {
                var ps = getMySQLConnection().prepareStatement(sql);
                ps.setQueryTimeout(30);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        });
    }

    /**
     * get the DataColumn(compact ResultSet) from sql db with the specified params
     *
     * @param tableName      table to search in
     * @param conditionKey   key that contains keyValue (table header)
     * @param conditionValue value from key (table data)
     * @return DataColumn with the keyValue and key from given params as string
     */
    @SneakyThrows
    public DataColumn getStringResultColumn(String tableName, String conditionKey, Object conditionValue, String key) {
        CompletableFuture<DataColumn> future = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            DataColumn column = null;
            try {
                val statement = prepareStatement();

                val resultSet = statement.executeQuery("SELECT * FROM `" + tableName + "` WHERE `" + conditionKey + "` = '" + conditionValue + "'");
                while (resultSet.next()) {
                    val string = resultSet.getString(key);
                    logger.warn(string);
                    column = new DataColumn(key, string);
                }

                statement.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }

            if (column == null) {
                logger.error("Something went wrong! returning empty DataColumn");
                column = new DataColumn("empty", "empty");
            }

            future.complete(column);
        });
        return future.get();
    }

    /**
     * get the DataColumn(compact ResultSet) from sql db with the specified params
     *
     * @param tableName      table to search in
     * @param conditionKey   key that contains keyValue (table header)
     * @param conditionValue value from key (table data)
     * @return DataColumn with the keyValue and key from given params as integer
     */
    @SneakyThrows
    public DataColumn getIntResultColumn(String tableName, String conditionKey, Object conditionValue, String key) {
        CompletableFuture<DataColumn> future = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            DataColumn column = null;
            try {
                val statement = prepareStatement();

                val resultSet = statement.executeQuery("SELECT * FROM `" + tableName + "` WHERE `" + conditionKey + "` = '" + conditionValue + "'");

                while (resultSet.next()) {
                    column = new DataColumn(key, resultSet.getInt(key));
                }

                statement.close();

            } catch (SQLException e) {
                logger.error(e.getMessage());
            }

            if (column == null) {
                logger.error("Something went wrong! returning empty DataColumn");
                column = new DataColumn("empty", -1);
            }
        });
        return future.get();
    }

    /**
     * checks if given column exist
     *
     * @param tableName           name that contains columnName and the expected value
     * @param columnName          key that contains keyValue (table header)
     * @param expectedColumnValue value from key (table data)
     * @return if column exists in db
     */
    @SneakyThrows
    public boolean existsColumn(String tableName, String columnName, Object expectedColumnValue) {

        val future = new CompletableFuture<Boolean>();

        Executors.newCachedThreadPool().submit(() -> {
            try {
                var ps = prepareStatement();
                val resultSet = ps.executeQuery("SELECT * FROM " + tableName + " WHERE " + columnName + " IS NOT NULL AND " + columnName + " LIKE '" + expectedColumnValue + "'");

                future.complete(resultSet.next());

                ps.close();

            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        });

        return future.get();
    }

    /**
     * creates the given data in db
     *
     * @param tableName the table name that contains the new data
     * @param columns   keys and values that where created in the table
     */
    public void insert(String tableName, DataColumn... columns) {
        Executors.newCachedThreadPool().submit(() -> {
            var columnNames = new StringBuilder();
            var values = new StringBuilder();

            for (DataColumn column : columns) {
                columnNames.append(",").append("`").append(column.name()).append("`");
                values.append(",").append("'").append(column.value()).append("'");
            }

            try {

                val statement = prepareStatement();

                statement.executeUpdate("INSERT INTO " + tableName + "(" + columnNames.substring(1) + ") VALUES (" + values.substring(1) + ")");

                statement.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        });
    }

    /**
     * updates given columns that all have the same conditionValue
     *
     * @param tableName      the table name where all data where updated
     * @param conditionKey   key that says at wich row the data should update
     * @param conditionValue value that is expected to be similar to other key value, so it can be updated
     * @param columns        columns with key and value that should be updated
     */
    public void update(String tableName, String conditionKey, Object conditionValue, DataColumn... columns) {

        Executors.newCachedThreadPool().submit(() -> {
            var updateColumns = new StringBuilder();

            for (DataColumn column : columns) {
                updateColumns.append(",`").append(column.name).append("` = '").append(column.value).append("'");
            }

            try {
                val statement = prepareStatement();

                statement.executeUpdate("UPDATE " + tableName + " SET " + updateColumns.substring(1) + " WHERE `" + conditionKey + "` = '" + conditionValue + "'");

                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * row that should be deleted
     *
     * @param tableName      the table name where the row where deleted
     * @param conditionKey   key that says at wich row the data should be deleted
     * @param conditionValue value that is expected to be similar to other key value, so it can be deleted
     */
    public void delete(String tableName, String conditionKey, Object conditionValue) {
        Executors.newCachedThreadPool().submit(() -> {
            try {

                val statement = prepareStatement();

                statement.executeUpdate("DELETE FROM `" + tableName + "` WHERE `" + conditionKey + "`='" + conditionValue + "'");

                statement.close();

            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        });
    }

    /**
     * help will be later added!
     *
     * @param table     later
     * @param columnRow later
     * @return pls wait later
     */
    public List<DataSchema> getAllColumnsFromTable(String table, String columnRow) {

        var list = new ArrayList<DataSchema>();

        Executors.newCachedThreadPool().submit(() -> {
            try {
                val statement = prepareStatement();

                var resultSet = statement.executeQuery("SELECT " + columnRow + " FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = " + table + " ORDER BY ORDINAL_POSITION");
                while (resultSet.next()) {

                    String tableSchema = resultSet.getString("TABLE_SCHEMA");
                    String tableName = resultSet.getString("TABLE_NAME");
                    String columnName = resultSet.getString("COLUMN_NAME");
                    int ordinalPosition = resultSet.getInt("ORDINAL_POSITION");
                    String dataType = resultSet.getString("DATA_TYPE");
                    list.add(new DataSchema(tableSchema, tableName, columnName, ordinalPosition, dataType));
                }

                statement.close();

            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        });

        return list;
    }

    @SneakyThrows
    private Statement prepareStatement() {
        val statement = getMySQLConnection().createStatement();
        statement.setQueryTimeout(30);
        return statement;
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
