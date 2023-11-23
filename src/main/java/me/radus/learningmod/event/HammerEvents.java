package me.radus.learningmod.event;

import me.radus.learningmod.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

        BlockPos pos = event.getPos();
        Direction facing = player.getDirection();
        Direction left = facing.getClockWise();
        Direction right = facing.getCounterClockWise();

        player.gameMode.destroyBlock(pos);
        player.gameMode.destroyBlock(pos.relative(left));
        player.gameMode.destroyBlock(pos.relative(right));

        currentlyMining.remove(playerId);
        event.setCanceled(true);
    }
}
