package com.frogedev.hammer_enchant.core;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class HammerActions {
    public static final MiningHammerAction MINING = new MiningHammerAction();
    public static final TillingHammerAction TILLING = new TillingHammerAction();

    @Nullable
    public static IHammerAction getHammerAction(Player player, ItemStack tool) {
        return null;
    }
}
