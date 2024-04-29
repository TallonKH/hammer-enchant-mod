package me.radus.hammer_enchant.config;

import me.radus.hammer_enchant.HammerEnchantMod;

public enum MiningSpeedMode {
    SUM,
    MAX;

    public static MiningSpeedMode fromString(String str) {
        switch (str.toUpperCase()) {
            case "SUM" -> {
                return SUM;
            }
            case "MAX" -> {
                return MAX;
            }
        }

        HammerEnchantMod.LOGGER.warn("Invalid value for config 'MiningSpeedMode': '{}', assuming default value", str);
        return MAX;
    }
}
