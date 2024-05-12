package com.frogedev.hammer_enchant.core;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface IHammerAction {
    boolean shouldTryHandler(Player player, ItemStack tool);

    void perform(Level level, ServerPlayer player, ItemStack tool, List<BlockPos> blocks);

    boolean testOrigin(Level level, Player player, ItemStack tool, BlockPos pos);

    boolean testNeighbor(Level level, Player player, ItemStack tool, BlockPos originPos, BlockState originBlockState, BlockPos neighborPos, BlockState neighborBlockState);

    Vec3 getHighlightColor();
}