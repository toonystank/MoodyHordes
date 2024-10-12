package com.toonystank.moodyHordes.data;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record Item(String type,
                   String name,
                   @Nullable Integer modelData,
                   @Nullable List<String> lore,
                   @Nullable Map<Enchantment,Integer> enchants,
                   boolean hideEnchants,
                   boolean hideAttributes,
                   int dropChance) {

    public Material getMaterial() {
        Material material = Material.matchMaterial(type);
        if (material == null) {
            throw new IllegalArgumentException("Invalid material: " + type);
        }
        return material;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(getMaterial());
        itemStack.setAmount(1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            throw new IllegalArgumentException("ItemMeta is null");
        }
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemMeta.setCustomModelData(modelData);
        if (enchants != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }
        if (hideEnchants) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (hideAttributes) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
