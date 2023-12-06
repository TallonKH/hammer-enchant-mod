package me.radus.rainbow_mpc;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import me.radus.rainbow_mpc.enchantment.MiningShapeEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ModEnchantments {
    private static final Registrate REGISTRATE = RainbowMpc.registrate();

    public static final RegistryEntry<MiningShapeEnchantment> MINING_SHAPE_SURFACE_ENCHANTMENT = REGISTRATE
            .enchantment("mining_shape_surface", EnchantmentCategory.DIGGER, MiningShapeEnchantment.build(3))
            .rarity(Enchantment.Rarity.VERY_RARE)
            .addSlots(EquipmentSlot.MAINHAND)
            .lang("Mining Shape Surface")
            .register();

    public static final RegistryEntry<MiningShapeEnchantment> MINING_SHAPE_DEPTH_ENCHANTMENT = REGISTRATE
            .enchantment("mining_shape_depth", EnchantmentCategory.DIGGER, MiningShapeEnchantment.build(2))
            .rarity(Enchantment.Rarity.VERY_RARE)
            .addSlots(EquipmentSlot.MAINHAND)
            .lang("Mining Shape Depth")
            .register();
}
