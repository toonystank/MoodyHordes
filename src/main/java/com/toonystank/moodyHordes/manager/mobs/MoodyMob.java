package com.toonystank.moodyHordes.manager.mobs;

import com.toonystank.moodyHordes.MoodyHordes;
import com.toonystank.moodyHordes.ai.RegenerationGoal;
import com.toonystank.moodyHordes.ai.ShieldBlockingGoal;
import com.toonystank.moodyHordes.data.ChanceData;
import com.toonystank.moodyHordes.data.MobData;
import com.toonystank.moodyHordes.data.mob.Attribute;
import com.toonystank.moodyHordes.data.mob.Equipped;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;

import java.util.Map;

@Getter
public abstract class MoodyMob<T extends Mob> implements ChanceData {

    protected final T baseMob;
    protected final ServerLevel world;
    protected final MobData mobData;
    protected ShieldBlockingGoal shieldBlockingGoal;
    protected RegenerationGoal regenerationGoal;

    public MoodyMob(T baseMob, ServerLevel world, MobData mobData) {
        this.baseMob = baseMob;
        this.world = world;
        this.mobData = mobData;

        initializeGoals();
        if (mobData.attribute() != null) {
            setAttributes(mobData.attribute());
        }
        if (mobData.equippedMap() != null) {
            equipItems(mobData.equippedMap());
        }
    }

    // Custom hurt method
    public boolean hurt(DamageSource source, float amount) {
        if (mobData.abilities() == null) {
            return false;
        }

        if (mobData.abilities().isShieldEnabled()) {
            amount = shieldBlockingGoal.reduceDamage(source, amount); // Use shield effect to reduce damage
        }

        return baseMob.hurt(source, amount); // Default hurt behavior
    }

    // Custom tick method
    public void tick() {
        if (mobData.abilities() != null && mobData.abilities().isRegenerationEnabled()) {
            regenerateHealth(); // Custom health regeneration
        }

        baseMob.tick();
    }

    private void regenerateHealth() {
        if (baseMob.getHealth() < baseMob.getMaxHealth()) {
            baseMob.setHealth(baseMob.getHealth() + 0.5f);
        }
    }

    // New spawn method to handle mob spawning and configuration
    public void spawn(Location location) {
        Bukkit.getScheduler().runTask(MoodyHordes.getInstance(), () -> {
                    baseMob.setPos(location.getX(), location.getY(), location.getZ());
                    world.addFreshEntity(baseMob);
                    if (mobData.attribute() != null) {
                        setAttributes(mobData.attribute());
                    }
                    if (mobData.equippedMap() != null) {
                        equipItems(mobData.equippedMap());
                    }
                    initializeGoals();
                });
        System.out.println("Custom mob spawned at: " + location);
    }

    private void initializeGoals() {
        if (mobData.abilities() == null) return;

        if (mobData.abilities().isShieldEnabled()) {
            this.shieldBlockingGoal = new ShieldBlockingGoal(baseMob);
            baseMob.goalSelector.addGoal(20, shieldBlockingGoal);
        }

        if (mobData.abilities().isRegenerationEnabled()) {
            this.regenerationGoal = new RegenerationGoal(baseMob);
            baseMob.goalSelector.addGoal(21, regenerationGoal);
        }
    }

    protected void setAttributes(Attribute attributes) {
        setAttributeIfPresent(Attributes.MAX_HEALTH, attributes.health());
        baseMob.setHealth((float) attributes.health());

        setAttributeIfPresent(Attributes.ATTACK_DAMAGE, attributes.damage());
        setAttributeIfPresent(Attributes.ARMOR, attributes.armor());
        setAttributeIfPresent(Attributes.MOVEMENT_SPEED, attributes.speed());
        setAttributeIfPresent(Attributes.KNOCKBACK_RESISTANCE, attributes.knockBackResistance());
        setAttributeIfPresent(Attributes.ATTACK_SPEED, attributes.attackSpeed());
        setAttributeIfPresent(Attributes.FOLLOW_RANGE, attributes.followRange());
    }

    private void equipItems(Map<Equipped.Type, Equipped> equippedMap) {
        for (Map.Entry<Equipped.Type, Equipped> entry : equippedMap.entrySet()) {
            Equipped.Type type = entry.getKey();
            Equipped equipped = entry.getValue();

            switch (type) {
                case MAIN_HAND -> setItemSlot(EquipmentSlot.MAINHAND, equipped);
                case OFF_HAND -> setItemSlot(EquipmentSlot.OFFHAND, equipped);
                case HELMET -> setItemSlot(EquipmentSlot.HEAD, equipped);
                case CHESTPLATE -> setItemSlot(EquipmentSlot.CHEST, equipped);
                case LEGGINGS -> setItemSlot(EquipmentSlot.LEGS, equipped);
                case BOOTS -> setItemSlot(EquipmentSlot.FEET, equipped);
                default -> throw new IllegalArgumentException("Invalid equipment type: " + type);
            }
        }
    }

    private void setItemSlot(EquipmentSlot slot, Equipped equipped) {
        baseMob.setItemSlot(slot, CraftItemStack.asNMSCopy(equipped.item().getItemStack()));
        baseMob.setDropChance(slot, equipped.item().dropChance());
    }

    private void setAttributeIfPresent(net.minecraft.world.entity.ai.attributes.Attribute attribute, double value) {
        AttributeInstance instance = baseMob.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }
    @Override
    public int getSpawnChance() {
        return mobData.spawnChance();
    }
}
