package me.radus.hammer_enchant.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;

public class MiningShapeEnchantment extends Enchantment {
    private int maxLevel = 1;

    public MiningShapeEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }

    public static Supplier<MiningShapeEnchantment> build(int maxLevel) {
        return () -> {
            var enchantment = new MiningShapeEnchantment();
            enchantment.maxLevel = maxLevel;
            return enchantment;
        };
    }
}
