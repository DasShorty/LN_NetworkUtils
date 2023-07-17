/*
    --------------------------
    Project : ServerTechnology
    Package : de.shortexception.eventsystem.gui
    Date 27.06.2022
    @author ShortException
    --------------------------
*/


package com.laudynetwork.networkutils.api.gui;

import com.laudynetwork.networkutils.api.gui.event.CloseReason;
import com.laudynetwork.networkutils.api.gui.event.UICloseEvent;
import com.laudynetwork.networkutils.api.item.itembuilder.ItemBuilder;
import com.laudynetwork.networkutils.api.item.itembuilder.ItemStackBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@SuppressWarnings("unused")
public abstract class GUI implements InventoryHolder {

    private final Player player;
    private final int size;
    private final Inventory inventory;
    private final Map<Integer, GUIItem> guiItemMap;
    private Material background = Material.GRAY_STAINED_GLASS_PANE;

    public GUI(Player player, Component displayName, int size) {
        this.player = player;
        this.size = size;
        guiItemMap = new HashMap<>();
        inventory = Bukkit.createInventory(this, size, displayName);
    }

    public void setBackground(Material background) {
        this.background = background;
    }

    public abstract void generateGUI(Player player);

    protected void generate() {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (!guiItemMap.containsKey(i)) {
                guiItemMap.put(i, new GUIItem(i, new ItemBuilder(background).displayName(Component.empty()).itemFlags(ItemFlag.values()), (clicker, clickedItem, clickType) -> GUIItem.GUIAction.CANCEL));
            }
        }
        guiItemMap.forEach((slot, item) -> {

            if (item.itemStackBuilder().build().getType() == Material.AIR) {
                inventory.setItem(slot, item.itemStackBuilder().build());
                return;
            }

            inventory.setItem(slot, item.itemStackBuilder().itemFlags(ItemFlag.values()).build());
        });
    }

    protected void updateGUI() {
        inventory.clear();
        generateGUI(player);
        generate();
    }

    public abstract void onClose(Player player);

    public void close(Player player, CloseReason closeReason) {
        onClose(player);
        Bukkit.getPluginManager().callEvent(new UICloseEvent(player, this, closeReason));
    }

    public void open(Player player) {
        generateGUI(player);
        generate();
        player.openInventory(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getClickedInventory().equals(inventory))) return;
        if (!(event.getWhoClicked() instanceof Player clicked)) return;

        int index = event.getSlot();

        GUIItem item = this.guiItemMap.get(index);

        if (item == null || item.action() == null)
            return;

        GUIItem.GUIAction action = item.action().onClick(clicked, event.getCurrentItem(), event.getClick());

        switch (action) {
            case CLOSE -> {
                event.setCancelled(true);
                close(player, CloseReason.CLOSE);
            }
            case NONE -> event.setCancelled(false);
            case CANCEL -> event.setCancelled(true);
            case CANCEL_AND_NEW -> {
                event.setCancelled(true);
                close(player, CloseReason.NEW_UI);
            }
        }
    }

    protected void set(int index, @NotNull ItemStackBuilder<?> itemStackBuilder) {
        set(index, itemStackBuilder, null);
    }

    protected void set(int index, @NotNull ItemStackBuilder<?> itemStackBuilder, GUIItem.ClickAction itemCompletion) {
        set(index, new GUIItem(index, itemStackBuilder, itemCompletion));
    }

    private void set(int index, GUIItem item) {
        if (index >= size)
            throw new IllegalArgumentException("invalid index " + index + " the inventory has only " + size + " slots! [" + getClass().getName() + "]");

        this.guiItemMap.remove(index);
        if (item != null && item.itemStackBuilder().build() != null)
            this.guiItemMap.put(index, item);

        generate();
    }

    protected void clearAll() {
        this.guiItemMap.clear();
        this.inventory.clear();
    }

    protected void clear() {
        this.inventory.clear();
    }

}

