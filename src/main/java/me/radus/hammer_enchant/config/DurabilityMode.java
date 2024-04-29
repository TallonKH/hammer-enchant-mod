package me.radus.hammer_enchant.config;

import me.radus.hammer_enchant.HammerEnchantMod;

public enum DurabilityMode {
    NORMAL,
    SQRT;

    public static DurabilityMode fromString(String str) {
        switch (str.toUpperCase()) {
            case "SQRT" -> {
                return SQRT;
            }
            case "NORMAL" -> {
                return NORMAL;
            }
        }

        HammerEnchantMod.LOGGER.warn("Invalid value for config 'DurabilityMode': '{}', assuming default value", str);
        return NORMAL;
    }

    public int calculate(int rawDamage) {
        switch (this) {
            case SQRT -> {
                return (int) Math.ceil(Math.sqrt(rawDamage));
            }
            case NORMAL -> {
                return rawDamage;
            }
        }

        // Unreachable
        return -1;
    }
}
