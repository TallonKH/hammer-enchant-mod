package me.radus.hammer_enchant.config;

import me.radus.hammer_enchant.HammerEnchantMod;

import java.util.List;

public enum MiningSpeedMode {
    FOCUSED_BLOCK {
        @Override
        public float computeDestroyTime(float primaryDestroyTime, List<Float> allDestroyTimes) {
            return primaryDestroyTime;
        }
    },
    SUM {
        @Override
        public float computeDestroyTime(float primaryDestroyTime, List<Float> allDestroyTimes) {
            return allDestroyTimes.stream().reduce(0f, Float::sum);
        }
    },
    SQRT_SUM {
        @Override
        public float computeDestroyTime(float primaryDestroyTime, List<Float> allDestroyTimes) {
            return (float) Math.sqrt(allDestroyTimes.stream().reduce(0f, Float::sum));
        }
    },
    AVG {
        @Override
        public float computeDestroyTime(float primaryDestroyTime, List<Float> allDestroyTimes) {
            return allDestroyTimes.stream().reduce(0f, Float::sum) / allDestroyTimes.size();
        }
    },
    MAX {
        @Override
        public float computeDestroyTime(float primaryDestroyTime, List<Float> allDestroyTimes) {
            return allDestroyTimes.stream().reduce(0f, Float::max);
        }
    };

    public static final MiningSpeedMode DEFAULT = SQRT_SUM;

    public static MiningSpeedMode fromString(String str) {
        try {
            return MiningSpeedMode.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            HammerEnchantMod.LOGGER.warn("Invalid value for config 'MiningSpeedMode': '{}', assuming default value '{}'", str, DEFAULT);
            return DEFAULT;
        }
    }

    abstract public float computeDestroyTime(float primaryDestroyTime, List<Float> allDestroyTimes);
}
