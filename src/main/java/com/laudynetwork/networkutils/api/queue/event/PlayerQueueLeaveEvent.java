package com.laudynetwork.networkutils.api.queue.event;

import com.laudynetwork.networkutils.api.queue.Queue;
import com.laudynetwork.networkutils.api.queue.QueuePlayer;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerQueueLeaveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Queue queue;
    @Getter
    private final QueuePlayer queuePlayer;
    private boolean cancelled = false;

    public PlayerQueueLeaveEvent(Queue queue, QueuePlayer queuePlayer) {
        this.queue = queue;
        this.queuePlayer = queuePlayer;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
