package com.laudynetwork.networkutils.api.item.itembuilder;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class HeadBuilder implements ItemStackBuilder<HeadBuilder> {

    private final ItemStack itemStack;
    private final SkullMeta meta;

    public HeadBuilder() {
        this.itemStack = new ItemStack(Material.PLAYER_HEAD);
        meta = (SkullMeta) itemStack.getItemMeta();
    }

    public HeadBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        if (itemStack.getType() != Material.PLAYER_HEAD)
            throw new IllegalStateException("type must be PLAYER_HEAD");
        this.meta = (SkullMeta) this.itemStack.getItemMeta();
    }

    @Override
    public HeadBuilder itemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    @Override
    public HeadBuilder displayName(Component component) {
        meta.displayName(component);
        return this;
    }

    @Override
    public HeadBuilder lore(Component... components) {
        meta.lore(Arrays.asList(components));
        return this;
    }

    @Override
    public HeadBuilder lore(ArrayList<Component> components) {
        meta.lore(components);
        return this;
    }

    @Override
    public HeadBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    @Override
    public Object persistentData(PersistentDataType type, NamespacedKey namespacedKey) {
        return meta.getPersistentDataContainer().get(namespacedKey, type);
    }

    @Override
    public boolean hasPersistentData(NamespacedKey namespacedKey, PersistentDataType type) {
        return meta.getPersistentDataContainer().has(namespacedKey, type);
    }

    @Override
    public HeadBuilder persistentData(NamespacedKey namespacedKey, PersistentDataType type, Object value) {
        meta.getPersistentDataContainer().set(namespacedKey, type, value);
        return this;
    }

    public HeadBuilder headOwner(UUID headOwner) {
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(headOwner));
        return this;
    }

    @Override
    public ItemStack build() {
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
