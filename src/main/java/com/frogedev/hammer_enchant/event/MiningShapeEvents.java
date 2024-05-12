package com.frogedev.hammer_enchant.event;

import com.frogedev.hammer_enchant.ModConfig;
import com.frogedev.hammer_enchant.core.HammerActions;
import com.frogedev.hammer_enchant.core.IHammerAction;
import com.frogedev.hammer_enchant.tag.ModTags;
import com.frogedev.hammer_enchant.core.MiningShapeHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MiningShapeEvents {
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent someEvent) {
        if (!(someEvent.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (someEvent instanceof PlayerInteractEvent.RightClickBlock rightClickBlockEvent) {
            ItemStack tool = rightClickBlockEvent.getItemStack();

            if (player.getCooldowns().isOnCooldown(tool.getItem())) {
                return;
            }

            if (HammerActions.TILLING.shouldTryHandler(player, tool)) {
                if (MiningShapeHelpers.handleMiningShapeEvent(
                        player,
                        tool,
                        rightClickBlockEvent.getPos(),
                        rightClickBlockEvent.getHitVec(),
                        HammerActions.TILLING
                )) {
                    rightClickBlockEvent.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        if (MiningShapeHelpers.handleMiningShapeEvent(
                player,
                player.getMainHandItem(),
                event.getPos(),
                // Server-side raycast. For client, use Minecraft.instance.hitResult.
                event.getPlayer().pick(event.getPlayer().getBlockReach(), 0F, false),
                HammerActions.MINING
        )) {
            event.setCanceled(true);
        }
    }

    // Mining speed (s) is basically everything *but* block hardness (h).
    // Let (t) be time to break.
    //  normally: t=h/s
    //  we want: t' = f(h1,h2,...)/s
    //  we can only change (s), so we do: t' = h/s'
    //      f(h1,h2,...})/s = h/s'
    //      s' = s*h / f(h1,h2,...)
    @SubscribeEvent
    public static void onBlockBreakStart(PlayerEvent.BreakSpeed event) {
        if (event.getPosition().isEmpty()) {
            return;
        }

        Player player = event.getEntity();
        BlockPos breakPos = event.getPosition().get();

        ItemStack tool = player.getMainHandItem();
        if (!MiningShapeHelpers.hasMiningShapeModifiers(tool)) {
            return;
        }

        Level level = player.level();
        Iterator<BlockPos> blockPosIter = MiningShapeHelpers.getCandidateBlockPositions(
                player,
                tool,
                player.pick(player.getBlockReach(), 0F, false),
                breakPos,
                HammerActions.MINING
        );

        List<Float> allDestroyTimes = new ArrayList<>();
        if (blockPosIter.hasNext()) {
            while (blockPosIter.hasNext()) {
                BlockPos blockPos = blockPosIter.next();
                BlockState blockState = level.getBlockState(blockPos);
                allDestroyTimes.add(blockState.getBlock().defaultDestroyTime());
            }

            float centerDestroyTime = level.getBlockState(breakPos).getBlock().defaultDestroyTime();
            float totalDestroyTime = ModConfig.MINING_SPEED_MODE.get().computeDestroyTime(centerDestroyTime, allDestroyTimes);
            float newSpeed = event.getOriginalSpeed() * centerDestroyTime / totalDestroyTime;
            event.setNewSpeed(newSpeed);
        }
    }
}
