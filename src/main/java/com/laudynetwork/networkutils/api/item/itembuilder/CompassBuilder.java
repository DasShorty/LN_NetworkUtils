package com.laudynetwork.networkutils.api.item.itembuilder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;

public class CompassBuilder implements ItemStackBuilder<CompassBuilder> {

    private final ItemStack itemStack;
    private final CompassMeta meta;

    public CompassBuilder(Material material) {
        if (!material.name().contains("COMPASS"))
            throw new IllegalStateException("Illegal material type in compass builder!");
        this.itemStack = new ItemStack(material);
        this.meta = (CompassMeta) itemStack.getItemMeta();
    }

    public CompassBuilder() {
        this.itemStack = new ItemStack(Material.COMPASS);
        this.meta = null;
    }

    public CompassBuilder(ItemStack itemStack) {
        if (!itemStack.getType().name().contains("COMPASS"))
            throw new IllegalStateException("Illegal material type in compass builder!");
        this.itemStack = itemStack;
        this.meta = (CompassMeta) this.itemStack.getItemMeta();
    }

    public CompassBuilder setTarget(Location location) {
        this.meta.setLodestone(location);
        return this;
    }

    public CompassBuilder setTracked(boolean tracked) {
        this.meta.setLodestoneTracked(tracked);
        return this;
    }

    @Override
    public CompassBuilder itemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    @Override
    public CompassBuilder enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    @Override
    public CompassBuilder unEnchant(Enchantment enchantment) {
        meta.removeEnchant(enchantment);
        return this;
    }

    @Override
    public CompassBuilder displayName(Component component) {
        meta.displayName(Component.empty().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).append(component));
        return this;
    }

    @Override
    public CompassBuilder lore(Component... components) {
        meta.lore(Arrays.asList(components));
        return this;
    }

    @Override
    public CompassBuilder lore(ArrayList<Component> components) {
        meta.lore(components);
        return this;
    }

    @Override
    public CompassBuilder amount(int amount) {
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
    public CompassBuilder persistentData(NamespacedKey namespacedKey, PersistentDataType type, Object value) {
        meta.getPersistentDataContainer().set(namespacedKey, type, value);
        return this;
    }

    @Override
    public ItemStack build() {

        if (this.meta != null) itemStack.setItemMeta(meta);
        return itemStack;
    }

}
