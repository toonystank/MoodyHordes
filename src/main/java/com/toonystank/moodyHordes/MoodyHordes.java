package com.toonystank.moodyHordes;

import co.aikar.commands.PaperCommandManager;
import com.toonystank.moodyHordes.commands.MainCommand;
import com.toonystank.moodyHordes.manager.TierManager;
import com.toonystank.moodyHordes.manager.mobs.MobInfoRegistry;
import com.toonystank.moodyHordes.manager.tiers.RegionManager;
import com.toonystank.moodyHordes.utils.MainConfig;
import com.toonystank.moodyHordes.utils.MessageUtils;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
@Getter
public final class MoodyHordes extends JavaPlugin {

    @Getter
    private static MoodyHordes instance;
    private MainConfig mainConfig;
    private RegionManager regionManager;
    private TierManager tierManager;
    private MobInfoRegistry mobInfoRegistry;

    @Override
    public void onEnable() {
        instance = this;
        try {
            initializeConfig();
            initializeManagers();
            registerCommands();
            scheduleTieredMobSpawning(mainConfig.getSpawnInterval());
        } catch (IOException e) {
            MessageUtils.toConsole("Failed to initialize MoodyHordes plugin");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void initializeConfig() throws IOException {
        mainConfig = new MainConfig(this);
    }

    private void initializeManagers() throws IOException {
        mobInfoRegistry = new MobInfoRegistry();
        regionManager = new RegionManager();
        tierManager = new TierManager(mainConfig);
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");
        manager.registerCommand(new MainCommand());
    }

    private void scheduleTieredMobSpawning(int spawnInterval) {
        getServer().getScheduler().runTaskTimerAsynchronously(this, tierManager::spawnTieredMob, 0, spawnInterval * 20L);
    }

    @Override
    public void onDisable() {
        MessageUtils.toConsole("MoodyHordes plugin disabled");
    }
}