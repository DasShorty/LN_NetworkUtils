package com.laudynetwork.networkutils.essentials.language;

import com.laudynetwork.networkutils.api.gui.GUIHandler;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class LanguageCommand implements CommandExecutor {

    private final MessageBackend msgBackend;
    private final GUIHandler<Plugin> guiHandler;
    private final MessageAPI msgApi;

    public LanguageCommand(MessageBackend msgBackend, GUIHandler<Plugin> guiHandler) {
        this.msgBackend = msgBackend;
        this.guiHandler = guiHandler;
        this.msgApi = new MessageAPI(this.msgBackend, MessageAPI.PrefixType.SYSTEM);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.msgApi.getMessage(TranslationLanguage.ENGLISH, "command.only.player"));
            return true;
        }

        val networkPlayer = new NetworkPlayer(this.msgBackend.getConnection(), player.getUniqueId());
        val title = this.msgApi.getTranslation(networkPlayer.getLanguage(), "");

        guiHandler.open(player, new LanguageUI(player, title, this.msgApi, this.msgBackend));
        return true;
    }
}
