package com.laudynetwork.networkutils.utils.commands;

import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProtocolCommand implements CommandExecutor {

    private final SQLConnection connection;

    public ProtocolCommand(SQLConnection connection) {
        this.connection = connection;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("only for players");
            return true;
        }

        NetworkPlayer networkPlayer = new NetworkPlayer(this.connection, player.getUniqueId());

        player.sendMessage("Protocol Version: " + networkPlayer.getPlayerVersion());

        return true;
    }
}
