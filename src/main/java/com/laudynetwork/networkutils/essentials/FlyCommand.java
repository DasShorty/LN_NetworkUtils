package com.laudynetwork.networkutils.essentials;

import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FlyCommand implements CommandExecutor, TabCompleter {

    private final MessageAPI msgApi;
    private final SQLConnection sqlConnection;

    public FlyCommand(MessageBackend msgBackend) {
        this.msgApi = new MessageAPI(msgBackend, MessageAPI.PrefixType.SYSTEM);
        this.sqlConnection = msgBackend.getConnection();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(msgApi.getTranslation(TranslationLanguage.ENGLISH, "command.only.player"));
            return true;
        }

        NetworkPlayer networkPlayer = new NetworkPlayer(this.sqlConnection, player.getUniqueId());

        val language = networkPlayer.getLanguage();

        if (!player.hasPermission("essentials.fly.self") && !player.hasPermission("essentials.fly.other")) {
            player.sendMessage(msgApi.getMessage(language, "command.missing.permission"));
            return true;
        }

        if (args.length == 0) {

            if (!player.hasPermission("essentials.fly.self")) {
                player.sendMessage(msgApi.getMessage(language, "command.missing.permission"));
                return true;
            }

            if (player.getAllowFlight()) {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.sendMessage(msgApi.getMessage(language, "command.fly.self.off"));
            } else {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.sendMessage(msgApi.getMessage(language, "command.fly.self.on"));
            }

        } else {

            if (!player.hasPermission("essentials.fly.other")) {
                player.sendMessage(msgApi.getMessage(language, "command.missing.permission"));
                return true;
            }

            if (Bukkit.getPlayerExact(args[0]) == null) {
                player.sendMessage(msgApi.getMessage(language, "command.player.not.found"));
                return true;
            }

            val target = Bukkit.getPlayerExact(args[0]);

            assert target != null;

            if (target.getAllowFlight()) {
                target.setAllowFlight(false);
                target.setFlying(false);
                player.sendMessage(msgApi.getMessage(language, "command.fly.other.off"));
                target.sendMessage(msgApi.getMessage(language, "command.fly.self.off"));
            } else {
                target.setAllowFlight(true);
                target.setFlying(true);
                player.sendMessage(msgApi.getMessage(language, "command.fly.other.on"));
                target.sendMessage(msgApi.getMessage(language, "command.fly.self.on"));
            }

        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> list = new ArrayList<>();

        if (args.length == 0)
            return list;

        if (args.length == 1)
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());


        String current = args[args.length - 1];
        List<String> completer = new ArrayList<>();

        for (String s : list)
            if (s.startsWith(current))
                completer.add(s);

        return completer;
    }
}