package com.laudynetwork.networkutils.api.resourcePackAPI.item;

import com.laudynetwork.networkutils.api.item.itembuilder.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public interface CustomItems {
    ItemStack getItem();

    ItemBuilder getItemBuilder();
}
