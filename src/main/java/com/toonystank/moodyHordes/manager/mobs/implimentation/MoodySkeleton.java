package com.toonystank.moodyHordes.manager.mobs.implimentation;

import com.toonystank.moodyHordes.data.MobData;
import com.toonystank.moodyHordes.manager.mobs.MoodyMob;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Skeleton;

public class MoodySkeleton extends MoodyMob<Skeleton> {


    public MoodySkeleton( ServerLevel world, MobData mobData) {
        super(EntityType.SKELETON, new Skeleton(EntityType.SKELETON,world), world, mobData);
    }
}
