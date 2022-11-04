package com.laudynetwork.networkutils.listeners;

import com.laudynetwork.networkutils.api.item.Base64;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Base64Listener implements Listener {

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        var player = event.getPlayer();
        var message = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());

        if (!(message.startsWith("base64") && player.isOp()))
            return;

        if (!player.getInventory().getItemInMainHand().getType().isItem())
            return;

        event.setCancelled(true);

        player.sendMessage(Component.text("Base64 Code: ")
                .color(NamedTextColor.GRAY).append(Component.text("[COPY]")
                        .color(NamedTextColor.GREEN).clickEvent(
                                ClickEvent.copyToClipboard(Base64.itemStackToBase64(player.getInventory().getItemInMainHand()))
                        )));

    }

}
