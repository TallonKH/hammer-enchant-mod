package me.radus.learningmod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static me.radus.learningmod.Registration.COMPLEX_BLOCK_ENTITY;

public class ComplexBlockEntity extends BlockEntity {
    private final static long TICKS_PER_SECOND = 20;

    public ComplexBlockEntity(BlockPos pos, BlockState state) {
        super(COMPLEX_BLOCK_ENTITY.get(), pos, state);
    }

    public void onTick(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            final long secondDelay = TICKS_PER_SECOND * 10;

            if (level.getGameTime() % secondDelay == 0) {
                final double particleSpeed = 0.15;
                final int particleCount = 10;
                serverLevel.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, particleCount, 0, 0, 0, particleSpeed);
            }

            BlockPos dropPos = pos.above();
            ItemStack droppedItem = new ItemStack(Items.AMETHYST_SHARD, 2);
            Block.popResource(level, dropPos, droppedItem);
        }
    }
}
