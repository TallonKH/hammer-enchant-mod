package me.radus.learningmod.event;

import me.radus.learningmod.ModEnchantments;
import me.radus.learningmod.util.BlockBreakingHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HammerEvents {
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

        int enchantLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.HAMMER_ENCHANTMENT.get(), player);
        if (enchantLevel <= 0) {
            return;
        }

        currentlyMining.add(playerId);

        BlockPos origin = event.getPos();
        List<BlockPos> breakableBlocks = BlockBreakingHelper.getBreakableBlocks(player, origin);
        ServerPlayerGameMode gameMode = player.gameMode;

        for (BlockPos pos : breakableBlocks) {
            gameMode.destroyBlock(pos);
        }

        currentlyMining.remove(playerId);
        event.setCanceled(true);
    }
}
