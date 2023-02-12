package com.laudynetwork.networkutils.essentials.vanish;

import com.laudynetwork.networkutils.NetworkUtils;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.Replacement;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VanishCommand implements CommandExecutor {


    private final MessageAPI msgApi;
    public VanishCommand(MessageBackend msgBackend) {
        this.msgApi = new MessageAPI(msgBackend, MessageAPI.PrefixType.SYSTEM);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(msgApi.getTranslation(TranslationLanguage.ENGLISH, "command.only.player"));
            return true;
        }

        NetworkPlayer networkPlayer = new NetworkPlayer(NetworkUtils.getINSTANCE().getDbConnection(), player.getUniqueId());

        val language = networkPlayer.getLanguage();

        if (!player.hasPermission("essentials.vanish.self") && !player.hasPermission("essentials.vanish.other")) {
            player.sendMessage(msgApi.getMessage(language, "command.missing.permission"));
            return true;
        }

        switch (args.length) {

            case 0 -> {

                VanishedPlayer vanishedPlayer = new VanishedPlayer(player.getUniqueId());

                if (vanishedPlayer.isVanished()) {
                    vanishedPlayer.update(false);
                    player.sendMessage(msgApi.getMessage(language, "command.vanish.self.off"));
                } else {
                    vanishedPlayer.update(true);
                    player.sendMessage(msgApi.getMessage(language, "command.vanish.self.on"));
                }

            }

            case 1 -> {

                if(Bukkit.getPlayerExact(args[0]) == null) {
                    player.sendMessage(msgApi.getMessage(language, "command.player.not.found"));
                    return true;
                }

                val target = Bukkit.getPlayerExact(args[0]);

                VanishedPlayer vanishedPlayer = new VanishedPlayer(target.getUniqueId());

                if (vanishedPlayer.isVanished()) {
                    vanishedPlayer.update(false);
                    target.sendMessage(msgApi.getMessage(language, "command.vanish.self.off"));
                    player.sendMessage(msgApi.getMessage(language, "command.vanish.other.off"));
                } else {
                    vanishedPlayer.update(true);
                    target.sendMessage(msgApi.getMessage(language, "command.vanish.self.on"));
                    player.sendMessage(msgApi.getMessage(language, "command.vanish.other.on"));
                }

            }

            default -> {
                player.sendMessage(this.msgApi.getMessage(language, "command.usage", new Replacement("%command%", "/vanish [Player]")));
            }

        }
        return true;
    }
}
