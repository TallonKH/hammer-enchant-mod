package me.radus.learningmod.event;

import me.radus.learningmod.ModEnchantments;
import me.radus.learningmod.util.MiningShapeHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MiningShapeEvents {
    private static final Set<UUID> currentlyMining = new HashSet<>();

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        UUID playerId = player.getUUID();
        if (currentlyMining.contains(playerId)) {
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

        currentlyMining.add(playerId);
        BlockPos pos;

        do {
            pos = breakableBlocks.next();
            gameMode.destroyBlock(pos);
        } while (breakableBlocks.hasNext());

        currentlyMining.remove(playerId);
        event.setCanceled(true);
    }
}
