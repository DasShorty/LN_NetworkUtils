package com.laudynetwork.networkutils;

import com.laudynetwork.database.redis.Redis;
import com.laudynetwork.networkutils.api.MongoDatabase;
import com.laudynetwork.networkutils.api.gui.GUIHandler;
import com.laudynetwork.networkutils.api.location.commandimpl.LocationCommand;
import com.laudynetwork.networkutils.api.messanger.backend.MessageCache;
import com.laudynetwork.networkutils.api.tablist.TablistManager;
import com.laudynetwork.networkutils.essentials.FlyCommand;
import com.laudynetwork.networkutils.essentials.GamemodeCommand;
import com.laudynetwork.networkutils.essentials.control.ControlCommand;
import com.laudynetwork.networkutils.essentials.control.api.ControlSubCommandHandler;
import com.laudynetwork.networkutils.essentials.language.LanguageCommand;
import com.laudynetwork.networkutils.essentials.vanish.VanishCommand;
import com.laudynetwork.networkutils.listeners.ChatListener;
import com.laudynetwork.networkutils.listeners.CommandProtectionListener;
import lombok.Getter;
import lombok.val;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings("unused")
public final class NetworkUtils extends JavaPlugin {
    private static NetworkUtils INSTANCE;
    @Getter
    private MongoDatabase database;
    @Getter
    private TablistManager tablistManager;
    private Redis redis;
    @Getter
    private MessageCache messageCache;

    public static NetworkUtils getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
        this.redis = new Redis();
        this.database = new MongoDatabase("mongodb://root:Pe7yeBPvimUcJ6B2kN@89.163.129.221:27017/?authMechanism=SCRAM-SHA-1");
    }

    @Override
    public void onEnable() {

        val guiHandler = new GUIHandler<Plugin>(this);
        Bukkit.getServicesManager().register(GUIHandler.class, guiHandler, this, ServicePriority.High);

        val subControlCommandHandler = new ControlSubCommandHandler();
        Bukkit.getServicesManager().register(ControlSubCommandHandler.class, subControlCommandHandler, this, ServicePriority.High);

        this.messageCache = new MessageCache();
        this.messageCache.loadFileInCache(this.getResource("translations/own/de.json"), "de");
        this.messageCache.loadFileInCache(this.getResource("translations/own/en.json"), "en");
        this.messageCache.loadFileInCache(this.getResource("translations/plugins/de.json"), "de");
        this.messageCache.loadFileInCache(this.getResource("translations/plugins/en.json"), "en");

        var pm = Bukkit.getPluginManager();

        val vanishCommand = new VanishCommand();
        pm.registerEvents(vanishCommand, this);
        Objects.requireNonNull(getCommand("vanish")).setExecutor(vanishCommand);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        LuckPerms luckPerms = null;

        if (provider != null) {
            luckPerms = provider.getProvider();
        }

        if (luckPerms == null) {
            getSLF4JLogger().error("LuckPerms instance is null!");
            return;
        }

        this.tablistManager = new TablistManager(this, luckPerms);

        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new CommandProtectionListener(this.database), this);

        Objects.requireNonNull(getCommand("location")).setExecutor(new LocationCommand(this.database));
        Objects.requireNonNull(getCommand("gamemode")).setExecutor(new GamemodeCommand(this.database));
        Objects.requireNonNull(getCommand("fly")).setExecutor(new FlyCommand(this.database));
        Objects.requireNonNull(getCommand("control")).setExecutor(new ControlCommand(subControlCommandHandler, this.database));
        Objects.requireNonNull(getCommand("language")).setExecutor(new LanguageCommand(guiHandler, this.database, this));

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "minecraft:player-send-to-server");
        getSLF4JLogger().info("loaded!");
    }

    @Override
    public void onDisable() {
        this.database.shutdown();
        this.redis.shutdown();
    }
}
