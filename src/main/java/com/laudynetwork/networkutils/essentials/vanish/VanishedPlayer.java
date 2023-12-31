package com.laudynetwork.networkutils.essentials.vanish;

import com.laudynetwork.networkutils.NetworkUtils;
import com.laudynetwork.networkutils.api.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.UUID;

public record VanishedPlayer(UUID uuid) {

    public VanishedPlayer update(boolean vanished) {

        val player = Bukkit.getPlayer(this.uuid.toString());
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

        val database = Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(MongoDatabase.class)).getProvider();

        val document = database.getDatabase().getCollection("minecraft_networktutils_essentials_vanish").find(Filters.eq("uuid", this.uuid.toString())).first();

        if (document == null)
            return false;

        return document.get("vanished", Boolean.class);
    }

    private void updateDB(boolean vanished) {
        val database = Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(MongoDatabase.class)).getProvider();
        database.getDatabase().getCollection("minecraft_networktutils_essentials_vanish")
                .updateOne(Filters.eq("uuid", this.uuid.toString()), Updates.set("vanished", vanished));
    }

}
