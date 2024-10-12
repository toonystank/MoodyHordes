package com.toonystank.moodyHordes.manager.mobs;


import net.minecraft.world.entity.Mob;

public record MobType<T extends Mob>(MobMap mobEnumType, T mobType) {

}
