package com.laudynetwork.networkutils.essentials.vanish;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.laudynetwork.networkutils.NetworkUtils;
import com.laudynetwork.networkutils.api.MongoDatabase;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
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

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VanishCommand implements CommandExecutor, Listener {

    private final Cache<UUID, Boolean> vanishCache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).build();

    private final MessageAPI msgApi = new MessageAPI(NetworkUtils.getINSTANCE().getMessageCache(), MessageAPI.PrefixType.SYSTEM);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(msgApi.getTranslation("en", "command.only.player"));
            return true;
        }

        NetworkPlayer networkPlayer = new NetworkPlayer(Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(MongoDatabase.class)).getProvider(), player.getUniqueId());

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
                    player.sendMessage(msgApi.getMessage(language, "command.player.not.found", Placeholder.unparsed("player", args[0])));
                    return true;
                }

                val target = Bukkit.getPlayerExact(args[0]);

                assert target != null;
                VanishedPlayer vanishedPlayer = new VanishedPlayer(target.getUniqueId());

                if (isInVanish(target.getUniqueId())) {
                    this.vanishCache.put(target.getUniqueId(), false);
                    vanishedPlayer.update(false);
                    target.sendMessage(msgApi.getMessage(language, "command.vanish.self.off"));
                    player.sendMessage(msgApi.getMessage(language, "command.vanish.other.off", Placeholder.unparsed("player", target.getName())));
                } else {
                    this.vanishCache.put(target.getUniqueId(), true);
                    vanishedPlayer.update(true);
                    target.sendMessage(msgApi.getMessage(language, "command.vanish.self.on"));
                    player.sendMessage(msgApi.getMessage(language, "command.vanish.other.on", Placeholder.unparsed("player", target.getName())));
                }

            }

            default ->
                    player.sendMessage(this.msgApi.getMessage(language, "command.usage", Placeholder.unparsed("command", "/vanish [player]")));

        }
        return true;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {

        val player = event.getPlayer();

        val vanishedPlayer = new VanishedPlayer(player.getUniqueId());

        this.vanishCache.put(player.getUniqueId(), vanishedPlayer.isVanished());

        Bukkit.getOnlinePlayers().forEach(players -> {
            if (this.vanishCache.asMap().containsKey(player.getUniqueId()))
                if (this.vanishCache.asMap().get(player.getUniqueId()))
                    players.hidePlayer(NetworkUtils.getINSTANCE(), player);

            if (this.vanishCache.asMap().containsKey(players.getUniqueId()))
                if (this.vanishCache.asMap().get(players.getUniqueId()))
                    player.hidePlayer(NetworkUtils.getINSTANCE(), players);
        });
    }

    private boolean isInVanish(UUID uuid) {

        if (this.vanishCache.asMap().containsKey(uuid))
            return this.vanishCache.asMap().get(uuid);

        val vanishedPlayer = new VanishedPlayer(uuid);
        return vanishedPlayer.isVanished();
    }
}
