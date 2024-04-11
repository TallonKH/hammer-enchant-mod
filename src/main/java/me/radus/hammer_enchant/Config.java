package me.radus.hammer_enchant;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = HammerEnchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<String> DURABILITY_MODE = BUILDER
            .comment("Durability mode [NORMAL/SQRT]")
            .define("durabilityMode", "NORMAL");
    public static final ForgeConfigSpec.ConfigValue<Float> MINING_SPEED_CHEAT_CAP = BUILDER
            .comment("Limit to how much harder a block can be than the center block. Basically, prevents mining obsidian by mining stone next to it. 0=only blocks with same or lower hardness will be mined. ")
            .define("miningSpeedCheatCap", 2.0F);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static DurabilityMode durabilityMode;
    public static float miningSpeedCheatCap;

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        durabilityMode = DurabilityMode.fromString(DURABILITY_MODE.get());
        miningSpeedCheatCap = MINING_SPEED_CHEAT_CAP.get();
    }
}
