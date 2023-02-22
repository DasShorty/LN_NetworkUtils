package com.laudynetwork.networkutils.api.queue;

import com.laudynetwork.networkutils.api.queue.event.PlayerQueueJoinEvent;
import com.laudynetwork.networkutils.api.queue.event.PlayerQueueLeaveEvent;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;

import java.util.List;
import java.util.Objects;

public record Queue(String name, int id, List<QueuePlayer> waitingPlayers, String destinationServer, BossBar bossBar, int maxSlots, int minSlots) {
    public void removePlayer(QueuePlayer queuePlayer) {

        val event = new PlayerQueueLeaveEvent(this, queuePlayer);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        this.waitingPlayers.remove(queuePlayer);
        this.bossBar.addPlayer(Objects.requireNonNull(Bukkit.getPlayer(queuePlayer.getUuid())));
    }

    public boolean addPlayer(QueuePlayer queuePlayer) {
        if (waitingPlayers.size() < minSlots)
            return false;

        val event = new PlayerQueueJoinEvent(this, queuePlayer);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return false;

        this.waitingPlayers.add(queuePlayer);
        this.bossBar.addPlayer(Objects.requireNonNull(Bukkit.getPlayer(queuePlayer.getUuid())));

        return true;
    }
}
