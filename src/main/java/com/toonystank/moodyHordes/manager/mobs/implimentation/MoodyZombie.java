package com.toonystank.moodyHordes.manager.mobs.implimentation;

import com.toonystank.moodyHordes.data.MobData;
import com.toonystank.moodyHordes.manager.mobs.MoodyMob;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;

public class MoodyZombie extends MoodyMob<Zombie> {


    public MoodyZombie(ServerLevel world, MobData mobData) {
        super(EntityType.ZOMBIE, new Zombie(world), world, mobData);
    }


}
