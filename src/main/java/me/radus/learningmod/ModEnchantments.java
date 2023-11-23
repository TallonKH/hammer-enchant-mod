package me.radus.learningmod;

import me.radus.learningmod.enchantment.HammerEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, LearningMod.MODID);

    public static final RegistryObject<Enchantment> HAMMER_ENCHANTMENT = ENCHANTMENTS.register("hammer", HammerEnchantment::new);

    public static void init(IEventBus modEventBus) {
        ENCHANTMENTS.register(modEventBus);
    }
}
