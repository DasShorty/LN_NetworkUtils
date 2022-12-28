package com.laudynetwork.networkutils.api.gui;

import com.laudynetwork.networkutils.api.item.itembuilder.ItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("rawtypes")
public record GUIItem(int position, ItemStackBuilder itemStackBuilder, ClickAction action) {

    public enum GUIAction {
        CLOSE,
        CANCEL,
        NONE
    }

    public interface ClickAction {
        GUIAction onClick(Player clicker, ItemStack clickedItem, ClickType clickType);
    }

}
