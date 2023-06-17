package com.laudynetwork.networkutils.listeners;

import com.laudynetwork.networkutils.api.MongoDatabase;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.val;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandProtectionListener implements Listener {

    private final MessageAPI msgAPI = MessageAPI.create(MessageAPI.PrefixType.SYSTEM);
    private final MongoDatabase database;

    public CommandProtectionListener(MongoDatabase database) {
        this.database = database;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        val message = event.getMessage();

        val command = message.split(" ")[0];

        val player = event.getPlayer();
        val networkPlayer = new NetworkPlayer(this.database, player.getUniqueId());

        val language = networkPlayer.getLanguage();

        if (Bukkit.getHelpMap().getHelpTopic(command) == null) {
            event.setCancelled(true);
            player.sendMessage(this.msgAPI.getMessage(language, "command.unknown", Placeholder.unparsed("command", command)));

            return;
        }

        if (player.isOp())
            return;


        switch (command.split(":")[0].replace("/", "")) {
            case "help", "?", "bukkit", "paper", "spigot", "ver", "version", "info" -> {
                event.setCancelled(true);
                player.sendMessage(this.msgAPI.getMessage(language, "command.missing.permission"));
            }
            case "pl", "plugins" -> {
                event.setCancelled(true);
                player.sendMessage(this.msgAPI.getMessage(language, "command.plugins.show"));
            }
        }

    }
}
