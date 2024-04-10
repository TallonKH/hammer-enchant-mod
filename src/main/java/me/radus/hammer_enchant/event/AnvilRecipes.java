package me.radus.hammer_enchant.event;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilRecipes {
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.getLeft().is(Items.END_ROD) && event.getRight().is(Items.ENCHANTED_BOOK)) {
            ItemStack inputItem = event.getLeft();
            ItemStack enchantBook = event.getRight();

            Map<Enchantment, Integer> storedEnchantments = EnchantmentHelper.getEnchantments(enchantBook);
            Map<Enchantment, Integer> itemEnchantments = EnchantmentHelper.getEnchantments(inputItem);

            if (storedEnchantments.isEmpty()) {
                return;
            }

            for (var entry : storedEnchantments.entrySet()) {
                Enchantment key = entry.getKey();
                int existingLevel = itemEnchantments.getOrDefault(key, 0);
                int storedLevel = entry.getValue();
                itemEnchantments.put(key, storedLevel + existingLevel);
            }

            ItemStack outputItem = inputItem.copy();
            EnchantmentHelper.setEnchantments(itemEnchantments, outputItem);

            event.setCost(3);
            event.setOutput(outputItem);
        }
    }
}
