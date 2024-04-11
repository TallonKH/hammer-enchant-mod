package me.radus.hammer_enchant.event;

import me.radus.hammer_enchant.Config;
import me.radus.hammer_enchant.HammerEnchantMod;
import me.radus.hammer_enchant.util.MiningShapeHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
        Iterator<BlockPos> targetBlockPositions = MiningShapeHelpers.getBreakableBlockPositions(player, origin);
        ServerPlayerGameMode gameMode = player.gameMode;

        if (!targetBlockPositions.hasNext()) {
            return;
        }

        BlockPos pos;
        ItemStack tool = player.getMainHandItem();
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