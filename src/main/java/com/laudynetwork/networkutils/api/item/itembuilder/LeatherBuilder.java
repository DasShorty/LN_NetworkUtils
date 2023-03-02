package com.laudynetwork.networkutils.api.item.itembuilder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;

public class LeatherBuilder implements ItemStackBuilder<LeatherBuilder> {

    private final ItemStack itemStack;
    private final LeatherArmorMeta meta;

    public LeatherBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.meta = (LeatherArmorMeta) itemStack.getItemMeta();
    }

    public LeatherBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.meta = (LeatherArmorMeta) this.itemStack.getItemMeta();
    }

    @Override
    public LeatherBuilder displayName(Component component) {
        meta.displayName(Component.empty().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).append(component));
        return this;
    }

    @Override
    public LeatherBuilder lore(Component... components) {
        meta.lore(Arrays.asList(components));
        return this;
    }

    @Override
    public LeatherBuilder lore(ArrayList<Component> components) {
        meta.lore(components);
        return this;
    }

    @Override
    public LeatherBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    @Override
    public Object persistentData(PersistentDataType type, NamespacedKey namespacedKey) {
        return meta.getPersistentDataContainer().get(namespacedKey, type);
    }

    @Override
    public LeatherBuilder persistentData(NamespacedKey namespacedKey, PersistentDataType type, Object value) {
        meta.getPersistentDataContainer().set(namespacedKey, type, value);
        return this;
    }

    @Override
    public boolean hasPersistentData(NamespacedKey namespacedKey, PersistentDataType type) {
        return meta.getPersistentDataContainer().has(namespacedKey, type);
    }

    @Override
    public LeatherBuilder itemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    @Override
    public LeatherBuilder enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    @Override
    public LeatherBuilder unEnchant(Enchantment enchantment) {
        meta.removeEnchant(enchantment);
        return this;
    }

    public LeatherBuilder color(Color color) {
        meta.setColor(color);
        return this;
    }

    public Color color() {
        return meta.getColor();
    }

    @Override
    public ItemStack build() {
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
