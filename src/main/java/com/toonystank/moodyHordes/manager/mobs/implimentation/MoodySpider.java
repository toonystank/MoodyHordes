package com.toonystank.moodyHordes.manager.mobs.implimentation;

import com.toonystank.moodyHordes.data.MobData;
import com.toonystank.moodyHordes.manager.mobs.MoodyMob;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.monster.Spider;

public class MoodySpider extends MoodyMob<Spider> {

    public MoodySpider(ServerLevel world, MobData mobData) {
        super(new Spider(EntityType.SPIDER,world), world, mobData);
    }
}
