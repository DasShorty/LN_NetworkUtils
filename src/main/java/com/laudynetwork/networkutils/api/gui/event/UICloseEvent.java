package com.laudynetwork.networkutils.api.gui.event;

import com.laudynetwork.networkutils.api.gui.GUI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Class made by DasShorty ~Anthony
 */
@RequiredArgsConstructor @Getter
public class UICloseEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final GUI gui;
    private final CloseReason closeReason;

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
