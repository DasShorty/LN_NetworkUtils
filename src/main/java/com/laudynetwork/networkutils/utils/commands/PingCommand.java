package com.laudynetwork.networkutils.utils.commands;

import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.Replacement;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PingCommand implements CommandExecutor {

    private final MessageAPI msgAPI;

    public PingCommand(MessageBackend msgBackend) {
        this.msgAPI = new MessageAPI(msgBackend, "system");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("only for players");
            return true;
        }

        val translation = this.msgAPI.getTranslation(TranslationLanguage.ENGLISH, "layout.ping", new Replacement("%ping%", player.getPing()));
        val message = this.msgAPI.getMessage(TranslationLanguage.ENGLISH, "command.ping");
        player.sendMessage(message.append(Component.text(" ").append(this.msgAPI.asHighlight(translation))));

        return true;
    }
}
