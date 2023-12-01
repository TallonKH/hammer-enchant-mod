package me.radus.learningmod;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.EnchantmentBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import me.radus.learningmod.enchantment.HammerEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    private static final Registrate REGISTRATE = LearningMod.registrate();

    public static final RegistryEntry<HammerEnchantment> HAMMER_ENCHANTMENT = REGISTRATE
            .enchantment("hammer", EnchantmentCategory.DIGGER, HammerEnchantment::new)
            .rarity(Enchantment.Rarity.VERY_RARE)
            .addSlots(EquipmentSlot.MAINHAND)
            .lang("Hammer")
            .register();
}
