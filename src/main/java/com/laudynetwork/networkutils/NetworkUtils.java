package com.laudynetwork.networkutils;

import com.laudynetwork.database.mysql.MySQL;
import com.laudynetwork.database.redis.Redis;
import com.laudynetwork.networkutils.api.gui.GUIHandler;
import com.laudynetwork.networkutils.api.location.commandimpl.LocationCommand;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.tablist.TablistManager;
import com.laudynetwork.networkutils.essentials.FlyCommand;
import com.laudynetwork.networkutils.essentials.GamemodeCommand;
import com.laudynetwork.networkutils.essentials.control.ControlCommand;
import com.laudynetwork.networkutils.essentials.language.LanguageCommand;
import com.laudynetwork.networkutils.essentials.vanish.VanishCommand;
import com.laudynetwork.networkutils.listeners.Base64Listener;
import com.laudynetwork.networkutils.listeners.CommandProtectionListener;
import com.laudynetwork.networkutils.registration.RegisterCommand;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings("unused")
public final class NetworkUtils extends JavaPlugin {
    private static NetworkUtils INSTANCE;
    @Getter
    private MySQL sql;
    @Getter
    private TablistManager tablistManager;
    private Redis redis;

    public static NetworkUtils getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
        this.redis = new Redis();
        sql = new MySQL("89.163.129.221", "laudynetwork", "M8-)opnbhrn/z]kD", "laudynetwork");
        this.sql.connect();
    }

    @Override
    public void onEnable() {

        GUIHandler<Plugin> guiHandler = new GUIHandler<>(this);

        MessageBackend backend = new MessageBackend(this.sql, "networkutils");

        var pm = Bukkit.getPluginManager();

        pm.registerEvents(new Base64Listener(), this);
        pm.registerEvents(new CommandProtectionListener(backend), this);

        Objects.requireNonNull(getCommand("location")).setExecutor(new LocationCommand(backend));
        Objects.requireNonNull(getCommand("gamemode")).setExecutor(new GamemodeCommand(backend));
        Objects.requireNonNull(getCommand("fly")).setExecutor(new FlyCommand(backend));
        Objects.requireNonNull(getCommand("control")).setExecutor(new ControlCommand(backend));
        Objects.requireNonNull(getCommand("register")).setExecutor(new RegisterCommand(backend, redis));
        Objects.requireNonNull(getCommand("language")).setExecutor(new LanguageCommand(backend, guiHandler));

        VanishCommand vanishCommand = new VanishCommand(backend);
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

        getSLF4JLogger().info("loaded!");
    }

    @Override
    public void onDisable() {
        this.redis.shutdown();
        this.sql.close();
    }
}
