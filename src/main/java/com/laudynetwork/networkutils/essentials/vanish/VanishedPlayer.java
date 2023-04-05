package com.laudynetwork.networkutils.essentials.vanish;

import com.laudynetwork.database.mysql.utils.Select;
import com.laudynetwork.database.mysql.utils.UpdateValue;
import com.laudynetwork.networkutils.NetworkUtils;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.UUID;

public record VanishedPlayer(UUID uuid) {

    public VanishedPlayer update(boolean vanished) {

        val player = Bukkit.getPlayer(this.uuid);
        assert player != null;

        if (vanished)
            Bukkit.getOnlinePlayers().forEach(players -> {
                if (!players.hasPermission("networkutils.essentials.vanish")) {
                    changeInCache(players, player, true);
                    players.hidePlayer(NetworkUtils.getINSTANCE(), player);
                }
            });

        else
            Bukkit.getOnlinePlayers().forEach(players -> {
                if (!players.hasPermission("networkutils.essentials.vanish")) {
                    changeInCache(players, player, false);
                    players.showPlayer(NetworkUtils.getINSTANCE(), player);
                }
            });

        updateDB(vanished);

        NetworkUtils.getINSTANCE().getTablistManager().updateScoreboard();

        return new VanishedPlayer(this.uuid);
    }

    private void changeInCache(Player player, Player target, boolean value) {

        if (!player.hasMetadata("vanished-player"))
            player.setMetadata("vanished-player", new FixedMetadataValue(NetworkUtils.getINSTANCE(), new ArrayList<Player>()));

        val vanishedPlayers = ((ArrayList<Player>) player.getMetadata("vanished-player").get(0).value());
        assert vanishedPlayers != null;

        if (value)
            vanishedPlayers.add(target);
        else
            vanishedPlayers.remove(target);

    }

    @SneakyThrows
    public boolean isVanished() {

        val sql = NetworkUtils.getINSTANCE().getSql();
        val select = new Select("minecraft_networktutils_essentials_vanish", "*", "uuid = '" + this.uuid.toString() + "'");

        if (!sql.rowExist(select)) {
            return false;
        }

        return ((Integer) sql.rowSelect(select).getRows().get(0).get("vanished")) == 1;
    }

    private void updateDB(boolean vanished) {

        val sql = NetworkUtils.getINSTANCE().getSql();
        val select = new Select("minecraft_networktutils_essentials_vanish", "*", "uuid = '" + uuid.toString() + "'");


        if (!sql.rowExist(select)) {
            sql.tableInsert("minecraft_networktutils_essentials_vanish", "uuid, vanished", uuid.toString(), vanished ? 1 : 0);
            return;
        }

        sql.rowUpdate("minecraft_networktutils_essentials_vanish", "uuid = '" + uuid + "'", new UpdateValue("vanished", vanished ? 1 : 0));
    }

}
