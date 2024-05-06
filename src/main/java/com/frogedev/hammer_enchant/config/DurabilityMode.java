package com.frogedev.hammer_enchant.config;

import com.frogedev.hammer_enchant.HammerEnchantMod;

public enum DurabilityMode {
    FULL {
        @Override
        public int computeDamage(int raw) {
            return raw;
        }
    },
    SQRT {
        @Override
        public int computeDamage(int raw) {
            return (int) Math.ceil(Math.sqrt(raw));
        }
    },
    ONE {
        @Override
        public int computeDamage(int raw) {
            return 1;
        }
    };

    public static final DurabilityMode DEFAULT = SQRT;

    public static DurabilityMode fromString(String str) {
        try {
            return DurabilityMode.valueOf(str);
        } catch (IllegalArgumentException e) {
            HammerEnchantMod.LOGGER.warn("Invalid value for config 'DurabilityMode': '{}', assuming default value '{}'", str, DEFAULT);
            return DEFAULT;
        }
    }

    public abstract int computeDamage(int raw);
}
