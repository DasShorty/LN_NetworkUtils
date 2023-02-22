package com.laudynetwork.networkutils.api.queue;

import com.laudynetwork.networkutils.api.sql.SQLConnection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueHandler implements Listener {

    private final SQLConnection connection;

    public QueueHandler(SQLConnection connection) {
        this.connection = connection;
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
    }
}
