package com.frogedev.hammer_enchant.core;

import com.frogedev.hammer_enchant.ModConfig;
import com.frogedev.hammer_enchant.tag.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TillingHammerAction implements IHammerAction {
    private static final Vec3 BLOCK_HIGHLIGHT_COLOR = new Vec3(0.8f, 1.0f, 0.0f);

    public static boolean canTill(Level level, BlockPos blockPos) {
        return level.getBlockState(blockPos).is(ModTags.Blocks.TILLABLE_BLOCK_TAG) && level.getBlockState(blockPos.above()).isAir();
    }

    @Override
    public boolean shouldTryHandler(Player player, ItemStack tool) {
        return tool.getItem() instanceof HoeItem;
    }

    @Override
    public void perform(Level level, ServerPlayer player, ItemStack tool, List<BlockPos> blocks) {
        int blocksConverted = 0;

        for (BlockPos block : blocks) {
            level.setBlock(block, Blocks.FARMLAND.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            blocksConverted++;
        }

        int damagePenalty = ModConfig.DURABILITY_MODE.get().computeDamage(blocksConverted);
        player.getMainHandItem().hurtAndBreak(damagePenalty, player, (a) -> {
        });
    }

    @Override
    public boolean testNeighbor(Level level, Player player, ItemStack tool, BlockPos originPos, BlockState originBlockState, BlockPos neighborPos, BlockState neighborBlockState) {
        return canTill(level, neighborPos);
    }

    @Override
    public boolean testOrigin(Level level, Player player, ItemStack tool, BlockPos pos) {
        return canTill(level, pos);
    }

    @Override
    public Vec3 getHighlightColor() {
        return BLOCK_HIGHLIGHT_COLOR;
    }
}
