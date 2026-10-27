package mys.hexvoid.item.tags;

import mys.hexvoid.Hexvoid;
import net.minecraft.resources.ResourceLocation;

public class HexvoidTags {
    public static final String TAG_PATTERN_CLEARED = Hexvoid.ModLoc("pattern_cleared").toString();

    // advancements criterion
    public static final String ADV_CRITERION_GRANT = "grant";
    public static final String ADV_CRITERION_TRY_CHANNEL = "try_channel";
    public static final String ADV_CRITERION_FIND_BROKEN = "find_broken";
    public static final String ADV_CRITERION_EXCHANGE_LIFE = "exchange_life";

    // advancements
    public static final ResourceLocation ADV_LORE_RESEARCH = Hexvoid.ModLoc("lore/research");
    public static final ResourceLocation ADV_DEEPWORLD = Hexvoid.ModLoc("research");
    public static final ResourceLocation ADV_FIND_BROKEN = Hexvoid.ModLoc("findbroken");
    public static final ResourceLocation ADV_EXCHANGE_LIFE = Hexvoid.ModLoc("exchangelife");

    // recipe
    public static final ResourceLocation RECIPE_WEVING_MIND_STAFF = Hexvoid.ModLoc("weaving_media/mind_staff");
    public static final ResourceLocation RECIPE_WEVING_ENLIGHTENING_APPLE = Hexvoid.ModLoc("weaving_media/enlightening_apple");
}
