package com.toonystank.moodyHordes.manager.mobs.implimentation;

import com.toonystank.moodyHordes.data.MobData;
import com.toonystank.moodyHordes.manager.mobs.MoodyMob;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class MoodyZombie extends MoodyMob<Zombie> {

    public MoodyZombie(ServerLevel world, MobData mobData) {
        super(new Zombie(EntityType.ZOMBIE, world), world, mobData);
    }

    /**
     * Override the hurt method to apply shield effect and regeneration on the zombie.
     */
    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return super.hurt(source,amount);
    }

    /**
     * Override the tick method to handle regeneration on the zombie.
     */
    @Override
    public void tick() {
        super.tick();
    }

    /**
     * Spawn the MoodyZombie at the given location.
     */
    public void spawnZombie(Location location) {
        spawn(location);
    }
}
