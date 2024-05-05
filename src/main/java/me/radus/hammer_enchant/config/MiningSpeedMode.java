package me.radus.hammer_enchant.config;

import me.radus.hammer_enchant.HammerEnchantMod;

import java.util.List;

public enum MiningSpeedMode {
    SUM {
        @Override
        public float computeDestroyTime(float primaryDestroyTime, List<Float> allDestroyTimes) {
            return allDestroyTimes.stream().reduce(0f, Float::sum);
        }
    },
    MAX {
        @Override
        public float computeDestroyTime(float primaryDestroyTime, List<Float> allDestroyTimes) {
            return allDestroyTimes.stream().reduce(0f, Float::max);
        }
    };

    public static final MiningSpeedMode DEFAULT = MAX;
    public static MiningSpeedMode fromString(String str) {
        try {
            return MiningSpeedMode.valueOf(str.toUpperCase());
        } catch(IllegalArgumentException e){
            HammerEnchantMod.LOGGER.warn("Invalid value for config 'MiningSpeedMode': '{}', assuming default value", str);
            return DEFAULT;
        }
    }

    abstract public float computeDestroyTime(float primaryDestroyTime, List<Float> allDestroyTimes);
}
