package com.laudynetwork.networkutils.api.gui.impl;

import com.laudynetwork.networkutils.api.gui.UI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

@SuppressWarnings("rawtypes")
public abstract class SimpleUI extends UI {

    public SimpleUI(Component displayName, int size, Player opener) {
        super(displayName, size, opener);
    }

    public abstract void onGenerate(Player player);

    @Override
    protected void generate() {
        onGenerate(getOpener());
        for (int i = 0; i < getInventory().getSize(); i++) {
            var item = getGuiItems().get(i);
            getInventory().setItem(i, item.itemStackBuilder().itemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS).build());
        }
    }
}
