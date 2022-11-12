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

public abstract class UI implements InventoryHolder {

    private final Inventory inventory;
    @Getter
    private final Player opener;

    @Getter
    private final Map<Integer, GUIItem> guiItems = new HashMap<>();

    public UI(Component displayName, int size, Player player) {
        this.inventory = Bukkit.createInventory(this, size, displayName);
        this.opener = player;
    }

    public void setBackground(Material material) {
        for (int i = 0; i < getInventory().getSize(); i++) {
            if (!getGuiItems().containsKey(i))
                getGuiItems().put(i, new GUIItem(i, new ItemBuilder(material).displayName(Component.text(" ")).itemFlags(ItemFlag.values()), (clicker, clickedItem) -> GUIItem.GUIAction.CANCEL));
        }
    }

    public void set(int position, ItemStackBuilder itemStackBuilder, GUIItem.ClickAction action) {
        set(new GUIItem(position, itemStackBuilder, action));
    }

    public void set(GUIItem item) {
        getGuiItems().put(item.position(), item);
    }

    protected abstract void generate();

    public void openUI(Player player) {
        generate();
        player.openInventory(getInventory());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

}
