package com.toonystank.moodyHordes.data.mob;

import com.toonystank.moodyHordes.data.Item;

public record Equipped(Type type,
                       Item item) {

    public enum Type {
        MAIN_HAND,
        OFF_HAND,
        HELMET,
        BOOTS,
        LEGGINGS,
        CHESTPLATE;

        public static Type fromString(String string) {
            for (Type value : values()) {
                if (value.name().equalsIgnoreCase(string)) {
                    return value;
                }
            }
            return null;
        }
    }
}
