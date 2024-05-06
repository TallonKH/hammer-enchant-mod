package com.frogedev.hammer_enchant;

import com.frogedev.hammer_enchant.enchantment.MiningShapeEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, HammerEnchantMod.MOD_ID);

    public static final RegistryObject<Enchantment> MINING_SHAPE_SURFACE_ENCHANTMENT = ENCHANTMENTS.register("mining_shape_surface", MiningShapeEnchantment.build(3));
    public static final RegistryObject<Enchantment> MINING_SHAPE_DEPTH_ENCHANTMENT = ENCHANTMENTS.register("mining_shape_depth", MiningShapeEnchantment.build(2));

    public static void register(IEventBus bus) {
        ENCHANTMENTS.register(bus);
    }
}
