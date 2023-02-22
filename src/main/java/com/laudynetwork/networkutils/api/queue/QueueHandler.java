package com.laudynetwork.networkutils.api.queue;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class QueueHandler implements Listener {

    private final SQLConnection connection;
    private final QueueManager queueManager;
    private final Cache<UUID, String> playerQuitQueueCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();

    public QueueHandler(SQLConnection connection, QueueManager queueManager) {
        this.connection = connection;
        this.queueManager = queueManager;
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        val player = event.getPlayer();

        QueuePlayer queuePlayer = new QueuePlayer(player.getUniqueId(), this.connection);

        if (queuePlayer.isPlayerInQueue())
            return;

        val queueName = queuePlayer.getQueueName();
        playerQuitQueueCache.put(player.getUniqueId(), queueName);

        this.queueManager.getQueueFromName(queueName).removePlayer(queuePlayer);

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        val player = event.getPlayer();

        QueuePlayer queuePlayer = new QueuePlayer(player.getUniqueId(), this.connection);

        val queueName = playerQuitQueueCache.getIfPresent(player.getUniqueId());

        if (queueName == null)
            return;

        if (this.queueManager.getQueueFromName(queueName).addPlayer(queuePlayer)) {
            player.sendMessage("joined");
        } else
            player.sendMessage("can't join");

    }
}
