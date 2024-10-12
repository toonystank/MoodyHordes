package com.toonystank.moodyHordes.data;


import com.toonystank.moodyHordes.data.mob.Abilities;
import com.toonystank.moodyHordes.data.mob.Attribute;
import com.toonystank.moodyHordes.data.mob.Drop;
import com.toonystank.moodyHordes.data.mob.Equipped;
import com.toonystank.moodyHordes.utils.MessageUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record MobData(String name,
                      String type,
                      @Nullable Abilities abilities,
                      @Nullable Attribute attribute,
                      int spawnChance,
                      @Nullable List<Drop> dropList,
                      @Nullable Map<Equipped.Type,Equipped> equippedMap) {


    @SuppressWarnings("unchecked")
    public EntityType<? extends Mob> getEntityType() {
        MessageUtils.toConsole("Getting EntityType for " + type);
        ResourceLocation resourceLocation = new ResourceLocation("minecraft", type.toLowerCase());
        Bukkit.getLogger().info("ResourceLocation: " + resourceLocation);

        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(resourceLocation);
        Bukkit.getLogger().info("EntityType: " + entityType);
        return (EntityType<? extends Mob>) entityType;

    }

}

