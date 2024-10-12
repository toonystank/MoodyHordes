package com.toonystank.moodyHordes.manager.tiers;

import com.toonystank.moodyHordes.data.Item;
import com.toonystank.moodyHordes.utils.ConfigManager;
import com.toonystank.moodyHordes.utils.MessageUtils;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;
import java.util.*;

@Getter
public class ItemManager extends ConfigManager {

    private final List<Item> items = new ArrayList<>();

    private Map<String,Item> itemMap = new HashMap<>();

    public ItemManager() throws IOException {
        super("items.yml",false,true);
        load();
    }

    private void load() {
        Set<String> itemNames = getConfigurationSection("items").getKeys(false);
        for (String itemName : itemNames) {
            ConfigurationSection section = getConfigurationSection("items." + itemName);
            Item item = loadItem(section);
            itemMap.put(itemName.toLowerCase(),item);
            items.add(item);
        }
    }

    public Item loadItem(ConfigurationSection section) {
        if (section == null) {
            throw new IllegalArgumentException("ConfigurationSection is null");
        }
        String type = section.getString("type");
        if (type == null) {
            throw new IllegalArgumentException("Item type is null");
        }
        if (type.startsWith("ITEM:")) {
            MessageUtils.toConsole("Item type: " + type);
            String finalType = type;
            type = type.substring(5);
            MessageUtils.toConsole("Final type: " + type);
            return Optional.ofNullable(itemMap.get(type.toLowerCase()))
                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + finalType));
        }
        int modelData = section.getInt("modelData");
        String name = section.getString("name");
        List<String> lore = section.getStringList("lore");
        Map<Enchantment,Integer> enchants = new HashMap<>();
        loadEnchantments(section, enchants);
        boolean hideEnchants = section.getBoolean("hideEnchants");
        boolean hideAttributes = section.getBoolean("hideAttributes");
        int dropChance = section.getInt("dropChance");
        return new Item(type, name, modelData, lore, enchants, hideEnchants, hideAttributes, dropChance);
    }

    public void loadEnchantments(ConfigurationSection section,Map<Enchantment,Integer> enchants) {
        ConfigurationSection enchantmentSection = section.getConfigurationSection("enchantments");
        if (enchantmentSection == null) {
            MessageUtils.toConsole("No enchantments section found in configuration.");
            return;
        }
        for (String key : enchantmentSection.getKeys(false)) {
            String formattedKey = key.toLowerCase();
            MessageUtils.toConsole("Enchantment key: " + key);
            Enchantment enchantment = getEnchantmentByKey(formattedKey);
            if (enchantment == null) {
                MessageUtils.toConsole("Invalid enchantment key: " + key);
                continue;
            }
            int level = enchantmentSection.getInt(key);
            enchants.put(enchantment, level);
        }
    }

    private Enchantment getEnchantmentByKey(String key) {
        try {
            return Enchantment.getByKey(NamespacedKey.minecraft(key));
        } catch (IllegalArgumentException e) {
            MessageUtils.toConsole("Error retrieving enchantment: " + key + " - " + e.getMessage());
            return null;
        }
    }

}
