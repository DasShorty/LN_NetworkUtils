package com.laudynetwork.networkutils.api.location.commandimpl;

import com.laudynetwork.networkutils.api.MongoDatabase;
import com.laudynetwork.networkutils.api.location.DatabaseLocation;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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

public class LocationCommand implements CommandExecutor, TabCompleter {
    private final MongoDatabase database;
    private final MessageAPI msgAPI = MessageAPI.create(MessageAPI.PrefixType.SYSTEM);

    public LocationCommand(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.msgAPI.getMessage("en", "command.only.player"));
            return true;
        }

        val networkPlayer = new NetworkPlayer(this.database, player.getUniqueId());
        val language = networkPlayer.getLanguage();

        if (!player.hasPermission("networkutils.location.use")) {
            player.sendMessage(this.msgAPI.getMessage(language, "command.permission.missing"));
            return true;
        }

        switch (args.length) {
            case 0, 1 -> {

                if (args.length == 0) {
                    player.sendMessage(this.msgAPI.getMessage(language, "command.use", Placeholder.unparsed("command", "/location <add/remove/list/get>")));
                    return true;
                }

                switch (args[0].toLowerCase()) {
                    case "add" -> player.sendMessage(this.msgAPI.getMessage(language, "command.use", Placeholder.unparsed("command", "/location add <location-key>")));
                    case "remove" -> player.sendMessage(this.msgAPI.getMessage(language, "command.use", Placeholder.unparsed("command", "/location remove <location-key>")));
                    case "get" -> player.sendMessage(this.msgAPI.getMessage(language, "command.use", Placeholder.unparsed("command", "/location get <location-key>")));
                    case "list" -> {
                        player.sendMessage(this.msgAPI.getMessage(language, "location.command.list"));
                        DatabaseLocation.getAllLocationNames(this.database).forEach(s -> player.sendMessage(Component.text(s).clickEvent(ClickEvent.suggestCommand("/location get " + s))));
                    }
                }
            }
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "add" -> {

                        val location = player.getLocation();

                        val key = args[1];
                        if (DatabaseLocation.existsLocation(key, this.database)) {
                            player.sendMessage(this.msgAPI.getMessage(language, "command.location.exist"));
                            return true;
                        }

                        DatabaseLocation.createLocation(key, location, this.database);

                        player.sendMessage(this.msgAPI.getMessage(language, "command.location.add"));


                    }

                    case "remove" -> {

                        if (!DatabaseLocation.existsLocation(args[1], this.database)) {
                            player.sendMessage(this.msgAPI.getMessage(language, "command.location.not.exist"));
                            return true;
                        }

                        val sqlLocation = DatabaseLocation.fromDatabase(args[1], this.database);
                        sqlLocation.deleteLocation();

                        player.sendMessage(this.msgAPI.getMessage(language, "command.location.remove"));

                    }

                    case "get" -> {

                        player.sendMessage(args[1]);

                        if (!DatabaseLocation.existsLocation(args[1], this.database)) {
                            player.sendMessage(this.msgAPI.getMessage(language, "command.location.not.exist"));
                            return true;
                        }

                        val sqlLocation = DatabaseLocation.fromDatabase(args[1], this.database);
                        val storageLocation = sqlLocation.getStoredLocation();

                        assert storageLocation != null;
                        val tpTranslation = this.msgAPI.getTranslation(language, "layout.tp").clickEvent(
                                ClickEvent.suggestCommand("/teleport " + storageLocation.getBlockX() + " " + storageLocation.getBlockY() + " " + storageLocation.getBlockZ())
                        );
                        val locationName = Component.text(args[1]);
                        val message = this.msgAPI.getMessage(language, "location.command.get");

                        player.sendMessage(message.append(locationName).append(Component.text(" ")).append(tpTranslation));

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
                    case "add" -> list.add("<Location Key>");
                    case "remove", "get" -> list.addAll(DatabaseLocation.getAllLocationNames(this.database));
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
