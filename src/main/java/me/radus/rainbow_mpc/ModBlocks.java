package me.radus.rainbow_mpc;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import me.radus.rainbow_mpc.block.ComplexBlock;
import me.radus.rainbow_mpc.block.ComplexBlockEntity;
import me.radus.rainbow_mpc.block.SimpleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class ModBlocks {
    private static final Registrate REGISTRATE = RainbowMpc.registrate();

    public static final BlockEntry<SimpleBlock> SIMPLE_BLOCK = noPropBlock("simple_block", SimpleBlock::new)
            .simpleItem()
            .lang("Simple Block")
            .register();

    public static final BlockEntry<ComplexBlock> COMPLEX_BLOCK = noPropBlock("complex_block", ComplexBlock::new)
            .simpleItem()
            .blockEntity(ComplexBlockEntity::new).build()
            .lang("Complex Block")
            .register();

    public static final BlockEntityEntry<ComplexBlockEntity> COMPLEX_BLOCK_ENTITY = REGISTRATE
            .blockEntity("complex_block_entity", ComplexBlockEntity::new)
            .register();

    private static <T extends Block> BlockBuilder<T, Registrate> noPropBlock(String name, Supplier<T> factory) {
        return REGISTRATE.block(name, (BlockBehaviour.Properties props) -> factory.get());
    }
}
