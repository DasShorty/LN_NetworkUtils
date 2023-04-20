package com.laudynetwork.networkutils.essentials.control.api;

import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.List;

public interface ControlSubCommand {

    String id();

    void onCommand(Player player, Command command, String label, String[] args, MessageBackend msgBackend, MessageAPI msgApi, NetworkPlayer networkPlayer);

    List<String> onTabComplete(Player player, Command command, String label, String[] args, MessageBackend msgBackend, MessageAPI msgApi, NetworkPlayer networkPlayer);

}
