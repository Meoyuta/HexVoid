package mys.hexvoid.datagen;

import mys.hexvoid.Hexvoid;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

import java.util.ArrayList;

public class DamageTypes {
    public static ArrayList<ResKeyAndType> DamageResKeysAndTypes = new ArrayList<>();
    public static final ResourceKey<DamageType> CALL_OF_SILENT = create(
            "call_of_silent",
            "hexvoid.death.call_of_silent",
            DamageScaling.NEVER,
            0.0f
    );
    public static final ResourceKey<DamageType> CHANNEL_TO_VOID = create(
            "channel_to_void",
            "hexvoid.death.channel_to_void",
            DamageScaling.NEVER,
            0.0f
    );
    public static final ResourceKey<DamageType> OVER_KNOWLEDGE = create(
            "overknowledge",
            "hexvoid.death.overknowledge",
            DamageScaling.NEVER,
            0.0f
    );

    private static ResourceKey<DamageType> create(String damageName, String pMsgId, DamageScaling pScaling, float pExhaustion) {
        var resourceKey = ResourceKey.create(Registries.DAMAGE_TYPE, Hexvoid.ModLoc(damageName));
        var damageType = new DamageType(pMsgId, pScaling, pExhaustion);
        if (DamageResKeysAndTypes == null) DamageResKeysAndTypes = new ArrayList<>();
        DamageResKeysAndTypes.add(new ResKeyAndType(resourceKey, damageType));
        return resourceKey;
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        for (var resKeyAndTypes : DamageResKeysAndTypes) {
            context.register(resKeyAndTypes.key(), resKeyAndTypes.type());
        }
    }

    public record ResKeyAndType(ResourceKey<DamageType> key, DamageType type) {
    }
}
