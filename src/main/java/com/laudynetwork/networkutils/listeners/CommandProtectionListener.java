package com.laudynetwork.networkutils.listeners;

import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import lombok.val;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandProtectionListener implements Listener {

    private final MessageAPI msgAPI;

    public CommandProtectionListener(MessageBackend msgBackend) {
        this.msgAPI = new MessageAPI(msgBackend, MessageAPI.PrefixType.SYSTEM);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        val message = event.getMessage();

        val command = message.split(" ")[0];

        if (Bukkit.getHelpMap().getHelpTopic(command) == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(this.msgAPI.getMessage(TranslationLanguage.ENGLISH, "command.unknown", Placeholder.unparsed("command", command)));

            return;
        }

        if (event.getPlayer().isOp())
            return;

        switch (command.split(":")[0].replace("/", "")) {
            case "help", "?", "bukkit", "paper", "spigot", "ver", "version", "info" -> {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.msgAPI.getMessage(TranslationLanguage.ENGLISH, "command.permission.missing"));
            }
            case "pl", "plugins" -> {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.msgAPI.getMessage(TranslationLanguage.ENGLISH, "command.plugins.show"));
            }
        }

    }
}
