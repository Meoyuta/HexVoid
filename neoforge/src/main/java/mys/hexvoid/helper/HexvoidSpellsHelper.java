package mys.hexvoid.helper;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import mys.hexvoid.registry.HexvoidSpellsRegistry;

public record HexvoidSpellsHelper(String name, String signature, HexDir dir, Action spellAction) {
    public static ActionRegistryEntry register(String name, String signature, HexDir dir, Action spellAction) {
        return HexvoidSpellsRegistry.wrap(name, HexPattern.fromAngleString(signature, dir), spellAction);
    }
}
