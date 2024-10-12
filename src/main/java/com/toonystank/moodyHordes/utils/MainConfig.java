package com.toonystank.moodyHordes.utils;

import com.toonystank.moodyHordes.MoodyHordes;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MainConfig extends ConfigManager {

    private List<String> tiers = new ArrayList<>();
    private int spawnInterval;

    public MainConfig(MoodyHordes plugin) throws IOException {
        super("config.yml",false,true);
        load();
    }

    private void load() {
        tiers = getStringList("tiers");
        spawnInterval = getInt("spawnInterval");
        Bukkit.getLogger().info("Loaded " + tiers.size() + " tiers");
    }
}
