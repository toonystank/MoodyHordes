package com.toonystank.moodyHordes.manager.tiers;

import com.toonystank.moodyHordes.data.DataSection;
import com.toonystank.moodyHordes.data.Item;
import com.toonystank.moodyHordes.data.MobData;
import com.toonystank.moodyHordes.data.RegionData;
import com.toonystank.moodyHordes.data.mob.Abilities;
import com.toonystank.moodyHordes.data.mob.Attribute;
import com.toonystank.moodyHordes.data.mob.Drop;
import com.toonystank.moodyHordes.data.mob.Equipped;
import com.toonystank.moodyHordes.utils.ConfigManager;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import java.io.IOException;
import java.util.*;

public class TierData extends ConfigManager {

    private final ItemManager itemManager;
    @Getter
    private DataSection dataSection;
    @Getter
    private final List<MobData> mobData = new ArrayList<>();

    public TierData(String fileName, ItemManager itemManager) throws IOException {
        super(fileName, "tiers", false, true);
        this.itemManager = itemManager;
        load();
    }

    public void load() {
        loadDataSection();
        Set<String> mobNames = getConfigurationSection("mobs").getKeys(false);
        for (String mobName : mobNames) {
            ConfigurationSection section = getConfigurationSection("mobs." + mobName);
            MobData mobData = loadMob(section);
            this.mobData.add(mobData);
        }
    }
    public void loadDataSection() {
        ConfigurationSection dataSection = getConfigurationSection("data");
        if (dataSection == null) {
            throw new IllegalArgumentException("Data section is missing");
        }
        String name = dataSection.getString("name");
        String description = dataSection.getString("description");
        int spawn_chance = dataSection.getInt("spawn_chance");
        boolean region_specificEnabled = dataSection.getBoolean("region_specific.enable");
        String region_specificRegion = dataSection.getString("region_specific.region");
        RegionData regionData = RegionManager.getRegion(region_specificRegion);
        if (regionData == null) {
            throw new IllegalArgumentException("Region data is null in " + this.getFile() + " for region " + region_specificRegion);
        }
        boolean region_specificRemoveVanillaLoot = dataSection.getBoolean("region_specific.remove_vanilla_loot");
        this.dataSection = new DataSection(name, description, spawn_chance, region_specificEnabled, regionData, region_specificRemoveVanillaLoot);
    }

    private MobData loadMob(ConfigurationSection section) {
        if (section == null) {
            throw new IllegalArgumentException("Mob section is null");
        }

        String name = section.getString("name");
        String type = section.getString("type");
        boolean isShieldEnabled = section.getBoolean("abilities.shield");
        boolean isRegenerationEnabled = section.getBoolean("abilities.regeneration");
        Abilities abilities = new Abilities(isShieldEnabled, isRegenerationEnabled);
        Attribute attribute = loadAttributes(section.getConfigurationSection("attribute"));
        int spawnChance = section.getInt("spawn_chance");
        List<Drop> dropList = loadDrops(section.getConfigurationSection("drops"));
        Map<Equipped.Type, Equipped> equippedMap = loadEquippedItems(section.getConfigurationSection("equipped"));

        return new MobData(name, type, abilities, attribute, spawnChance, dropList, equippedMap);
    }

    private Attribute loadAttributes(ConfigurationSection attributeSection) {
        if (attributeSection == null) {
            return Attribute.withDefaults();
        }

        int health = attributeSection.getInt("health");
        int damage = attributeSection.getInt("damage");
        int armor = attributeSection.getInt("armor");
        int speed = attributeSection.getInt("speed");
        int knockbackResistance = attributeSection.getInt("knockback_resistance");
        int attackSpeed = attributeSection.getInt("attack_speed");
        int followRange = attributeSection.getInt("follow_range");

        return new Attribute(health, damage, armor, speed, knockbackResistance, attackSpeed, followRange);
    }

    private List<Drop> loadDrops(ConfigurationSection dropSection) {
        if (dropSection == null) {
            return new ArrayList<>();
        }

        List<Drop> dropList = new ArrayList<>();
        for (String dropName : dropSection.getKeys(false)) {
            ConfigurationSection itemSection = dropSection.getConfigurationSection(dropName);
            if (itemSection == null) {
                continue;
            }
            Item item = itemManager.loadItem(itemSection);
            dropList.add(new Drop(item));
        }
        return dropList;
    }

    private Map<Equipped.Type, Equipped> loadEquippedItems(ConfigurationSection equippedSection) {
        if (equippedSection == null) {
            return new HashMap<>();
        }

        Map<Equipped.Type, Equipped> equippedMap = new HashMap<>();
        for (String equippedName : equippedSection.getKeys(false)) {
            ConfigurationSection itemSection = equippedSection.getConfigurationSection(equippedName);
            if (itemSection == null) {
                continue;
            }
            Equipped.Type type = Equipped.Type.fromString(equippedName);
            Item item = itemManager.loadItem(itemSection);
            equippedMap.put(type, new Equipped(type, item));
        }
        return equippedMap;
    }
}