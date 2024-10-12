package com.toonystank.moodyHordes.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class ShieldBlockingGoal extends Goal {

    private final LivingEntity entity;
    private int blockTicks;
    private int shieldCooldown;

    private static final int SHIELD_COOLDOWN_TIME = 10;
    private static final float SHIELD_BREAK_VOLUME = 1.0f;
    private static final float SHIELD_BREAK_PITCH = 1.0f;

    public ShieldBlockingGoal(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return hasShield() && isAttackingEntityInFront() && shieldCooldown <= 0;
    }

    @Override
    public void start() {
        blockTicks = 20;
        if (hasShield()) {
            this.entity.startUsingItem(InteractionHand.OFF_HAND);
        }
    }

    @Override
    public void tick() {
        if (blockTicks > 0) {
            blockTicks--;
        }
        if (shieldCooldown > 0) {
            shieldCooldown--;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return blockTicks > 0;
    }

    @Override
    public void stop() {
        blockTicks = 0;
        if (hasShield()) {
            this.entity.stopUsingItem();
        }
    }

    private boolean hasShield() {
        ItemStack offhand = this.entity.getItemBySlot(EquipmentSlot.OFFHAND);
        return offhand != null && offhand.getItem() == Items.SHIELD;
    }

    private boolean isAttackingEntityInFront() {
        LivingEntity attacker = this.entity.getLastHurtByMob();
        if (attacker == null) {
            return false;
        }
        Vec3 lookVec = this.entity.getLookAngle();
        Vec3 attackVec = new Vec3(attacker.getX() - this.entity.getX(),
                attacker.getY() - this.entity.getY(),
                attacker.getZ() - this.entity.getZ()).normalize();
        double dotProduct = lookVec.dot(attackVec);
        return dotProduct > Math.cos(Math.toRadians(60));
    }

    public float reduceDamage(DamageSource source, float amount) {
        if (this.canUse()) {
            this.entity.getCommandSenderWorld().playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.NEUTRAL, 1.0f, 1.0f);

            if (isExplosion(source)) {
                amount *= 0.5F;
            } else {
                amount = 0;
            }

            if (source.getEntity() instanceof LivingEntity attacker && isAxeInUse(attacker)) {
                applyShieldCooldown();
            }
        }

        return amount;
    }

    private void applyShieldCooldown() {
        shieldCooldown = SHIELD_COOLDOWN_TIME;
        this.entity.getCommandSenderWorld().playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundEvents.SHIELD_BREAK, SoundSource.NEUTRAL, SHIELD_BREAK_VOLUME, SHIELD_BREAK_PITCH);
    }

    private boolean isExplosion(DamageSource source) {
        return source.getMsgId().equals("explosion") || source.getMsgId().equals("explosion.player");
    }

    private boolean isAxeInUse(LivingEntity entity) {
        ItemStack mainHand = entity.getMainHandItem();
        ItemStack offHand = entity.getOffhandItem();
        return (mainHand.getItem() instanceof AxeItem || offHand.getItem() instanceof AxeItem);
    }

    public int getCooldown() {
        return shieldCooldown;
    }

    public void setCooldown(int shieldCooldown) {
        this.shieldCooldown = shieldCooldown;
    }
}
