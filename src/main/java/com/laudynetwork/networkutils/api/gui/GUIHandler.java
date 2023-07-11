package com.laudynetwork.networkutils.api.gui;

import com.laudynetwork.networkutils.api.gui.event.CloseReason;
import com.laudynetwork.networkutils.api.gui.event.UICloseEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
     */
    public synchronized void open(Player player, GUI ui) {
        openGUIs.put(player.getUniqueId(), ui);
        ui.open(player);
    }

    public synchronized void close(Player player) {
        if (!this.openGUIs.containsKey(player.getUniqueId()))
            return;
        this.openGUIs.get(player.getUniqueId()).onClose(player);
        openGUIs.remove(player.getUniqueId());
    }

    public void openDelayed(Player player, GUI ui, CloseReason reason) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> reOpen(player, ui, reason), 1L);
    }

    private void reOpen(Player player, GUI ui, CloseReason reason) {
        if (isPlayerInUI(player.getUniqueId()))
            openGUIs.get(player.getUniqueId()).close(player, reason);
        open(player, ui);
    }

    public boolean isPlayerInUI(UUID uuid) {
        return openGUIs.containsKey(uuid);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player))
            return;

        if (isPlayerInUI(player.getUniqueId())) {
            return;
        }

        var gui = openGUIs.get(player.getUniqueId());

        event.setCancelled(true);

        gui.handleClick(event);
    }

    @EventHandler
    public void onUIClose(UICloseEvent event) {
        if (event.getCloseReason() == CloseReason.NEW_UI)
            return;
        close(event.getPlayer());
    }
}
