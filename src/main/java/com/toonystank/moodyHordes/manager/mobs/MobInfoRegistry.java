package com.toonystank.moodyHordes.manager.mobs;

import com.toonystank.moodyHordes.data.MobData;
import com.toonystank.moodyHordes.manager.mobs.implimentation.MoodySkeleton;
import com.toonystank.moodyHordes.manager.mobs.implimentation.MoodySpider;
import com.toonystank.moodyHordes.manager.mobs.implimentation.MoodyZombie;
import com.toonystank.moodyHordes.utils.MessageUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;

import java.util.HashMap;
import java.util.Map;

public class MobInfoRegistry {

    private static final Map<String, MobInfo<?, ?>> mobMap = new HashMap<>();

    public MobInfoRegistry() {
        loadDefaults();
    }

    /**
     * Register default mob types.
     */
    public void loadDefaults() {
        register("ZOMBIE", MoodyZombie.class, Zombie.class);
        register("SKELETON", MoodySkeleton.class, Skeleton.class);
        register("SPIDER", MoodySpider.class, Spider.class);
    }

    /**
     * Register a mob type with its class and base entity class.
     *
     * @param mobName   The name of the mob.
     * @param mobClass  The class of the custom MoodyMob.
     * @param baseClass The base entity type (e.g., Zombie.class).
     * @param <T>       The base type of the Minecraft mob.
     * @param <M>       The custom MoodyMob class type.
     * @return The MobInfo containing the registration details.
     */
    public <T extends Mob, M extends MoodyMob<T>> MobInfo<T, M> register(String mobName, Class<M> mobClass, Class<T> baseClass) {
        if (mobMap.containsKey(mobName)) {
            throw new IllegalArgumentException("Mob with name '" + mobName + "' is already registered.");
        }

        MobInfo<T, M> mobInfo = new MobInfo<>(mobName, mobClass, baseClass);
        MessageUtils.toConsole("registering mob " + mobName + " " + mobClass + " " + baseClass);
        mobMap.put(mobName.toLowerCase(), mobInfo);
        return mobInfo;
    }

    /**
     * Retrieve mob info by its name.
     *
     * @param name The name of the mob.
     * @return The MobInfo corresponding to the mob name.
     */
    public static MobInfo<?, ?> getMobInfo(String name) {
        if (mobMap.containsKey(name.toLowerCase())) {
            MessageUtils.toConsole("contains the " + name);
        }else {
            MessageUtils.toConsole("dose not contain the mob info");
        }
        return mobMap.get(name.toLowerCase());
    }

    /**
     * Create a new instance of a registered custom MoodyMob.
     *
     * @param name      The name of the mob.
     * @param world     The world the mob will spawn in.
     * @param mobData   The data specific to the mob (attributes, abilities, etc.).
     * @param <T>       The base type of the mob.
     * @param <M>       The custom MoodyMob class type.
     * @return An instance of the registered MoodyMob or null if not found.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Mob, M extends MoodyMob<T>> M createMob(String name, ServerLevel world, MobData mobData) {
        MobInfo<T, M> mobInfo = (MobInfo<T, M>) getMobInfo(name);
        if (mobInfo == null) {
            throw new IllegalArgumentException("No mob registered with name '" + name + "'.");
        }

        try {
            // Create a new instance using the world and mobData (no need for EntityType)
            return mobInfo.mobClass().getConstructor(ServerLevel.class, MobData.class)
                    .newInstance(world, mobData);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error creating instance of mob: " + name, e);
        }
    }

    /**
     * MobInfo stores information about a registered mob type.
     *
     * @param mobName   The name of the mob.
     * @param mobClass  The class of the custom MoodyMob.
     * @param baseClass The base entity type (e.g., Zombie.class).
     * @param <T>       The base type of the Minecraft mob.
     * @param <M>       The custom MoodyMob class type.
     */
    public record MobInfo<T extends Mob, M extends MoodyMob<T>>(String mobName, Class<M> mobClass, Class<T> baseClass) {}
}
