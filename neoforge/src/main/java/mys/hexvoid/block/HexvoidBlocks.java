package mys.hexvoid.block;

import mys.hexvoid.Hexvoid;
import mys.hexvoid.items.HexvoidItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.function.Supplier;

public class HexvoidBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Hexvoid.MODID);

    public static ArrayList<DeferredHolder<Block, ? extends Block>> RegisteredBlocks = new ArrayList<>();

    public static final DeferredHolder<Block, Block> test_block = registerBlock("test_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK)));


    public static DeferredHolder<Block, Block> registerBlock(String name, Supplier<Block> block) {
        DeferredHolder<Block, Block> toReturn = BLOCKS.register(name, block);
        RegisteredBlocks.add(toReturn);
        HexvoidItems.RegisteredItems.add(registerBlockItem(name, toReturn));
        return toReturn;
    }

    public static DeferredHolder<Item, Item> registerBlockItem(String name, DeferredHolder<Block, Block> block) {
        return HexvoidItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
