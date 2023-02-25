package com.laudynetwork.networkutils.essentials.vanish;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.laudynetwork.networkutils.NetworkUtils;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.val;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VanishCommand implements CommandExecutor, Listener {

    private final Cache<UUID, Boolean> vanishCache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).build();

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

                if (isInVanish(player.getUniqueId())) {
                    this.vanishCache.put(player.getUniqueId(), false);
                    vanishedPlayer.update(false);
                    player.sendMessage(msgApi.getMessage(language, "command.vanish.self.off"));
                } else {
                    this.vanishCache.put(player.getUniqueId(), true);
                    vanishedPlayer.update(true);
                    player.sendMessage(msgApi.getMessage(language, "command.vanish.self.on"));
                }

            }

            case 1 -> {

                if (Bukkit.getPlayerExact(args[0]) == null) {
                    player.sendMessage(msgApi.getMessage(language, "command.player.not.found"));
                    return true;
                }

                val target = Bukkit.getPlayerExact(args[0]);

                VanishedPlayer vanishedPlayer = new VanishedPlayer(target.getUniqueId());

                if (isInVanish(player.getUniqueId())) {
                    this.vanishCache.put(player.getUniqueId(), false);
                    vanishedPlayer.update(false);
                    target.sendMessage(msgApi.getMessage(language, "command.vanish.self.off"));
                    player.sendMessage(msgApi.getMessage(language, "command.vanish.other.off"));
                } else {
                    this.vanishCache.put(player.getUniqueId(), true);
                    vanishedPlayer.update(true);
                    target.sendMessage(msgApi.getMessage(language, "command.vanish.self.on"));
                    player.sendMessage(msgApi.getMessage(language, "command.vanish.other.on"));
                }

            }

            default -> {
                player.sendMessage(this.msgApi.getMessage(language, "command.usage", Placeholder.parsed("command", "/vanish [player]")));
            }

        }
        return true;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {

        val player = event.getPlayer();

        val vanishedPlayer = new VanishedPlayer(player.getUniqueId());

        this.vanishCache.put(player.getUniqueId(), vanishedPlayer.isVanished());
    }

    private boolean isInVanish(UUID uuid) {

        if (this.vanishCache.asMap().containsKey(uuid))
            return this.vanishCache.asMap().get(uuid);

        val vanishedPlayer = new VanishedPlayer(uuid);
        return vanishedPlayer.isVanished();
    }
}
