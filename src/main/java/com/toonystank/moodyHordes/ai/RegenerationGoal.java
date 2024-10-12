package com.toonystank.moodyHordes.ai;

import com.toonystank.moodyHordes.ParticleSystem;
import com.toonystank.moodyHordes.utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class RegenerationGoal extends Goal {

    private final LivingEntity entity;
    private int regenCooldown;
    @Setter
    @Getter
    private int noDamageTicks;
    private final int maxNoDamageTicks = 300;
    private final float regenAmount = 0.5f;
    private final ParticleSystem particleSystem;

    public RegenerationGoal(LivingEntity entity) {
        this.entity = entity;
        this.noDamageTicks = 0;
        this.particleSystem = new ParticleSystem((ServerLevel) entity.level(), ParticleTypes.ENCHANT)
                .setEntity(entity)
                .setRadius(1.5)
                .setSpeed(0.2)
                .setDuration(60);
    }

    @Override
    public boolean canUse() {
        return this.entity.getHealth() < this.entity.getMaxHealth();
    }

    @Override
    public void start() {
        MessageUtils.toConsole("Regeneration goal started");
        regenCooldown = 20;
    }

    @Override
    public void tick() {
        if (!(noDamageTicks >= maxNoDamageTicks) && regenCooldown >= 0) {
            MessageUtils.toConsole("Regen cooldown: " + regenCooldown);
            regenCooldown--;
            return;
        }

        if (noDamageTicks >= maxNoDamageTicks) {
            regenerateHealth();
            regenCooldown = 20;
            if (this.entity.getHealth() < this.entity.getMaxHealth()) {
                particleSystem.playSpiralEffect();
            }
        }

    }

    private void regenerateHealth() {
        MessageUtils.toConsole("Regenerating health: " + this.entity.getHealth());
        if (this.entity.getHealth() < this.entity.getMaxHealth()) {
            this.entity.setHealth(this.entity.getHealth() + regenAmount);
            MessageUtils.toConsole("Regenerating health started: " + this.entity.getHealth());
        }
    }

    public void onTick() {
        noDamageTicks++;
    }

    public void onHurt() {
        noDamageTicks = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.getHealth() < this.entity.getMaxHealth();
    }
}
