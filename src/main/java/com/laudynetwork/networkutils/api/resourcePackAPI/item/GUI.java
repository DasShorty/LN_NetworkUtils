package com.laudynetwork.networkutils.api.resourcePackAPI.item;

import com.laudynetwork.networkutils.api.item.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GUI implements CustomItems {
    GRAY_ENDER_EYE(1);


    private final Integer modelId;

    GUI(Integer modelId) {
        this.modelId = modelId;
    }

    public ItemStack getItem() {
        return getItemBuilder().build();
    }

    public ItemBuilder getItemBuilder() {
        return new ItemBuilder(Material.GREEN_WOOL).customModelData(modelId);
    }
}
