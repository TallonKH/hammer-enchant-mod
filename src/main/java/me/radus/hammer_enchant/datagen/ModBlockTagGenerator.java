package me.radus.hammer_enchant.datagen;

import me.radus.hammer_enchant.HammerEnchantMod;
import me.radus.hammer_enchant.tag.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, HammerEnchantMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.Blocks.TILLABLE_BLOCK_TAG)
                .add(
                        Blocks.DIRT,
                        Blocks.GRASS_BLOCK,
                        Blocks.DIRT_PATH,
                        Blocks.COARSE_DIRT,
                        Blocks.MYCELIUM,
                        Blocks.PODZOL
                );
    }
}
