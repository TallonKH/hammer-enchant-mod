package me.radus.rainbow_mpc.event;

import me.radus.rainbow_mpc.util.MiningShapeHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MiningShapeEvents {
    private static final Set<UUID> playersCurrentlyMining = new HashSet<>();

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        UUID playerId = player.getUUID();
        if (playersCurrentlyMining.contains(playerId)) {
            return;
        }

        if (!MiningShapeHelpers.hasMiningShapeModifiers(player)) {
            return;
        }

        BlockPos origin = event.getPos();
        Iterator<BlockPos> breakableBlocks = MiningShapeHelpers.getBreakableBlocks(player, origin);
        ServerPlayerGameMode gameMode = player.gameMode;

        if (!breakableBlocks.hasNext()) {
            return;
        }

        BlockPos pos;
        Level level = player.level();
        playersCurrentlyMining.add(playerId);

        do {
            pos = breakableBlocks.next();
            if (canBreakBlock(level, pos, player)) {
                gameMode.destroyBlock(pos);
            }
        } while (breakableBlocks.hasNext());

        playersCurrentlyMining.remove(playerId);
        event.setCanceled(true);
    }

    private static boolean canBreakBlock(Level level, BlockPos pos, Player player) {
        if (!ForgeHooks.canEntityDestroy(level, pos, player)) {
            return false;
        }

        BlockState state = level.getBlockState(pos);
        return state.getDestroySpeed(level, pos) > 0.0f;
    }
}
