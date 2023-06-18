package com.laudynetwork.networkutils.essentials;

import com.laudynetwork.networkutils.NetworkUtils;
import com.laudynetwork.networkutils.api.MongoDatabase;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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

@RequiredArgsConstructor
public class FlyCommand implements CommandExecutor, TabCompleter {

    private final MessageAPI msgApi = new MessageAPI(NetworkUtils.getINSTANCE().getMessageCache(), MessageAPI.PrefixType.SYSTEM);
    private final MongoDatabase database;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(msgApi.getMessage("en", "command.only.player"));
            return true;
        }

        NetworkPlayer networkPlayer = new NetworkPlayer(this.database, player.getUniqueId());

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
                player.sendMessage(msgApi.getMessage(language, "command.fly.other.off", Placeholder.unparsed("player", target.getName())));
                target.sendMessage(msgApi.getMessage(language, "command.fly.self.off"));
            } else {
                target.setAllowFlight(true);
                target.setFlying(true);
                player.sendMessage(msgApi.getMessage(language, "command.fly.other.on", Placeholder.unparsed("player", target.getName())));
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
