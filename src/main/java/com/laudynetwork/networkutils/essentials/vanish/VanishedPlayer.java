package com.laudynetwork.networkutils.essentials.vanish;

import com.laudynetwork.networkutils.NetworkUtils;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;

import java.util.UUID;

public record VanishedPlayer(UUID uuid) {

    private void checkTable() {
        NetworkUtils.getINSTANCE().getDbConnection().createTableWithPrimaryKey("minecraft_networktutils_essentials_vanish", "uuid", new SQLConnection.TableColumn("uuid", SQLConnection.ColumnType.VARCHAR, 35),
                new SQLConnection.TableColumn("vanished", SQLConnection.ColumnType.INTEGER, 1));
    }

    public VanishedPlayer update(boolean vanished) {

        checkTable();

        val player = Bukkit.getPlayer(this.uuid);
        assert player != null;

        Bukkit.getOnlinePlayers().forEach(players -> {
            if (!players.hasPermission("networkutils.essentials.vanish")) {
                players.hidePlayer(NetworkUtils.getINSTANCE(), player);
            }
        });

        updateDB(vanished);

        NetworkUtils.getINSTANCE().getTablistManager().updateScoreboard();

        return new VanishedPlayer(this.uuid);
    }

    @SneakyThrows
    public boolean isVanished() {

        checkTable();

        val sqlConnection = NetworkUtils.getINSTANCE().getDbConnection();

        if (!sqlConnection.existsColumn("minecraft_networktutils_essentials_vanish", "uuid", this.uuid.toString())) {
            return false;
        }

        CompletableFuture<Integer> future = new CompletableFuture<>();

        sqlConnection.resultSet("SELECT * FROM minecraft_networktutils_essentials_vanish WHERE `uuid`='" + this.uuid + "'", resultSet -> {

            try {

                while (resultSet.next()) {

                    future.complete(resultSet.getInt("vanished"));

                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return future.get() == 1;

    }

    private void updateDB(boolean vanished) {

        checkTable();

        val sqlConnection = NetworkUtils.getINSTANCE().getDbConnection();
        if (!sqlConnection.existsColumn("minecraft_networktutils_essentials_vanish", "uuid", this.uuid.toString())) {
            sqlConnection.insert("minecraft_networktutils_essentials_vanish", new SQLConnection.DataColumn("uuid", this.uuid.toString()),
                    new SQLConnection.DataColumn("vanished", vanished ? 1 : 0));
        }
        sqlConnection.update("minecraft_networktutils_essentials_vanish", "uuid", this.uuid.toString(),
                new SQLConnection.DataColumn("vanished", vanished ? 1 : 0));
    }

}
