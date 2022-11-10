package com.laudynetwork.networkutils.api.gui;

import com.laudynetwork.networkutils.api.gui.impl.SimpleUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIHandler<P extends Plugin> implements Listener {

    private final Map<UUID, UI> openGUIs = new HashMap<>();

    public GUIHandler(P plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public synchronized void open(Player player, SimpleUI simpleUI) {
        openGUIs.put(player.getUniqueId(), simpleUI);
        simpleUI.openUI(player);
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

        var item = gui.getGuiItems().get(event.getSlot());

        switch (item.action().onClick(player, event.getCurrentItem())) {
            case CANCEL -> event.setCancelled(true);
            case CLOSE -> player.closeInventory();
            case NONE -> {}
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event) {

        if (!(event.getPlayer() instanceof Player player))
            return;

        openGUIs.remove(player.getUniqueId());

    }
}
