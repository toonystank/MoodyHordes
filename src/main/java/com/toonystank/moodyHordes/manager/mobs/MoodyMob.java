package com.toonystank.moodyHordes.manager.mobs;

import com.toonystank.moodyHordes.MoodyHordes;
import com.toonystank.moodyHordes.ai.RegenerationGoal;
import com.toonystank.moodyHordes.ai.ShieldBlockingGoal;
import com.toonystank.moodyHordes.data.ChanceData;
import com.toonystank.moodyHordes.data.MobData;
import com.toonystank.moodyHordes.data.mob.Attribute;
import com.toonystank.moodyHordes.data.mob.Equipped;
import com.toonystank.moodyHordes.data.mob.Drop;
import com.toonystank.moodyHordes.utils.WorldUtils;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Getter
public class MoodyMob<T extends Mob> extends Mob implements ChanceData {

    private final EntityType<? extends Mob> entityType;
    private final T baseMob;
    private final ServerLevel world;
    private final MobData mobData;
    private ShieldBlockingGoal shieldBlockingGoal;
    private RegenerationGoal regenerationGoal;

    public MoodyMob(EntityType<? extends Mob> entityType, T baseMob, ServerLevel world, MobData mobData) {
        super(entityType, world);
        this.entityType = entityType;
        this.baseMob = baseMob;
        this.world = world;
        this.mobData = mobData;
        initializeGoals();

        if (mobData.attribute() != null)
            setAttributes(mobData.attribute());

        if (mobData.equippedMap() != null)
            equipItems(mobData.equippedMap());
    }

    /**
     * Initialize the AI goals for the mob, based on its abilities.
     */
    private void initializeGoals() {
        super.registerGoals();
        if (mobData.abilities() == null) return;

        if (mobData.abilities().isShieldEnabled()) {
            this.shieldBlockingGoal = new ShieldBlockingGoal(baseMob);
            this.goalSelector.addGoal(20, shieldBlockingGoal);
        }

        if (mobData.abilities().isRegenerationEnabled()) {
            this.regenerationGoal = new RegenerationGoal(baseMob);
            this.goalSelector.addGoal(21, regenerationGoal);
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (mobData.abilities() == null) return false;

        if (mobData.abilities().isShieldEnabled() && shieldBlockingGoal.canUse()) {
            amount = shieldBlockingGoal.reduceDamage(source, amount);
        }

        if (mobData.abilities().isRegenerationEnabled()) {
            regenerationGoal.onHurt();
        }

        return baseMob.hurt(source, amount);  // Delegate to base mob
    }

    @Override
    public void tick() {
        super.tick();
        if (mobData.abilities() == null) return;

        // Process regeneration each tick if enabled
        if (mobData.abilities().isRegenerationEnabled()) {
            regenerationGoal.onTick();
        }
    }

    /**
     * Set mob attributes using the provided Attribute record.
     */
    private void setAttributes(Attribute attributes) {
        setAttributeIfPresent(Attributes.MAX_HEALTH, attributes.health());
        baseMob.setHealth((float) attributes.health());

        setAttributeIfPresent(Attributes.ATTACK_DAMAGE, attributes.damage());
        setAttributeIfPresent(Attributes.ARMOR, attributes.armor());
        setAttributeIfPresent(Attributes.MOVEMENT_SPEED, attributes.speed());
        setAttributeIfPresent(Attributes.KNOCKBACK_RESISTANCE, attributes.knockBackResistance());
        setAttributeIfPresent(Attributes.ATTACK_SPEED, attributes.attackSpeed());
        setAttributeIfPresent(Attributes.FOLLOW_RANGE, attributes.followRange());
    }

    /**
     * Equip mob items based on the Equipped map from the Mob record.
     */
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

    /**
     * Set a specific attribute's base value if it is present.
     */
    private void setAttributeIfPresent(net.minecraft.world.entity.ai.attributes.Attribute attribute, double value) {
        AttributeInstance instance = baseMob.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (mobData.abilities() == null) return;
        if (mobData.abilities().isShieldEnabled()) {
            tag.putInt("ShieldCooldown", shieldBlockingGoal.getCooldown());
        }
        if (mobData.abilities().isRegenerationEnabled()) {
            tag.putInt("RegenTicks", regenerationGoal.getNoDamageTicks());
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        initializeGoals();
        if (mobData.attribute() == null) return;
        if (mobData.abilities().isShieldEnabled() && tag.contains("ShieldCooldown")) {
            shieldBlockingGoal.setCooldown(tag.getInt("ShieldCooldown"));
        }

        if (mobData.abilities().isRegenerationEnabled() && tag.contains("RegenTicks")) {
            regenerationGoal.setNoDamageTicks(tag.getInt("RegenTicks"));
        }
    }

    /**
     * Return the drop list for the mob.
     */
    public List<Drop> getDrops() {
        return mobData.dropList();
    }

    /**
     * Return the spawn chance of the mob.
     */
    public int getSpawnChance() {
        return mobData.spawnChance();
    }

    public void spawnMob(Location location) {
        ServerLevel worldServer = WorldUtils.getWorld(location.getWorld());
        if (worldServer == null) return;

        // Ensure this runs on the main thread
        Bukkit.getScheduler().runTask(MoodyHordes.getInstance(), () -> {
            Bukkit.getLogger().info("Trying to spawn mob " + baseMob.getType());
            MoodyMob<T> mob = new MoodyMob<>(entityType, baseMob, worldServer, mobData); // Pass entity type here
            mob.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            Bukkit.getLogger().info("Trying to spawn mob at " + location);
            worldServer.addFreshEntity(mob, CreatureSpawnEvent.SpawnReason.CUSTOM);
        });
    }

}
