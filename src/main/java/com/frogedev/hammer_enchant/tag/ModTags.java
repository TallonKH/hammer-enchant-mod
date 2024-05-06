package com.frogedev.hammer_enchant.tag;

import com.frogedev.hammer_enchant.HammerEnchantMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> TILLABLE_BLOCK_TAG = tag("tillable");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(HammerEnchantMod.MOD_ID, name));
        }
    }
}
