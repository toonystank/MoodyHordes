package com.toonystank.moodyHordes.data;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public record RegionData(String region,
                         String regionName,
                         String regionDescription,
                         String regionWorld,
                         String regionWorldGuardRegion) {

    public ProtectedRegion getRegion() {
        World world = getRegionWorld();
        if (world == null) {
            return null;
        }
        RegionManager regionManager = getRegionManager(world);
        if (regionManager == null) {
            return null;
        }
        return regionManager.getRegion(regionWorldGuardRegion);
    }

    public World getRegionWorld() {
        return Bukkit.getWorld(regionWorld);
    }

    private @Nullable RegionManager getRegionManager(World world) {
        com.sk89q.worldedit.world.World worldGuardWorld = BukkitAdapter.adapt(world);
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(worldGuardWorld);
    }
}
