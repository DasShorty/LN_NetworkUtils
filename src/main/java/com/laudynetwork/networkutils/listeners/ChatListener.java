package com.laudynetwork.networkutils.listeners;

import com.laudynetwork.networkutils.api.item.Base64;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        event.setCancelled(true);
    }

}
