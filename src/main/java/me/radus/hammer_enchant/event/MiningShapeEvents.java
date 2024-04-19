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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MiningShapeEvents {
    private static final Set<UUID> playersCurrentlyMining = new HashSet<>();
    private static final Set<UUID> playersCurrentlyTilling = new HashSet<>();

    public static boolean blockTillingPredicate(Level level, Player player, BlockPos originPos, BlockState originBlockState, BlockPos neighborPos, BlockState neighborBlockState){
        return neighborBlockState.is(ModTags.Blocks.TILLABLE_BLOCK_TAG) && level.getBlockState(neighborPos.above()).isAir();
    }

    public static boolean blockMiningPredicate(Level level, Player player, BlockPos originPos, BlockState originBlockState, BlockPos neighborPos, BlockState neighborBlockState){
        return player.hasCorrectToolForDrops(neighborBlockState) && neighborBlockState.getDestroySpeed(level, neighborPos) <= neighborBlockState.getDestroySpeed(level, originPos) + Config.MINING_SPEED_CHEAT_CAP.get();
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent someEvent) {
        if (!(someEvent.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if(someEvent instanceof PlayerInteractEvent.RightClickBlock rightClickBlockEvent) {
            ItemStack tool = rightClickBlockEvent.getItemStack();
            if (tool.getItem() instanceof HoeItem) {
                UUID playerId = player.getUUID();
                if (playersCurrentlyTilling.contains(playerId)) {
                    return;
                }

                if (!MiningShapeHelpers.hasMiningShapeModifiers(tool)) {
                    return;
                }

                Level level = rightClickBlockEvent.getLevel();
                BlockPos origin = rightClickBlockEvent.getPos();
                BlockState originBlockState = level.getBlockState(origin);

                if (!(originBlockState.is(ModTags.Blocks.TILLABLE_BLOCK_TAG) && level.getBlockState(origin.above()).isAir())) {
                    return;
                }

                Iterator<BlockPos> targetBlockPositions = MiningShapeHelpers.getCandidateBlockPositions(
                        player,
                        tool,
                        rightClickBlockEvent.getHitVec(),
                        origin,
                        MiningShapeEvents::blockTillingPredicate
                );

                if (!targetBlockPositions.hasNext()) {
                    return;
                }

                BlockPos pos;
                playersCurrentlyTilling.add(playerId);

                int blocksConverted = 0;
                do {
                    pos = targetBlockPositions.next();
                    someEvent.getLevel().setBlock(pos, Blocks.FARMLAND.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
                    blocksConverted++;
                } while (targetBlockPositions.hasNext());

                int damagePenalty = Config.durabilityMode.calculate(blocksConverted);
                player.getMainHandItem().hurtAndBreak(damagePenalty, player, (a) -> {
                });

                playersCurrentlyTilling.remove(playerId);
                rightClickBlockEvent.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        UUID playerId = player.getUUID();
        if (playersCurrentlyMining.contains(playerId)) {
            return;
        }

        ItemStack tool = player.getMainHandItem();

        if (!MiningShapeHelpers.hasMiningShapeModifiers(tool)) {
            return;
        }

        BlockPos origin = event.getPos();
        Level level = player.level();
        BlockState originBlockState = level.getBlockState(origin);
        float originDestroySpeed = originBlockState.getDestroySpeed(level, origin);

        if(!player.hasCorrectToolForDrops(originBlockState) || originDestroySpeed <= 0.1){
            return;
        }

        Iterator<BlockPos> targetBlockPositions = MiningShapeHelpers.getCandidateBlockPositions(
                player,
                tool,
                Minecraft.getInstance().hitResult,
                origin,
                MiningShapeEvents::blockMiningPredicate
        );
        ServerPlayerGameMode gameMode = player.gameMode;

        if (!targetBlockPositions.hasNext()) {
            return;
        }

        BlockPos pos;
        playersCurrentlyMining.add(playerId);

        int initialDamage = tool.getDamageValue();
        // The damage calculation might decrease how much damage the tool takes.
        // As a precaution, temporarily set the tool to undamaged such that it doesn't break prematurely.
        tool.setDamageValue(0);

        do {
            pos = targetBlockPositions.next();
            gameMode.destroyBlock(pos);
        } while (targetBlockPositions.hasNext());

        int rawDamageTaken = tool.getDamageValue();
        int damagePenalty = Config.durabilityMode.calculate(rawDamageTaken);

        tool.setDamageValue(initialDamage + damagePenalty);

        playersCurrentlyMining.remove(playerId);
        event.setCanceled(true);
    }
}