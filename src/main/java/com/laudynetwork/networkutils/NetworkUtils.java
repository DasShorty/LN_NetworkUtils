package com.laudynetwork.networkutils;

import com.laudynetwork.networkutils.api.sql.SQLConnection;
import com.laudynetwork.networkutils.listeners.Base64Listener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class NetworkUtils extends JavaPlugin {
    @Getter
    private SQLConnection dbConnection;
    private static NetworkUtils INSTANCE;

    @Override
    public void onEnable() {

        INSTANCE = this;

        getSLF4JLogger().info("Loading config!");
        var utilsConfig = this.getConfig();

        getSLF4JLogger().info("Creating Connection Pool...");
        dbConnection = new SQLConnection(utilsConfig.getString("language.jdbc"), utilsConfig.getString("language.user"), utilsConfig.getString("language.pwd"));

        getSLF4JLogger().info("Finished creating connection pool!");
        getSLF4JLogger().info("Ready for Db Handling!");

        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new Base64Listener(), this);
        getSLF4JLogger().info("loaded!");
    }

    @Override
    public void onDisable() {
        // ignore
    }

    public static NetworkUtils getINSTANCE() {
        return INSTANCE;
    }
}
