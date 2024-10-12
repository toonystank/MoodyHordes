package com.toonystank.moodyHordes.manager.mobs;

import lombok.Getter;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;

@Getter
public enum MobMap {
    ZOMBIE("Zombie", Zombie.class),
    SKELETON("Skeleton", Skeleton.class),
    SPIDER("Spider", Spider.class),
    CREEPER("Creeper", Creeper.class)
    ;


    private final String type;
    private final Class<? extends Mob> entityType;
    MobMap(String type, Class<? extends Mob> entityType) {
        this.type = type;
        this.entityType = entityType;

    }

    

    public static MobMap getMobMap(Mob mob) {
        for (MobMap mobMap : values()) {
            if (mobMap.getEntityType().equals(mob.getClass())) {
                return mobMap;
            }
        }
        return null;
    }
}
