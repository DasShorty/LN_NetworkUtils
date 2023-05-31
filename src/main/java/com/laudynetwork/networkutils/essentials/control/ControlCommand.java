package com.laudynetwork.networkutils.essentials.control;

import com.laudynetwork.database.mysql.MySQL;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import com.laudynetwork.networkutils.essentials.control.api.ControlSubCommandHandler;
import lombok.val;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ControlCommand implements CommandExecutor, TabCompleter {

    private final ControlSubCommandHandler subCommandHandler;
    private final MySQL sql;
    private final MessageAPI msgApi = MessageAPI.create(MessageAPI.PrefixType.SYSTEM);

    public ControlCommand(ControlSubCommandHandler handler, MySQL sql) {
        this.subCommandHandler = handler;
        this.sql = sql;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.msgApi.getMessage("en", "command.only.player"));
            return true;
        }

        val networkPlayer = new NetworkPlayer(this.sql, player.getUniqueId());

        val language = networkPlayer.getLanguage();

        if (!player.hasPermission("networkutils.control.basic")) {
            player.sendMessage(this.msgApi.getMessage(language, "command.missing.permission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(this.msgApi.getMessage(language, "command.usage", Placeholder.unparsed("command", "/control <subcommand>")));
            return true;
        }

        val id = args[0]; // id = subCommand

        if (!this.subCommandHandler.getSubCommands().containsKey(id)) {
            player.sendMessage(this.msgApi.getMessage(language, "command.usage", Placeholder.unparsed("command", "/control <subcommand>")));
            return true;
        }

        val controlSubCommand = this.subCommandHandler.getSubCommands().get(id);

        controlSubCommand.onCommand(player, command, label, args, networkPlayer);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        val list = new ArrayList<String>();

        if (!(sender instanceof Player player))
            return list;

        if (args.length == 0)
            return list;

        if (args.length == 1)
            list.addAll(this.subCommandHandler.getSubCommandIDs());
        else {
            val id = args[0]; // id = subCommand

            if (!this.subCommandHandler.getSubCommands().containsKey(id))
                return list;

            val subCommand = this.subCommandHandler.getSubCommands().get(id);
            list.addAll(subCommand.onTabComplete(player, command, label, args, new NetworkPlayer(this.sql, player.getUniqueId())));
        }

        val completer = new ArrayList<String>();
        val current = args[args.length - 1];
        list.forEach(s -> {
            if (s.startsWith(current))
                completer.add(s);
        });

        return completer;
    }
}
