package com.laudynetwork.networkutils.api.gui;

import com.laudynetwork.networkutils.api.item.itembuilder.ItemBuilder;
import com.laudynetwork.networkutils.api.item.itembuilder.ItemStackBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class GUI implements InventoryHolder {

    private final Inventory inventory;
    private final Player opener;
    @Getter
    private final Map<Integer, GUIItem> guiItems = new HashMap<>();

    public GUI(Component displayName, int size, Player opener) {
        this.inventory = Bukkit.createInventory(this, size, displayName);
        this.opener = opener;
    }

    public abstract void onGenerate(Player player);

    public void setBackground(Material material) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (!guiItems.containsKey(i))
                guiItems.put(i, new GUIItem(i, new ItemBuilder(material).displayName(Component.text(" ")).itemFlags(ItemFlag.values()), (clicker, clickedItem) -> GUIItem.GUIAction.CANCEL));
        }
    }

    protected void set(int position, ItemStackBuilder itemStackBuilder, GUIItem.ClickAction action) {
        set(new GUIItem(position, itemStackBuilder, action));
    }

    protected void set(GUIItem item) {
        guiItems.put(item.position(), item);
    }

    private void generate() {
        onGenerate(opener);
        for (int i = 0; i < inventory.getSize(); i++) {
            var item = guiItems.get(i);
            inventory.setItem(i, item.itemStackBuilder().itemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS).build());
        }
    }

    public void openGUI(Player player) {
        generate();
        player.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
