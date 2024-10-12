package com.toonystank.moodyHordes.manager;

import com.toonystank.moodyHordes.manager.tiers.ItemManager;
import com.toonystank.moodyHordes.manager.tiers.Tier;
import com.toonystank.moodyHordes.utils.ChanceSystem;
import com.toonystank.moodyHordes.utils.MainConfig;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TierManager {

    private final MainConfig mainConfig;
    private final Random random = new Random();

    public List<Tier> tiers = new ArrayList<>();
    @Getter
    private ItemManager itemManager;


    public TierManager(MainConfig mainConfig) throws IOException {
        this.mainConfig = mainConfig;
        load();
    }


    public void load() throws IOException {
        itemManager = new ItemManager();
        List<String> tierNames = mainConfig.getTiers();
        for (String tierName : tierNames) {
            try {
                Tier tier = new Tier(tierName,this);
                tiers.add(tier);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void spawnTieredMob() {
        int totalSpawnWeight = ChanceSystem.calculateTotalSpawnWeight(tiers);
        for (Tier tier : tiers) {
            if (tier == null) continue;
            if (ChanceSystem.shouldSpawn(tier.getSpawnChance(), totalSpawnWeight)) {
                Bukkit.getLogger().info("tier " + tier.getTierData().getDataSection().name() + " met the chance to try to spawn mobs");
                tier.spawnMobs();
            }
        }
    }

}
