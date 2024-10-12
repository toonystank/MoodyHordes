package com.toonystank.moodyHordes.manager.tiers;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.toonystank.moodyHordes.data.ChanceData;
import com.toonystank.moodyHordes.data.MobData;
import com.toonystank.moodyHordes.manager.TierManager;
import com.toonystank.moodyHordes.manager.mobs.MobInfoRegistry;
import com.toonystank.moodyHordes.manager.mobs.MoodyMob;
import com.toonystank.moodyHordes.utils.ChanceSystem;
import com.toonystank.moodyHordes.utils.MessageUtils;
import com.toonystank.moodyHordes.utils.WorldUtils;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Tier implements ChanceData {

    private final TierManager tierManager;
    private final TierData tierData;

    private final List<MoodyMob<?>> mobs = new ArrayList<>();

    public Tier( String tierName,TierManager tierManager) throws IOException {
        this.tierManager = tierManager;
        tierData = new TierData(tierName + ".yml", tierManager.getItemManager());
        loadMobs();
    }


    public void loadMobs() {
        ServerLevel world = WorldUtils.getWorld(tierData.getDataSection().regionSpecificRegion().getRegionWorld());
        if (world == null) {
            MessageUtils.toConsole("World is null " + tierData.getDataSection().regionSpecificRegion().getRegionWorld());
            return;
        }
        for (MobData data : tierData.getMobData()) {
            Bukkit.getLogger().info("Loading tier mob " + data + " from " + tierData.getDataSection().name());
            MoodyMob<?> Mob = MobInfoRegistry.createMob(data.type(),world,data);
            mobs.add(Mob);
        }
    }

    public void spawnMobs() {
        ProtectedRegion region = tierData.getDataSection().regionSpecificRegion().getRegion();
        Location location = WorldUtils.getRegionCenter(tierData.getDataSection().regionSpecificRegion().getRegionWorld(), region);

        if (location == null) {
            return;
        }
        Bukkit.getLogger().info("Spawning mobs for " + tierData.getDataSection().name());
        MessageUtils.toConsole("mobs size: " + mobs.size());
        int totalSpawnWeight = ChanceSystem.calculateTotalSpawnWeight(mobs);
        for (MoodyMob<?> mob : mobs) {
            if (mob == null) continue;
            Bukkit.getLogger().info("Looping mob " + mob.getEntityType().toString());
            if (ChanceSystem.shouldSpawn(mob.getSpawnChance(), totalSpawnWeight)) {
                Bukkit.getLogger().info("Spawning chance met for " + mob.getEntityType().toString());
                mob.spawnMob(location);
            }
        }
    }


    @Override
    public int getSpawnChance() {
        return tierData.getDataSection().spawnChance();
    }

}
