package com.toonystank.moodyHordes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.toonystank.moodyHordes.MoodyHordes;
import net.minecraft.world.entity.ai.behavior.BackUpIfTooClose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Player;

@CommandAlias("moodyhordes")
public class MainCommand extends BaseCommand {



    @Subcommand("spawn")
    @CommandAlias("spawnZombie")
    public void onAddProfane(Player sender) {
        Bukkit.getLogger().info("Spawning a tiered mob");
        MoodyHordes.getInstance().getTierManager().spawnTieredMob();
    }
}
