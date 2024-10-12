package com.toonystank.moodyHordes;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public class ParticleSystem {

    private final ServerLevel world;
    private final ParticleOptions particleType;
    private int ticks;
    private int maxTicks;
    private double radius;
    private double speed;
    private LivingEntity entity;

    public ParticleSystem(ServerLevel world, ParticleOptions particleType) {
        this.world = world;
        this.particleType = particleType;
        this.ticks = 0;
    }

    public ParticleSystem setEntity(LivingEntity entity) {
        this.entity = entity;
        return this;
    }

    public ParticleSystem setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public ParticleSystem setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public ParticleSystem setDuration(int maxTicks) {
        this.maxTicks = maxTicks;
        return this;
    }

    public void playSpiralEffect() {
        BiConsumer<Vec3, Integer> spiralPattern = this::spawnSpiralParticles;
        runEffect(spiralPattern);
    }

    public void playCircleEffect() {
        BiConsumer<Vec3, Integer> circlePattern = this::spawnCircleParticles;
        runEffect(circlePattern);
    }

    private void runEffect(BiConsumer<Vec3, Integer> particlePattern) {
        Vec3 position = entity != null ? entity.position() : Vec3.ZERO;
        for (int i = 0; i < maxTicks; i++) {
            particlePattern.accept(position, i);
        }
    }

    private void spawnSpiralParticles(Vec3 basePosition, int tick) {
        double angle = tick * speed;
        double xOffset = radius * Math.cos(angle);
        double zOffset = radius * Math.sin(angle);
        double yOffset = (tick * 0.1) % 2.0;

        Vec3 particlePos = basePosition.add(xOffset, yOffset, zOffset);
        world.sendParticles(particleType, particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0);
    }

    private void spawnCircleParticles(Vec3 basePosition, int tick) {
        double angle = 2 * Math.PI * (tick % 360) / 360;
        double xOffset = radius * Math.cos(angle);
        double zOffset = radius * Math.sin(angle);

        Vec3 particlePos = basePosition.add(xOffset, 0, zOffset);  // No height change
        world.sendParticles(particleType, particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0);
    }

    public void tick() {
        if (ticks < maxTicks) {
            ticks++;
            playSpiralEffect();
        }

    }

    public boolean isComplete() {
        return ticks >= maxTicks;
    }
}
