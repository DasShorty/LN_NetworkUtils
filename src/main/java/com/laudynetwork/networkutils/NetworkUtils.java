package com.laudynetwork.networkutils;

import com.laudynetwork.networkutils.api.sql.SQLConnection;
import com.laudynetwork.networkutils.listeners.Base64Listener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class NetworkUtils extends JavaPlugin {
    private static NetworkUtils INSTANCE;
    private SQLConnection dbConnection;

    @Override
    public void onLoad() {
        INSTANCE = this;

        getSLF4JLogger().info("Loading config!");
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();

        dbConnection = new SQLConnection(config.getString("db.jdbc"), config.getString("db.user"), config.getString("db.pwd"));

    }

    @Override
    public void onEnable() {

        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new Base64Listener(), this);
        getSLF4JLogger().info("loaded!");
    }

    @Override
    public void onDisable() {
        // ignore
    }
}
