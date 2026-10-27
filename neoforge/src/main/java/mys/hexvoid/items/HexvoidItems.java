package mys.hexvoid.items;

import mys.hexvoid.Hexvoid;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.function.Supplier;


public class HexvoidItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Hexvoid.MODID);
    
    public static ArrayList<DeferredHolder<Item, ? extends Item>> RegisteredItems = new ArrayList<>();

    public static final DeferredHolder<Item, Item> test_item = wrap("test_item", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> void_shard = wrap("void_shard", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> stable_void_shard = wrap("stable_void_shard", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> enlightening_apple = wrap("enlightening_apple", () ->
            new EnlighteningAppleItem(
                    new Item.Properties()
                            .rarity(Rarity.EPIC)
                            .food(
                                    new FoodProperties.Builder()
                                            .nutrition(1)
                                            .saturationModifier(0.0F)
                                            .build()
                            )
            )
    );
    public static final DeferredHolder<Item, Item> lore_fragment = wrap("lore_fragment", () ->
            new LoreFragmentItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .rarity(Rarity.RARE)
            )
    );

    public static final DeferredHolder<Item, Item> mind_staff = wrap("mind_staff", () ->
            new MindStaffItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .durability(1)
            )
    );

    private static DeferredHolder<Item, Item> wrap(String regName, Supplier<Item> supplier) {
        var item = ITEMS.register(regName, supplier);
        RegisteredItems.add(item);
        return item;
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
