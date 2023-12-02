package me.radus.learningmod.util;

import com.google.common.collect.AbstractIterator;
import me.radus.learningmod.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class MiningShapeHelpers {
    public static boolean hasMiningShapeModifiers(Player player) {
        Vec3i size = getMiningSize(player);
        return size.getX() > 0 || size.getY() > 0 || size.getZ() > 0;
    }

    public static Iterator<BlockPos> getBreakableBlocks(Player player, BlockPos origin) {
        final Vec3 upEstimateDir = new Vec3(0.0, 1.0, 0.0);

        Vec3 lookDir = origin.getCenter().subtract(player.getEyePosition()).normalize();
        Vec3 rightDir = lookDir.cross(upEstimateDir).normalize();
        Vec3 upDir = rightDir.cross(lookDir).normalize();

        Direction forward = Direction.getNearest(lookDir.x(), lookDir.y(), lookDir.z());
        Direction right = Direction.getNearest(rightDir.x(), rightDir.y(), rightDir.z());
        Direction up = Direction.getNearest(upDir.x(), upDir.y(), upDir.z());

        Vec3i size = getMiningSize(player);

        BlockPos nearBottomLeft = origin
                .relative(right, -size.getX())
                .relative(up, -size.getY());
        BlockPos farTopRight = origin
                .relative(right, size.getX())
                .relative(up, size.getY())
                .relative(forward, size.getZ());

        Iterator<BlockPos> rawBlocks = BlockPos.betweenClosed(nearBottomLeft, farTopRight).iterator();
        if (!player.isCrouching()) {
            return rawBlocks;
        }

        Level level = player.level();
        BlockState originBlockState = level.getBlockState(origin);

        return new FilteredIterator<>(rawBlocks, (BlockPos pos) ->
                level.getBlockState(pos) == originBlockState
        );
    }

    private static Vec3i getMiningSize(Player player) {
        int surfaceEnchantLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.MINING_SHAPE_SURFACE_ENCHANTMENT.get(), player);
        int depthEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.MINING_SHAPE_DEPTH_ENCHANTMENT.get(), player);

        int horizontal = 0;
        int vertical = 0;
        int depth = 0;

        switch (surfaceEnchantLevel) {
            case 1 -> {
                horizontal = 1;
                vertical = 0;
            }
            case 2 -> {
                horizontal = 1;
                vertical = 1;
            }
            case 3 -> {
                horizontal = 2;
                vertical = 1;
            }
        }

        switch (depthEnchantmentLevel) {
            case 1 -> {
                depth = 1;
            }
            case 2 -> {
                depth = 2;
            }
        }

        return new Vec3i(horizontal, vertical, depth);
    }
}
