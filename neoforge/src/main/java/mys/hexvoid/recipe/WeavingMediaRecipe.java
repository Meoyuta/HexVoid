package mys.hexvoid.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mys.hexvoid.registry.HexvoidRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record WeavingMediaRecipe(Ingredient input, ItemStack output) implements Recipe<RecipeInput> {
    @Override
    public boolean matches(@NotNull RecipeInput container, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput container, @NotNull HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return HexvoidRecipes.WEAVING_MEDIA_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return HexvoidRecipes.WEAVING_MEDIA_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<WeavingMediaRecipe> {
        private static final MapCodec<WeavingMediaRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(WeavingMediaRecipe::input),
                ItemStack.CODEC.fieldOf("output").forGetter(WeavingMediaRecipe::output)
        ).apply(instance, WeavingMediaRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, WeavingMediaRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, WeavingMediaRecipe::input,
                ItemStack.STREAM_CODEC, WeavingMediaRecipe::output,
                WeavingMediaRecipe::new
        );

        @Override
        public @NotNull MapCodec<WeavingMediaRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, WeavingMediaRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
