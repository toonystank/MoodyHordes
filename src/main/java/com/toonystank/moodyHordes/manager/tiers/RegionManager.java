package com.toonystank.moodyHordes.manager.tiers;

import com.toonystank.moodyHordes.data.RegionData;
import com.toonystank.moodyHordes.utils.ConfigManager;
import com.toonystank.moodyHordes.utils.MessageUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RegionManager extends ConfigManager {

    private static final List<RegionData> regions = new ArrayList<>();

    public RegionManager() throws IOException {
        super("Regions.yml",false,true);
        load();
    }

    public void load() {
        ConfigurationSection section = getConfigurationSection("Regions");

        for (String region : section.getKeys(false)) {
            ConfigurationSection regionSection = section.getConfigurationSection(region);
            if (regionSection == null) {
                throw new IllegalArgumentException("ConfigurationSection is null");
            }
            String name = regionSection.getString("name");
            String description = regionSection.getString("description");
            String world = regionSection.getString("world");
            String worldGuardRegion = regionSection.getString("worldGuardRegion");
            regions.add(new RegionData(region,name, description, world, worldGuardRegion));
        }
    }

    public static RegionData getRegion(String region) {
        for (RegionData regionData : regions) {
            MessageUtils.toConsole("provided Region: " +region + " Looping region "+ regionData.region());
            if (regionData.region().equalsIgnoreCase(region)) {
                return regionData;
            }
        }
        return null;
    }


}
