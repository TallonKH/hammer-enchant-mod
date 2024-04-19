package me.radus.hammer_enchant.tag;

import me.radus.hammer_enchant.HammerEnchantMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> TILLABLE_BLOCK_TAG = tag("tillable");
        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(HammerEnchantMod.MOD_ID, name));
        }
    }
}
