package com.laudynetwork.networkutils.api.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIHandler<P extends Plugin> implements Listener {

    private final Map<UUID, GUI> openGUIs = new HashMap<>();
    private final P plugin;

    public GUIHandler(P plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * if you try to open a GUI but the player has already an open ui please use the GUIHandler#openDelayed method
     * */
    public synchronized void open(Player player, GUI ui) {
        openGUIs.put(player.getUniqueId(), ui);
        ui.open(player);
    }

    /**
     * if you try to open a GUI but the player has already an open ui please use the GUIHandler#openDelayed method
     * */
    public void openDelayed(Player player, GUI ui) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> open(player, ui), 10L);
    }

    public boolean isPlayerInUI(UUID uuid) {
        return openGUIs.containsKey(uuid);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player))
            return;

        if (!openGUIs.containsKey(player.getUniqueId()))
            return;

        var gui = openGUIs.get(player.getUniqueId());

        event.setCancelled(true);

        gui.handleClick(event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event) {

        if (!(event.getPlayer() instanceof Player player))
            return;

        openGUIs.remove(player.getUniqueId());
        System.out.println("closed gui");

    }
}
