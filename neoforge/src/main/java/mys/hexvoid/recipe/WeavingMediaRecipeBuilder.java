package mys.hexvoid.recipe;

import mys.hexvoid.registry.HexvoidRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.data.recipes.RecipeOutput;

public class WeavingMediaRecipeBuilder {
    private final Ingredient input;
    private final ItemStack output;

    private WeavingMediaRecipeBuilder(Ingredient input, ItemStack output) {
        this.input = input;
        this.output = output.copy();
    }

    public static WeavingMediaRecipeBuilder weavingMedia(Ingredient input, ItemStack output) {
        return new WeavingMediaRecipeBuilder(input, output);
    }

    public void save(RecipeOutput writer, ResourceLocation id) {
        writer.accept(id, new WeavingMediaRecipe(input, output), null);
    }
}
