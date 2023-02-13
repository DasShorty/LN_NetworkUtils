package com.laudynetwork.networkutils;

import com.laudynetwork.networkutils.api.location.commandimpl.LocationCommand;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.player.resourcepack.ResourcePackHandler;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import com.laudynetwork.networkutils.api.tablist.TablistManager;
import com.laudynetwork.networkutils.essentials.FlyCommand;
import com.laudynetwork.networkutils.essentials.GamemodeCommand;
import com.laudynetwork.networkutils.essentials.vanish.VanishCommand;
import com.laudynetwork.networkutils.listeners.Base64Listener;
import com.laudynetwork.networkutils.listeners.CommandProtectionListener;
import com.laudynetwork.networkutils.listeners.PlayerJoinListener;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings("unused")
public final class NetworkUtils extends JavaPlugin {
    public static NetworkUtils getINSTANCE() {
        return INSTANCE;
    }

    public SQLConnection getDbConnection() {
        return dbConnection;
    }

    private static NetworkUtils INSTANCE;
    private SQLConnection dbConnection;
    @Getter
    private ResourcePackHandler resourcePackHandler;
    @Getter
    private TablistManager tablistManager;

    @Override
    public void onLoad() {
        INSTANCE = this;

        getSLF4JLogger().info("Loading config!");
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();

        this.dbConnection = new SQLConnection(config.getString("db.jdbc"), config.getString("db.user"), config.getString("db.pwd"));
    }

    @Override
    public void onEnable() {

        this.resourcePackHandler = new ResourcePackHandler(this);
        MessageBackend backend = new MessageBackend(this.dbConnection, "networkutils");

        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new Base64Listener(), this);
        pm.registerEvents(new CommandProtectionListener(backend), this);
        pm.registerEvents(new PlayerJoinListener(this.dbConnection), this);

        Objects.requireNonNull(getCommand("location")).setExecutor(new LocationCommand(backend));
        Objects.requireNonNull(getCommand("gamemode")).setExecutor(new GamemodeCommand(backend));
        Objects.requireNonNull(getCommand("fly")).setExecutor(new FlyCommand(backend));

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
        // ignore
    }
}
