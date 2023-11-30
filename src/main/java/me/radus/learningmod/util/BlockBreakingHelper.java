package me.radus.learningmod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakingHelper {
    public static List<BlockPos> getBreakableBlocks(Player player, BlockPos origin) {
        var list = new ArrayList<BlockPos>();

        Direction facing = player.getDirection();
        Direction left = facing.getClockWise();
        Direction right = facing.getCounterClockWise();

        list.add(origin);
        list.add(origin.relative(left));
        list.add(origin.relative(right));

        return list;
    }
}
