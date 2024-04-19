package me.radus.hammer_enchant.event;

import me.radus.hammer_enchant.Config;
import me.radus.hammer_enchant.tag.ModTags;
import me.radus.hammer_enchant.util.MiningShapeHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import static me.radus.hammer_enchant.util.MiningShapeHelpers.handleMiningShapeEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MiningShapeEvents {
    private static final Set<UUID> playersCurrentlyMining = new HashSet<>();
    private static final Set<UUID> playersCurrentlyTilling = new HashSet<>();

    public static boolean canTill(Level level, BlockPos blockPos){
        return level.getBlockState(blockPos).is(ModTags.Blocks.TILLABLE_BLOCK_TAG) && level.getBlockState(blockPos.above()).isAir();
    }

    public static boolean blockMiningPredicate(Level level, Player player, BlockPos originPos, BlockState originBlockState, BlockPos neighborPos, BlockState neighborBlockState){
        return player.hasCorrectToolForDrops(neighborBlockState) && neighborBlockState.getDestroySpeed(level, neighborPos) <= neighborBlockState.getDestroySpeed(level, originPos) + Config.MINING_SPEED_CHEAT_CAP.get();
    }

    public static class TillingHandler implements MiningShapeHelpers.MiningShapeHandler {
        public static TillingHandler INSTANCE = new TillingHandler();
        @Override
        public void perform(Level level, ServerPlayer player, ItemStack tool, Iterator<BlockPos> blocks) {
            int blocksConverted = 0;
            do {
                BlockPos pos = blocks.next();
                level.setBlock(pos, Blocks.FARMLAND.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
                blocksConverted++;
            } while (blocks.hasNext());

            int damagePenalty = Config.durabilityMode.calculate(blocksConverted);
            player.getMainHandItem().hurtAndBreak(damagePenalty, player, (a) -> {});
        }

        @Override
        public boolean testNeighbor(Level level, Player player, BlockPos originPos, BlockState originBlockState, BlockPos neighborPos, BlockState neighborBlockState) {
            return canTill(level, neighborPos);
        }

        @Override
        public boolean testOrigin(Level level, Player player, BlockPos pos) {
            return canTill(level, pos);
        }
    }

    public static class MiningHandler implements MiningShapeHelpers.MiningShapeHandler {
        public static MiningHandler INSTANCE = new MiningHandler();

        @Override
        public void perform(Level level, ServerPlayer player, ItemStack tool, Iterator<BlockPos> blocks) {
            // The damage calculation might decrease how much damage the tool takes.
            // As a precaution, temporarily set the tool to undamaged such that it doesn't break prematurely.
            int initialDamage = tool.getDamageValue();
            tool.setDamageValue(0);

            do {
                player.gameMode.destroyBlock(blocks.next());
            } while (blocks.hasNext());

            int rawDamageTaken = tool.getDamageValue();
            int damagePenalty = Config.durabilityMode.calculate(rawDamageTaken);

            tool.setDamageValue(initialDamage + damagePenalty);
        }

        @Override
        public boolean testNeighbor(Level level, Player player, BlockPos originPos, BlockState originBlockState, BlockPos neighborPos, BlockState neighborBlockState) {
            return blockMiningPredicate(level, player, originPos, originBlockState, neighborPos, neighborBlockState);
        }

        @Override
        public boolean testOrigin(Level level, Player player, BlockPos pos) {
            BlockState originBlockState = level.getBlockState(pos);
            float originDestroySpeed = originBlockState.getDestroySpeed(level, pos);

            return player.hasCorrectToolForDrops(originBlockState) && originDestroySpeed >= 0.1;
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent someEvent) {
        if (!(someEvent.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if(someEvent instanceof PlayerInteractEvent.RightClickBlock rightClickBlockEvent) {
            ItemStack tool = rightClickBlockEvent.getItemStack();

            if(player.getCooldowns().isOnCooldown(tool.getItem())){
                return;
            }

            if (tool.getItem() instanceof HoeItem) {
                handleMiningShapeEvent(
                        playersCurrentlyTilling,
                        player,
                        tool,
                        rightClickBlockEvent.getPos(),
                        rightClickBlockEvent.getHitVec(),
                        TillingHandler.INSTANCE
                );
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        handleMiningShapeEvent(
                playersCurrentlyMining,
                player,
                player.getMainHandItem(),
                event.getPos(),
                Minecraft.getInstance().hitResult,
                MiningHandler.INSTANCE
        );
    }
}