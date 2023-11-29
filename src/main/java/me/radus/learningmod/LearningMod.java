package me.radus.learningmod;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LearningMod.MOD_ID)
public class LearningMod {
    public static final String MOD_ID = "learningmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final NonNullLazy<Registrate> REGISTRATE = NonNullLazy.of(
            () -> Registrate.create(MOD_ID)
                    .defaultCreativeTab("learning_tab", builder -> builder
                            .title(Component.literal("Learning!!"))
                            .icon(Items.BOOKSHELF::getDefaultInstance)).build()

    );

    @NotNull
    public static Registrate registrate() {
        return REGISTRATE.get();
    }

    public LearningMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Prevent JVM from optimizing away these classes...
        new ModBlocks();
        new ModEnchantments();

        modEventBus.addListener(this::onModCommonSetup);

        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void onModCommonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock) {
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
        }

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
