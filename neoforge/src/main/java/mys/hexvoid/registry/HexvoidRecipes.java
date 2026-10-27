package mys.hexvoid.registry;

import mys.hexvoid.Hexvoid;
import mys.hexvoid.recipe.WeavingMediaRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings({"SameParameterValue", "unused"})
public class HexvoidRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Hexvoid.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Hexvoid.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<WeavingMediaRecipe>> WEAVING_MEDIA_TYPE =
            TYPES.register("weaving_media", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Hexvoid.MODID + ":weaving_media";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WeavingMediaRecipe>> WEAVING_MEDIA_SERIALIZER =
            SERIALIZERS.register("weaving_media", WeavingMediaRecipe.Serializer::new);

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        SERIALIZERS.register(bus);
    }
}
