package com.laudynetwork.networkutils.essentials.language;

import com.laudynetwork.networkutils.NetworkUtils;
import com.laudynetwork.networkutils.api.MongoDatabase;
import com.laudynetwork.networkutils.api.gui.GUIHandler;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class LanguageCommand implements CommandExecutor {

    private final MessageAPI msgApi = new MessageAPI(NetworkUtils.getINSTANCE().getMessageCache(), MessageAPI.PrefixType.SYSTEM);
    private final GUIHandler<Plugin> guiHandler;
    private final MongoDatabase database;
    private final Plugin plugin;

    public LanguageCommand(GUIHandler<Plugin> guiHandler, MongoDatabase database, Plugin plugin) {
        this.guiHandler = guiHandler;
        this.database = database;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.msgApi.getMessage("en", "command.only.player"));
            return true;
        }

        val networkPlayer = new NetworkPlayer(this.database, player.getUniqueId());
        val title = this.msgApi.getTranslation(networkPlayer.getLanguage(), "networkutils.language.ui.title");

        guiHandler.open(player, new LanguageUI(player, title, this.database, this.plugin));
        return true;
    }
}
