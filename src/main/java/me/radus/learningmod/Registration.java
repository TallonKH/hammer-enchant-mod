package me.radus.learningmod;

import me.radus.learningmod.blocks.ComplexBlock;
import me.radus.learningmod.blocks.ComplexBlockEntity;
import me.radus.learningmod.blocks.SimpleBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class Registration {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LearningMod.MODID);
    public static final RegistryObject<Block> SIMPLE_BLOCK = BLOCKS.register("simple_block", SimpleBlock::new);
    public static final RegistryObject<Block> COMPLEX_BLOCK = BLOCKS.register("complex_block", ComplexBlock::new);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, LearningMod.MODID);
    public static final RegistryObject<BlockEntityType<ComplexBlockEntity>> COMPLEX_BLOCK_ENTITY = BLOCK_ENTITIES.register("complex_block",
            () -> BlockEntityType.Builder.of(ComplexBlockEntity::new, COMPLEX_BLOCK.get()).build(null));

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LearningMod.MODID);
    public static final RegistryObject<Item> SIMPLE_BLOCK_ITEM = ITEMS.register("simple_block", () -> new BlockItem(SIMPLE_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> COMPLEX_BLOCK_ITEM = ITEMS.register("complex_block", () -> new BlockItem(COMPLEX_BLOCK.get(), new Item.Properties()));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LearningMod.MODID);

    public static final RegistryObject<CreativeModeTab> LEARNING_TAB = CREATIVE_MODE_TABS.register("learning", () -> CreativeModeTab.builder()
            .title(Component.literal("Learning!!"))
            .icon(Items.BOOKSHELF::getDefaultInstance)
            .build());

    public static void init(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(Registration::onBuildCreativeTabContents);

        MinecraftForge.EVENT_BUS.addListener(Registration::onAnvilUpdate);
        MinecraftForge.EVENT_BUS.addListener(Registration::onBlockBreak);
    }

    private static void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == LEARNING_TAB.get()) {
            event.accept(SIMPLE_BLOCK_ITEM);
            event.accept(COMPLEX_BLOCK_ITEM);
        }
    }

    private static void onAnvilUpdate(AnvilUpdateEvent event) {
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

    private static void onPlayerTick(TickEvent.PlayerTickEvent event) {

    }

    private static void onBlockBreak(BlockEvent.BreakEvent event) {

    }
}
