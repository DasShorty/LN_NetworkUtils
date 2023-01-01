package com.laudynetwork.networkutils.api.location.commandimpl;

import com.laudynetwork.networkutils.api.location.SQLLocation;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.Replacement;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LocationCommand implements CommandExecutor, TabCompleter {
    private final SQLConnection connection;
    private final MessageAPI msgAPI;

    public LocationCommand(MessageBackend msgBackend) {
        this.msgAPI = new MessageAPI(msgBackend, "system");
        this.connection = msgBackend.getConnection();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("only for players");
            return true;
        }

        if (!player.hasPermission("networkutils.location.use")) {
            player.sendMessage(this.msgAPI.getMessage(TranslationLanguage.ENGLISH, "command.permission.missing"));
            return true;
        }

        switch (args.length) {
            case 0, 1 -> {

                if (args.length == 0) {
                    player.sendMessage(this.msgAPI.getMessage(TranslationLanguage.ENGLISH, "command.use", new Replacement("%command%", "/location <add/remove/list/get>")));
                    return true;
                }

                switch (args[0].toLowerCase()) {
                    case "add" -> {
                        player.sendMessage(this.msgAPI.getMessage(TranslationLanguage.ENGLISH, "command.use", new Replacement("%command%", "/location add <location-key>")));
                    }
                    case "remove" -> {
                        player.sendMessage(this.msgAPI.getMessage(TranslationLanguage.ENGLISH, "command.use", new Replacement("%command%", "/location remove <location-key>")));
                    }
                    case "get" -> {
                        player.sendMessage(this.msgAPI.getMessage(TranslationLanguage.ENGLISH, "command.use", new Replacement("%command%", "/location get <location-key>")));
                    }
                    case "list" -> {
                        val locationNames = SQLLocation.getAllLocationNames(this.connection);

                        player.sendMessage(this.msgAPI.getMessage(TranslationLanguage.ENGLISH, "location-command-list"));

                        locationNames.forEach(s -> {

                            val sqlLocation = SQLLocation.fromSQL(s, connection);
                            val storedLocation = sqlLocation.getStoredLocation();
                            if (storedLocation == null)
                                return;

                            player.sendMessage(msgAPI.asHighlight(Component.text(s)).clickEvent(ClickEvent.suggestCommand("/tp " + storedLocation.getBlockX() + " " + storedLocation.getBlockY() + " " + storedLocation.getBlockZ())));
                        });

                    }
                }
            }
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "add" -> {

                        val location = player.getLocation();

                        if (SQLLocation.existsLocation(args[1], this.connection)) {
                            player.sendMessage(this.msgAPI.getMessage(TranslationLanguage.ENGLISH, "location-command-add-exist"));
                            return true;
                        }

                        SQLLocation.createLocation(args[1], location, this.connection);

                        player.sendMessage(this.msgAPI.getMessage(TranslationLanguage.ENGLISH, "location-command-add"));


                    }
                }
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        val list = new ArrayList<String>();

        switch (args.length) {
            case 0 -> {
                return list;
            }
            case 1 -> {
                list.add("add");
                list.add("remove");
                list.add("list");
                list.add("get");
            }
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "add" -> {
                        list.add("<Location Key>");
                    }
                    case "remove", "get" -> {
                        list.addAll(SQLLocation.getAllLocationNames(this.connection));
                    }
                }
            }
        }

        val current = args[args.length - 1];
        val completer = new ArrayList<String>();

        list.forEach(s -> {
            if (s.startsWith(current))
                completer.add(s);
        });

        return completer;
    }
}
