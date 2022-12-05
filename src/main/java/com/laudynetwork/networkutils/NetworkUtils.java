package com.laudynetwork.networkutils;

import com.laudynetwork.networkutils.listeners.Base64Listener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class NetworkUtils extends JavaPlugin {
    @Getter
    private static NetworkHandler NETWORK_HANDLER;

    @Override
    public void onEnable() {
        NETWORK_HANDLER = new NetworkHandler(this);
        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new Base64Listener(), this);
        getSLF4JLogger().info("loaded!");
    }

    @Override
    public void onDisable() {
        // ignore
    }
}
