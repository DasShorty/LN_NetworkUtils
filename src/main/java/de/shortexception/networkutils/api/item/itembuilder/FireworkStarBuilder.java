package de.shortexception.networkutils.api.item.itembuilder;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;

public class FireworkStarBuilder implements ItemStackBuilder<FireworkStarBuilder> {
    private final FireworkEffectMeta meta;
    private final ItemStack itemStack;

    public FireworkStarBuilder() {
        this.itemStack = new ItemStack(Material.FIREWORK_STAR);
        this.meta = (FireworkEffectMeta) itemStack.getItemMeta();
    }

    public FireworkStarBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        if (itemStack.getType() != Material.FIREWORK_STAR)
            throw new IllegalStateException("type must be FIREWORK_STAR");
        this.meta = (FireworkEffectMeta) this.itemStack.getItemMeta();
    }

    @Override
    public FireworkStarBuilder itemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    @Override
    public FireworkStarBuilder displayName(Component component) {
        meta.displayName(component);
        return this;
    }

    @Override
    public FireworkStarBuilder lore(Component... components) {
        meta.lore(Arrays.asList(components));
        return this;
    }

    @Override
    public FireworkStarBuilder lore(ArrayList<Component> components) {
        meta.lore(components);
        return this;
    }

    @Override
    public FireworkStarBuilder amount(int amount) {
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
    public FireworkStarBuilder persistentData(NamespacedKey namespacedKey, PersistentDataType type, Object value) {
        meta.getPersistentDataContainer().set(namespacedKey, type, value);
        return this;
    }

    @Override
    public ItemStack build() {
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public FireworkStarBuilder setColor(Color color) {
        meta.setEffect(FireworkEffect.builder().withColor(color).build());
        return this;
    }
}
