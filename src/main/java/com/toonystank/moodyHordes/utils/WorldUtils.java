package com.toonystank.moodyHordes.utils;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

public class WorldUtils {

    public static ServerLevel getWorld(World world) {
        CraftWorld craftWorld = (CraftWorld) world;
        return craftWorld.getHandle();
    }

    public static Location getRegionCenter(World world, ProtectedRegion region) {
        if (region != null) {
            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint();

            int centerX = (min.x() + max.x()) / 2;
            int centerY = (min.y() + max.y()) / 2;
            int centerZ = (min.z() + max.z()) / 2;

            return new Location(world, centerX, centerY, centerZ);
        }
        return null;
    }
}
