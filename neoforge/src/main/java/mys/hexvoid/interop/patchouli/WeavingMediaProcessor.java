package mys.hexvoid.interop.patchouli;

import mys.hexvoid.Hexvoid;
import mys.hexvoid.recipe.WeavingMediaRecipe;
import mys.hexvoid.registry.HexvoidRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

@SuppressWarnings("unused")
public class WeavingMediaProcessor implements IComponentProcessor {
    private WeavingMediaRecipe recipe;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        var id = ResourceLocation.tryParse(variables.get("recipe", level.registryAccess()).asString());

        if (id == null) throw new NullPointerException("WeavingMedia recipe was not resolved in setup()");

        if (Hexvoid.IS_DEBUG_MODE) {
            var querier = level.getRecipeManager().getAllRecipesFor(HexvoidRecipes.WEAVING_MEDIA_TYPE.get());
            querier.forEach((T) -> Hexvoid.LOGGER.debug("Found WeavingRecipe :{}", T.id()));
        }

        var holder = level.getRecipeManager().byKey(id).orElse(null);
        if (holder != null && holder.value() instanceof WeavingMediaRecipe weaving) recipe = weaving;
        else recipe = null;

        if (recipe == null) {
            System.out.println("Patchouli: WeavingMedia recipe not found: " + id);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull IVariable process(Level level, String key) {
        if (recipe == null) throw new NullPointerException();
        return switch (key) {
            case "header" -> IVariable.wrap(recipe.output().getHoverName().getString(), level.registryAccess());
            case "input" -> {
                var input = recipe.input().getItems();
                if (input.length > 0) {
                    yield IVariable.from(input[0], level.registryAccess());
                } else {
                    yield IVariable.from(Items.BARRIER.getDefaultInstance(), level.registryAccess());
                }
            }
            case "output" -> IVariable.from(recipe.getResultItem(level.registryAccess()), level.registryAccess());
            case "op_id" -> IVariable.wrap("hexvoid:weaving_media", level.registryAccess());
            case "hex_size" -> IVariable.wrap(0.8, level.registryAccess());
            case "text" -> null;
            default -> throw new IllegalArgumentException();
        };
    }
}
