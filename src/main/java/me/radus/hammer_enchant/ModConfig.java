package me.radus.hammer_enchant;

import me.radus.hammer_enchant.config.DurabilityMode;
import me.radus.hammer_enchant.config.MiningSpeedMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HammerEnchantMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // TODO: switch to use EnumValue and get rid of other var below
    public static final ForgeConfigSpec.EnumValue<DurabilityMode> DURABILITY_MODE = BUILDER
            .comment("Durability mode [Default: 'NORMAL']")
            .defineEnum("durabilityMode", DurabilityMode.NORMAL);
    public static final ForgeConfigSpec.EnumValue<MiningSpeedMode> MINING_SPEED_MODE = BUILDER
            .comment("Mining speed mode [Default: '%s']".formatted(MiningSpeedMode.DEFAULT))
            .defineEnum("miningSpeedMode", MiningSpeedMode.DEFAULT);

    // TODO: remove
    public static final ForgeConfigSpec.ConfigValue<Float> MINING_SPEED_CHEAT_CAP = BUILDER
            .comment("Limit to how much harder a block can be than the center block. Basically, prevents mining obsidian by mining stone next to it. 0=only blocks with same or lower hardness will be mined. ")
            .define("miningSpeedCheatCap", 2.0F);
    public static final ForgeConfigSpec.ConfigValue<Float> INSTAMINE_THRESHOLD = BUILDER
            .comment("Max block hardness that is considered to be instaminable by hand. Basically, this prevents mining stone by breaking a torch next to it. Hoes are an exception and can freely mine anything under this limit (such as tall grass).")
            .define("instamineThreshold", 0.1F);

    static final ForgeConfigSpec SPEC = BUILDER.build();
}
