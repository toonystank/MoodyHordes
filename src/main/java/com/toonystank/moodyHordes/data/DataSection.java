package com.toonystank.moodyHordes.data;

public record DataSection(String name,
                          String description,
                          int spawnChance,
                          boolean regionSpecificEnabled,
                          RegionData regionSpecificRegion,
                          boolean regionSpecificRemoveVanillaLoot) {

}
