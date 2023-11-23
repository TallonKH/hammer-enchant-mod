package me.radus.learningmod.blocks;

import me.radus.learningmod.LearningMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SimpleBlock extends Block {
    public SimpleBlock() {
        super(Properties.of()
                .strength(3.5f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL)
                .randomTicks()
        );
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        final double particleSpeed = 0.15;
        final int particleCount = 10;
        level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, particleCount, 0, 0, 0, particleSpeed);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        LearningMod.LOGGER.debug("used!!!!!");
        if (level.isClientSide()) {
            final float soundVolume = 1.0f;
            final float soundPitch = 1.0f;
            level.playLocalSound(pos, SoundEvents.ANVIL_FALL, SoundSource.BLOCKS, soundVolume, soundPitch, false);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
