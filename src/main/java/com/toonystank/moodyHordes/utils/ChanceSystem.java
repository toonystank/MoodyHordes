package com.toonystank.moodyHordes.utils;

import com.toonystank.moodyHordes.data.ChanceData;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ChanceSystem {

    private static final double DEFAULT_FAILURE_PROBABILITY = 0.01; // Default 1% rare failure

    // Calculate the total spawn weight from the list of ChanceData
    public static int calculateTotalSpawnWeight(List<? extends ChanceData> data) {
        Bukkit.getLogger().info("Calculating total spawn weight from data: " + data);
        return data.stream()
                .filter(chanceData -> chanceData != null && chanceData.getSpawnChance() > 0)
                .mapToInt(ChanceData::getSpawnChance)
                .sum();
    }

    // Determine if something should spawn, with a rare failure event
    public static boolean shouldSpawn(int spawnChance, int totalSpawnWeight) {
        return shouldSpawn(spawnChance, totalSpawnWeight, DEFAULT_FAILURE_PROBABILITY);
    }

    // Overloaded method that allows customizing the rare failure probability
    public static boolean shouldSpawn(int spawnChance, int totalSpawnWeight, double failureProbability) {
        Bukkit.getLogger().info("Spawn chance: " + spawnChance + " | Total weight: " + totalSpawnWeight);

        // Handle edge cases
        if (spawnChance <= 0) {
            return false;
        }
        if (spawnChance >= 100) {
            return true;
        }

        // Calculate if the entity should spawn based on its spawn chance
        int randomValue = ThreadLocalRandom.current().nextInt(totalSpawnWeight) + 1;
        boolean meetsChance = randomValue <= spawnChance;

        // Introduce rare failure event
        boolean rareFailure = shouldForceFailure(failureProbability);
        if (rareFailure) {
            Bukkit.getLogger().warning("RARE FAILURE: Prevented spawn despite meeting chance conditions. (Random value: " + randomValue + ")");
        }

        boolean result = meetsChance && !rareFailure;
        logResult(result, randomValue, spawnChance, rareFailure);
        return result;
    }

    // Method to check if rare failure should happen
    private static boolean shouldForceFailure(double failureProbability) {
        return ThreadLocalRandom.current().nextDouble() < failureProbability;
    }

    // Log the result of the spawn check
    private static void logResult(boolean result, int randomValue, int spawnChance, boolean rareFailure) {
        if (result) {
            Bukkit.getLogger().info("Spawn successful! Random value: " + randomValue + " | Required: <= " + spawnChance);
        } else if (!rareFailure) {
            Bukkit.getLogger().info("Spawn failed! Random value: " + randomValue + " | Required: <= " + spawnChance);
        }
    }

}
