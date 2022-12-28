package com.laudynetwork.networkutils;

import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import com.laudynetwork.networkutils.listeners.Base64Listener;
import com.laudynetwork.networkutils.listeners.CommandProtectionListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class NetworkUtils extends JavaPlugin {
    @Getter
    private static SQLConnection DB_CONNECTION;


    @Override
    public void onLoad() {

        getSLF4JLogger().info("Loading config!");
        var utilsConfig = this.getConfig();

        getSLF4JLogger().info("Creating Connection Pool...");
        DB_CONNECTION = new SQLConnection(utilsConfig.getString("language.jdbc"), utilsConfig.getString("language.user"), utilsConfig.getString("language.pwd"));

        getSLF4JLogger().info("Finished creating connection pool!");
        getSLF4JLogger().info("Ready for Db Handling!");
    }

    @Override
    public void onEnable() {

        MessageBackend backend = new MessageBackend(DB_CONNECTION, "networkutils");

        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new Base64Listener(), this);
        pm.registerEvents(new CommandProtectionListener(backend), this);
        getSLF4JLogger().info("loaded!");
    }

    @Override
    public void onDisable() {
        // ignore
    }
}
