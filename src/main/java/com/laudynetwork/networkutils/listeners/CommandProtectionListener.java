package com.laudynetwork.networkutils.listeners;

import com.laudynetwork.networkutils.api.messanger.api.TranslationLanguage;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandProtectionListener implements Listener {

    private final MessageBackend msgBackend;

    public CommandProtectionListener(MessageBackend msgBackend) {
        this.msgBackend = msgBackend;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        val message = event.getMessage();

        val command = message.split(" ")[0];

        if (Bukkit.getHelpMap().getHelpTopic(command) == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(msgBackend.getTranslation(TranslationLanguage.ENGLISH, "command.unknown").createBuilder().build());

            return;
        }

        if (event.getPlayer().isOp())
            return;

        switch (command.split(":")[0].replace("/", "")) {
            case "help", "?", "bukkit", "paper", "spigot", "ver", "version", "info" -> {
                event.setCancelled(true);
                event.getPlayer().sendMessage(msgBackend.getTranslation(TranslationLanguage.ENGLISH, "command.permission.missing").createBuilder().build());
            }
            case "pl", "plugins" -> {
                event.setCancelled(true);
                event.getPlayer().sendMessage(msgBackend.getTranslation(TranslationLanguage.ENGLISH, "command.plugins.show").createBuilder().build());
            }
        }

    }
}
