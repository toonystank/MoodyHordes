package com.toonystank.moodyHordes.data.mob;

public record Attribute(double health,
                        double damage,
                        double armor,
                        double speed,
                        double knockBackResistance,
                        double attackSpeed,
                        double followRange) {

    public static Attribute withDefaults() {
        return new Attribute(
                20,
                5,
                0,
                0.23,
                0,
                1,
                35
        );
    }
}
