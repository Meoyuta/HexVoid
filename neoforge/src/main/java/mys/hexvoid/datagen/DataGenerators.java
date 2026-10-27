package mys.hexvoid.datagen;

import mys.hexvoid.Hexvoid;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = Hexvoid.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var datapackProvider = new RegistryDataProvider(output, event.getLookupProvider());
        var lookupProvider = datapackProvider.getRegistryProvider();
        var existingFileHelper = event.getExistingFileHelper();

        // datapack registry things
        generator.addProvider(event.includeServer(), datapackProvider);

        // damage type tags
        generator.addProvider(event.includeServer(), new DamageTypeTagsProvider(
                output,
                lookupProvider,
                existingFileHelper
        ));

        // loot tables
        generator.addProvider(event.includeServer(), new GlobalLootModifierProvider(
                output,
                event.getLookupProvider(),
                Hexvoid.MODID
        ));

        // recipes
        generator.addProvider(event.includeServer(), new HvRecipeProvider(output, event.getLookupProvider()));
    }
}
