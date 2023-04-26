package com.laudynetwork.networkutils.api.player.event;

import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public class PlayerChangeLanguageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final NetworkPlayer networkPlayer;
    private final TranslationLanguage oldLanguage;
    private final TranslationLanguage newLanguage;

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
