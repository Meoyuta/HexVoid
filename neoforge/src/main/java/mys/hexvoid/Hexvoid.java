package mys.hexvoid;

import at.petrak.hexcasting.common.lib.HexCreativeTabs;
import at.petrak.hexcasting.common.lib.HexRegistries;
import com.mojang.logging.LogUtils;
import mys.hexvoid.block.HexvoidBlocks;
import mys.hexvoid.components.HexvoidComponents;
import mys.hexvoid.datagen.registry.LootModifiersRegistry;
import mys.hexvoid.items.HexvoidItems;
import mys.hexvoid.registry.HexvoidRecipes;
import mys.hexvoid.registry.HexvoidSpellsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(Hexvoid.MODID)
public class Hexvoid {
    public static final String MODID = "hexvoid";
    public static final Logger LOGGER = LogUtils.getLogger();
    /**
     * if this const is <code>true</code>, the debug part will enable.
     * <p>
     * the default value is <code>false</code>
     */
    public static boolean IS_DEBUG_MODE = false;

    public Hexvoid(IEventBus modEventBus) {

        HexvoidComponents.register(modEventBus);
        HexvoidItems.register(modEventBus);
        HexvoidBlocks.register(modEventBus);
        HexvoidRecipes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::onRegister);

        // this registry is from datagen part
        LootModifiersRegistry.register(modEventBus);
    }

    public static void debug() {
        IS_DEBUG_MODE = !IS_DEBUG_MODE;
    }

    public static ResourceLocation ModLoc(String path) {
        var patchedPath = path.toLowerCase();
        return ResourceLocation.fromNamespaceAndPath(MODID, patchedPath);
    }

    public void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(HexRegistries.ACTION)) {
            // We register our spells
            HexvoidSpellsRegistry.registerActions();
            LOGGER.info("Hexcasting is in the voooooooid!");
            LOGGER.info("hexvoid has registered patterns.");
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(HexCreativeTabs.HEX_KEY)) {
            // items
            if (!HexvoidItems.RegisteredItems.isEmpty()) {
                for (var item : HexvoidItems.RegisteredItems) event.accept(item.get());
            } else {
                throw new IllegalArgumentException();
            }
            // block items
            // merge to 'items' register
            //event.accept(HexvoidBlocks.test_block.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // we do nothing when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @net.neoforged.fml.common.EventBusSubscriber(modid = MODID, bus = net.neoforged.fml.common.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // we have no client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
