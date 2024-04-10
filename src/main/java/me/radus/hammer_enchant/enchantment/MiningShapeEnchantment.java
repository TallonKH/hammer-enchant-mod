package me.radus.hammer_enchant.enchantment;

import com.tterrag.registrate.builders.EnchantmentBuilder.EnchantmentFactory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MiningShapeEnchantment extends Enchantment {
    private int maxLevel = 1;

    public MiningShapeEnchantment(Enchantment.Rarity rarity, EnchantmentCategory category, EquipmentSlot... slots) {
        super(rarity, category, slots);
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }

    public static EnchantmentFactory<MiningShapeEnchantment> build(int maxLevel) {
        return (Enchantment.Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) -> {
            var enchantment = new MiningShapeEnchantment(rarity, category, slots);
            enchantment.maxLevel = maxLevel;
            return enchantment;
        };
    }
}
