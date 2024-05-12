package com.frogedev.hammer_enchant.core;

import com.frogedev.hammer_enchant.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MiningHammerAction implements IHammerAction {
    private static final Vec3 BLOCK_HIGHLIGHT_COLOR = new Vec3(1.0f, 0.4f, 0.4f);

    @Override
    public boolean shouldTryHandler(Player player, ItemStack tool) {
        return true;
    }

    @Override
    public void perform(Level level, ServerPlayer player, ItemStack tool, List<BlockPos> blocks) {
        // The damage calculation might decrease how much damage the tool takes.
        // As a precaution, temporarily set the tool to undamaged such that it doesn't break prematurely.
        int initialDamage = tool.getDamageValue();
        tool.setDamageValue(0);

        for (BlockPos block : blocks) {
            player.gameMode.destroyBlock(block);
        }

        int rawDamageTaken = tool.getDamageValue();
        int damagePenalty = ModConfig.DURABILITY_MODE.get().computeDamage(rawDamageTaken);

        int newDamage = initialDamage + damagePenalty;
        tool.setDamageValue(newDamage);

        // Make sure tool breaks if it's supposed to.
        if (newDamage >= tool.getMaxDamage()) {
            tool.hurtAndBreak(0, player, (a) -> {
            });
        }
    }

    @Override
    public boolean testNeighbor(Level level, Player player, ItemStack tool, BlockPos originPos, BlockState originBlockState, BlockPos neighborPos, BlockState neighborBlockState) {
        float originDestroySpeed = originBlockState.getDestroySpeed(level, originPos);
        float neighborDestroySpeed = neighborBlockState.getDestroySpeed(level, neighborPos);

        if (tool.getItem() instanceof HoeItem) {
            // Allow hoe to mine any instamineable block.
            return tool.isCorrectToolForDrops(neighborBlockState) || neighborDestroySpeed <= ModConfig.INSTAMINE_THRESHOLD.get();
        } else {
            if (!tool.isCorrectToolForDrops(neighborBlockState)) {
                return false;
            }
            if (originDestroySpeed <= ModConfig.INSTAMINE_THRESHOLD.get()) {
                // If origin is instamined, only mine other instamineable blocks.
                return neighborDestroySpeed <= ModConfig.INSTAMINE_THRESHOLD.get();
            } else {
                // If origin is not instamined, only mine blocks with destroy speed within cheat limit.
                return neighborDestroySpeed <= originDestroySpeed + ModConfig.MINING_SPEED_CHEAT_CAP.get();
            }
        }
    }

    @Override
    public boolean testOrigin(Level level, Player player, ItemStack tool, BlockPos pos) {
        BlockState originBlockState = level.getBlockState(pos);

        Item toolItem = tool.getItem();

        if (toolItem instanceof HoeItem) {
            // Allow hoe to mine any instamineable block.
            return toolItem.isCorrectToolForDrops(originBlockState) || originBlockState.getDestroySpeed(level, pos) <= ModConfig.INSTAMINE_THRESHOLD.get();
        } else {
            return toolItem.isCorrectToolForDrops(originBlockState);
        }
    }

    @Override
    public Vec3 getHighlightColor() {
        return BLOCK_HIGHLIGHT_COLOR;
    }
}
