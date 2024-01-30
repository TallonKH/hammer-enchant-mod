package me.radus.rainbow_mpc.util;

import me.radus.rainbow_mpc.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.Iterator;

public class MiningShapeHelpers {

    public static boolean hasMiningShapeModifiers(Player player) {
        Vec3i size = getMiningSize(player);
        return size.getX() > 0 || size.getY() > 0 || size.getZ() > 0;
    }

    public static Iterator<BlockPos> getBreakableBlocks(Player player, BlockPos origin) {
        HitResult hitResult = Minecraft.getInstance().hitResult;

        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return Collections.emptyIterator();
        }

        BlockHitResult blockHitResult = (BlockHitResult) hitResult;

        boolean facingEastWest = Math.floorMod(Math.round(player.getYRot() + 45), 180) > 90;

        // Get the world axes that correspond to the mining shape's depth/width/height.
        Direction depthDir = blockHitResult.getDirection().getOpposite();
        Direction heightDir;
        Direction widthDir;
        if (depthDir.getAxis().isVertical()){
            if (facingEastWest) {
                heightDir = Direction.EAST;
                widthDir = Direction.SOUTH;
            } else {
                heightDir = Direction.SOUTH;
                widthDir = Direction.EAST;
            }
        } else {
            heightDir = Direction.UP;
            widthDir = depthDir.getClockWise();
        }

        // Get the corners of the mining shape.
        Vec3i selectionSize = getMiningSize(player);
        BlockPos minCorner = origin
                .relative(heightDir, -selectionSize.getY())
                .relative(widthDir, -selectionSize.getZ());
        BlockPos maxCorner = origin
                .relative(heightDir, selectionSize.getY())
                .relative(widthDir, selectionSize.getZ())
                .relative(depthDir, selectionSize.getX());

        Iterator<BlockPos> rawBlocks = BlockPos.betweenClosed(minCorner, maxCorner).iterator();
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
