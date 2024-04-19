package me.radus.hammer_enchant.util;

import me.radus.hammer_enchant.Config;
import me.radus.hammer_enchant.ModEnchantments;
import me.radus.hammer_enchant.tag.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiPredicate;

public class MiningShapeHelpers {


    public interface NeighborPredicate{
        public boolean test(Level level, Player player, BlockPos originPos, BlockState originBlockState, BlockPos neighborPos, BlockState neighborBlockState);
    }

    public static Iterator<BlockPos> getCandidateBlockPositions(Player player, ItemStack tool, HitResult hitResult, BlockPos origin, NeighborPredicate neighborPredicate) {
        Level level = player.level();
        BlockState originBlockState = level.getBlockState(origin);

        return new FilteredIterator<>(getAllBlockPositions(player, tool, hitResult, origin), blockPos -> {
            BlockState blockState = level.getBlockState(blockPos);
            if(player.isCrouching() && originBlockState.getBlock() != blockState.getBlock()){
                return false;
            }

            return neighborPredicate.test(level, player, origin, originBlockState, blockPos, blockState);
        });
    }

    public static Iterator<BlockPos> getAllBlockPositions(Player player, ItemStack tool, HitResult hitResult, BlockPos origin) {
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
        Vec3i selectionSize = getMiningSize(tool);
        BlockPos minCorner = origin
                .relative(heightDir, -selectionSize.getY())
                .relative(widthDir, -selectionSize.getZ());
        BlockPos maxCorner = origin
                .relative(heightDir, selectionSize.getY())
                .relative(widthDir, selectionSize.getZ())
                .relative(depthDir, selectionSize.getX());

        return BlockPos.betweenClosed(minCorner, maxCorner).iterator();
    }

    private static Vec3i getMiningSize(ItemStack itemStack) {
        int surfaceEnchantLevel = itemStack.getEnchantmentLevel(ModEnchantments.MINING_SHAPE_SURFACE_ENCHANTMENT.get());
        int depthEnchantLevel = itemStack.getEnchantmentLevel(ModEnchantments.MINING_SHAPE_DEPTH_ENCHANTMENT.get());

        int width = 0;
        int height = 0;

        if(surfaceEnchantLevel <= 4){
            width = (int) Math.ceil(surfaceEnchantLevel / 2.0);
            height = (int) Math.floor(surfaceEnchantLevel / 2.0);
        } else {
            width = surfaceEnchantLevel - 2;
            height = surfaceEnchantLevel - 2;
        }

        return new Vec3i(depthEnchantLevel, height, width);
    }
    
    public static boolean hasMiningShapeModifiers(ItemStack tool) {
        Vec3i size = getMiningSize(tool);
        return size.getX() > 0 || size.getY() > 0 || size.getZ() > 0;
    }
}
