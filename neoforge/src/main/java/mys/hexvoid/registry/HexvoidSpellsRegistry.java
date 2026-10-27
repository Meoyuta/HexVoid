package mys.hexvoid.registry;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import mys.hexvoid.Hexvoid;
import mys.hexvoid.casting.action.OpCallOfSilent;
import mys.hexvoid.casting.action.OpCalledBoom;
import mys.hexvoid.casting.action.OpChannelToVoid;
import mys.hexvoid.casting.action.OpClearDraws;
import mys.hexvoid.casting.action.OpHealing;
import mys.hexvoid.casting.action.OpWeavingMedia;
import mys.hexvoid.helper.HexvoidSpellsHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class HexvoidSpellsRegistry {
    static Map<ResourceLocation, ActionRegistryEntry> CACHED = new java.util.HashMap<>();

    // Define spells
    public static final ActionRegistryEntry OP_CALLED_BOOM = HexvoidSpellsHelper.register("op_called_boom", "qqqeqqqeqqqwqqqqq", HexDir.EAST, new OpCalledBoom());
    public static final ActionRegistryEntry OP_CALL_OF_SILENT = HexvoidSpellsHelper.register("op_call_of_silent", "wwwawwwawwaadaddadaddadada", HexDir.EAST, new OpCallOfSilent());
    public static final ActionRegistryEntry OP_CHANNEL_TO_VOID = HexvoidSpellsHelper.register("channel_to_void", "edewwqwawqwqwawddewweddwaw", HexDir.NORTH_WEST, new OpChannelToVoid());
    public static final ActionRegistryEntry OP_CLEAR_DRAWS = HexvoidSpellsHelper.register("clear_draws", "dwaawaawd", HexDir.NORTH_EAST, new OpClearDraws());
    public static final ActionRegistryEntry OP_WEAVING_MEDIA = HexvoidSpellsHelper.register("weaving_media", "dwdaeeadwdaeeadwdaeeewwdwdwwdwdwwdw", HexDir.NORTH_WEST, new OpWeavingMedia());
    public static final ActionRegistryEntry OP_HEALING = HexvoidSpellsHelper.register("healing", "aqwawqaeqqwwawwqqqwawaw", HexDir.NORTH_WEST, new OpHealing());

    // "wwqwwawwqwwqwwqwwqwwqwweawwwqwwwwawwwwqwwwwdwwwwewwwwewwwwewwwdwwwwqwwwdwwewwwewwwewwewwwewewwwewewewweweewwwwdqdwwwedwwww" is that cast(?)...

    public static void registerActions() {
        for (var pair : CACHED.entrySet()) {
            Registry.register(HexActions.REGISTRY, pair.getKey(), pair.getValue());
        }
    }

    public static ActionRegistryEntry wrap(String name, HexPattern pattern, Action action) {
        var key = Hexvoid.ModLoc(name);
        var val = new ActionRegistryEntry(pattern, action);
        CACHED.put(key, val);
        return val;
    }
}