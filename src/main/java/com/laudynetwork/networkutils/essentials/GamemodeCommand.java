package com.laudynetwork.networkutils.essentials;

import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.val;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GamemodeCommand implements CommandExecutor, TabCompleter {

    private final MessageAPI msgApi;
    private final SQLConnection sqlConnection;

    public GamemodeCommand(MessageBackend msgBackend) {
        this.sqlConnection = msgBackend.getConnection();
        this.msgApi = new MessageAPI(msgBackend, MessageAPI.PrefixType.SYSTEM);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(msgApi.getTranslation(TranslationLanguage.ENGLISH, "command.only.player"));
            return true;
        }

        NetworkPlayer networkPlayer = new NetworkPlayer(this.sqlConnection, player.getUniqueId());

        val language = networkPlayer.getLanguage();

        if (!player.hasPermission("essentials.gamemode.self") && !player.hasPermission("essentials.gamemode.other")) {
            player.sendMessage(msgApi.getMessage(language, "command.missing.permission"));
            return true;
        }

        switch (args.length) {
            case 1 -> {

                if (!player.hasPermission("essentials.gamemode.self")) {
                    player.sendMessage(msgApi.getMessage(language, "command.missing.permission"));
                    return true;
                }

                switch (args[0].toLowerCase()) {
                    case "0", "survival" -> {
                        player.setGameMode(GameMode.SURVIVAL);
                        val gamemode = LegacyComponentSerializer.legacyAmpersand().serialize(msgApi.getTranslation(language, "command.gamemode.gm.survival"));
                        player.sendMessage(msgApi.getMessage(language, "command.gamemode.change.self", Placeholder.unparsed("gamemode", gamemode)));
                    }
                    case "1", "creative" -> {
                        player.setGameMode(GameMode.CREATIVE);
                        val gamemode = LegacyComponentSerializer.legacyAmpersand().serialize(msgApi.getTranslation(language, "command.gamemode.gm.creative"));
                        player.sendMessage(msgApi.getMessage(language, "command.gamemode.change.self", Placeholder.unparsed("gamemode", gamemode)));

                    }
                    case "2", "adventure" -> {
                        player.setGameMode(GameMode.ADVENTURE);
                        val gamemode = LegacyComponentSerializer.legacyAmpersand().serialize(msgApi.getTranslation(language, "command.gamemode.gm.adventure"));
                        player.sendMessage(msgApi.getMessage(language, "command.gamemode.change.self", Placeholder.unparsed("gamemode", gamemode)));

                    }
                    case "3", "spectator" -> {
                        player.setGameMode(GameMode.SPECTATOR);
                        val gamemode = LegacyComponentSerializer.legacyAmpersand().serialize(msgApi.getTranslation(language, "command.gamemode.gm.spectator"));
                        player.sendMessage(msgApi.getMessage(language, "command.gamemode.change.self", Placeholder.unparsed("gamemode", gamemode)));

                    }

                    default -> {
                        player.sendMessage(msgApi.getMessage(language, "command.usage", Placeholder.unparsed("command", "/gm <0-3>")));
                        return true;
                    }
                }

            }

            case 2 -> {

                if (!player.hasPermission("essentials.gamemode.other")) {
                    player.sendMessage(msgApi.getMessage(language, "command.missing.permission"));
                    return true;
                }

                if (Bukkit.getPlayer(args[1]) == null) {
                    player.sendMessage(msgApi.getMessage(language, "command.player.not.found", Placeholder.unparsed("player", args[0])));
                    return true;
                }

                val target = Bukkit.getPlayer(args[1]);

                assert target != null;

                switch (args[0].toLowerCase()) {
                    case "0", "survival" -> {
                        target.setGameMode(GameMode.SURVIVAL);
                        val gamemode = LegacyComponentSerializer.legacyAmpersand().serialize(msgApi.getTranslation(language, "command.gamemode.gm.survival"));
                        target.sendMessage(msgApi.getMessage(language, "command.gamemode.change.self", Placeholder.unparsed("gamemode", gamemode)));
                        player.sendMessage(msgApi.getMessage(language, "command.gamemode.change.other", Placeholder.unparsed("gamemode", gamemode), Placeholder.unparsed("player", target.getName())));
                    }
                    case "1", "creative" -> {
                        target.setGameMode(GameMode.CREATIVE);
                        val gamemode = LegacyComponentSerializer.legacyAmpersand().serialize(msgApi.getTranslation(language, "command.gamemode.gm.creative"));
                        target.sendMessage(msgApi.getMessage(language, "command.gamemode.change.self", Placeholder.unparsed("gamemode", gamemode)));
                        player.sendMessage(msgApi.getMessage(language, "command.gamemode.change.other", Placeholder.unparsed("gamemode", gamemode), Placeholder.unparsed("player", target.getName())));

                    }
                    case "2", "adventure" -> {
                        target.setGameMode(GameMode.ADVENTURE);
                        val gamemode = LegacyComponentSerializer.legacyAmpersand().serialize(msgApi.getTranslation(language, "command.gamemode.gm.adventure"));
                        target.sendMessage(msgApi.getMessage(language, "command.gamemode.change.self", Placeholder.unparsed("gamemode", gamemode)));
                        player.sendMessage(msgApi.getMessage(language, "command.gamemode.change.other", Placeholder.unparsed("gamemode", gamemode), Placeholder.unparsed("player", target.getName())));

                    }
                    case "3", "spectator" -> {
                        target.setGameMode(GameMode.SPECTATOR);
                        val gamemode = LegacyComponentSerializer.legacyAmpersand().serialize(msgApi.getTranslation(language, "command.gamemode.gm.spectator"));
                        target.sendMessage(msgApi.getMessage(language, "command.gamemode.change.self", Placeholder.unparsed("gamemode", gamemode)));
                        player.sendMessage(msgApi.getMessage(language, "command.gamemode.change.other", Placeholder.unparsed("gamemode", gamemode), Placeholder.unparsed("player", target.getName())));

                    }

                    default -> {
                        player.sendMessage(msgApi.getMessage(language, "command.gamemode.usage", Placeholder.unparsed("gamemode", "/gm <0-3> [player]")));
                        return true;
                    }
                }

            }

            default -> {
                player.sendMessage(msgApi.getMessage(language, "command.usage", Placeholder.unparsed("gamemode", "/gm <0-3> [player]")));
            }
        }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> list = new ArrayList<>();

        if (args.length == 0)
            return list;

        if (args.length == 1) {
            list.add("0");
            list.add("1");
            list.add("2");
            list.add("3");
        }

        if (args.length == 2) {
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::name).map(component -> PlainTextComponentSerializer.plainText().serialize(component)).toList());
        }

        String current = args[args.length-1];
        List<String> completer = new ArrayList<>();

        for (String s : list) {
            if (s.startsWith(current)) {
                completer.add(s);
            }
        }

        return completer;
    }
}
