package com.laudynetwork.networkutils.registration;

import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import com.laudynetwork.networkutils.api.redis.Redis;
import lombok.val;
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

public class RegisterCommand implements CommandExecutor, TabCompleter {

    private final MessageAPI msgApi;
    private final MessageBackend msgBackend;

    private final WebsiteRegisterManager registerManager;

    public RegisterCommand(MessageBackend msgBackend, Redis redis) {
        this.msgBackend = msgBackend;
        this.msgApi = new MessageAPI(msgBackend, MessageAPI.PrefixType.SYSTEM);
        this.registerManager = new WebsiteRegisterManager(msgBackend.getConnection(), redis);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.msgApi.getMessage(TranslationLanguage.ENGLISH, "command.only.player"));
            return true;
        }

        val networkPlayer = new NetworkPlayer(this.msgBackend.getConnection(), player.getUniqueId());
        val language = networkPlayer.getLanguage();


        if (!player.hasPermission("networkutils.register")) {
            sender.sendMessage(this.msgApi.getMessage(language, "command.missing.permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(this.msgApi.getMessage(language, "command.usage", Placeholder.unparsed("command", "/register <mail>")));
            return true;

        }

        val mail = args[0];

        if (!this.registerManager.isValid(mail)) {
            sender.sendMessage(this.msgApi.getMessage(language, "command.register.invalid.mail", Placeholder.unparsed("mail", mail)));
            return true;
        }

        val registeredUser = this.registerManager.addUser(player.getUniqueId(), mail);
        if (!registeredUser.successfully()) {
            player.sendMessage(this.msgApi.getMessage(language, "command.register.registered"));
            return true;
        }

        String redirect = String.format("<click:open_url:'https://www.laudynetwork.com/%s/password-reset/%s'>link</click>", language.getDbName(), registeredUser.token().toString());

        player.sendMessage(this.msgApi.getMessage(language, "command.register.confirm",
                Placeholder.parsed("redirect", redirect)));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        val list = new ArrayList<String>();

        if (args.length == 0)
            return list;

        if (args.length == 1)
            list.add("<mail>");

        val completer = new ArrayList<String>();
        val current = args[args.length - 1];
        list.forEach(s -> {
            if (s.startsWith(current))
                completer.add(s);
        });

        return completer;
    }
}
