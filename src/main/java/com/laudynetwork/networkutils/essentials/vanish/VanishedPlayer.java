package com.laudynetwork.networkutils.essentials.vanish;

import com.laudynetwork.networkutils.NetworkUtils;
import com.mongodb.client.model.Filters;
import lombok.SneakyThrows;
import lombok.val;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.UUID;

public record VanishedPlayer(UUID uuid) {

    public VanishedPlayer update(boolean vanished) {

        val player = Bukkit.getPlayer(this.uuid);
        assert player != null;

        if (vanished)
            Bukkit.getOnlinePlayers().forEach(players -> {
                if (!players.hasPermission("networkutils.essentials.vanish")) {
                    players.hidePlayer(NetworkUtils.getINSTANCE(), player);
                }
            });

        else
            Bukkit.getOnlinePlayers().forEach(players -> {
                if (!players.hasPermission("networkutils.essentials.vanish")) {
                    players.showPlayer(NetworkUtils.getINSTANCE(), player);
                }
            });

        updateDB(vanished);

        NetworkUtils.getINSTANCE().getTablistManager().updateScoreboard();

        return new VanishedPlayer(this.uuid);
    }

    @SneakyThrows
    public boolean isVanished() {

        val database = NetworkUtils.getINSTANCE().getDatabase();

        val document = database.getDatabase().getCollection("minecraft_networktutils_essentials_vanish").find(Filters.eq("uuid", this.uuid)).first();

        if (document == null)
            return false;

        return document.get("vanished", Boolean.class);
    }

    private void updateDB(boolean vanished) {
        val database = NetworkUtils.getINSTANCE().getDatabase();
        database.getDatabase().getCollection("minecraft_networktutils_essentials_vanish")
                .updateOne(Filters.eq("uuid", this.uuid), new Document("$vanished", vanished));
    }

}
