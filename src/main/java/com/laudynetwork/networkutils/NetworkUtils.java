package com.laudynetwork.networkutils;

import com.laudynetwork.networkutils.listeners.Base64Listener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class NetworkUtils extends JavaPlugin {

    @Getter
    private static NetworkUtils instance;
    @Getter
    private NetworkHandler networkHandler;

    @Override
    public void onEnable() {
        instance = this;
        //var networkHandler = new NetworkHandler("mongodb://anthony:anthony@localhost:27017/?authMechanism=SCRAM-SHA-1&authSource=admin");

        var pm = Bukkit.getPluginManager();

        pm.registerEvents(new Base64Listener(), this);

        networkHandler = new NetworkHandler();

        getSLF4JLogger().info("loaded!");
    }

    @Override
    public void onDisable() {
        // ignore
    }
}
