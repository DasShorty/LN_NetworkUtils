package com.laudynetwork.networkutils.listeners;

import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final SQLConnection sqlConnection;

    public PlayerJoinListener(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {

        val player = event.getPlayer();
        NetworkPlayer networkPlayer = new NetworkPlayer(this.sqlConnection, player.getUniqueId());

        val playerVersion = networkPlayer.getPlayerVersion();

        networkPlayer.loadTexturePack(playerVersion);
    }
}
