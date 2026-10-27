package mys.hexvoid.datagen;

import mys.hexvoid.Hexvoid;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;


public class DamageTypeTagsProvider extends TagsProvider<DamageType> {

    protected DamageTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, Registries.DAMAGE_TYPE, future, Hexvoid.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        var damageTags = new ArrayList<TagKey<DamageType>>();
        damageTags.add(DamageTypeTags.BYPASSES_ARMOR);
        damageTags.add(DamageTypeTags.BYPASSES_ENCHANTMENTS);
        damageTags.add(DamageTypeTags.BYPASSES_EFFECTS);
        damageTags.add(DamageTypeTags.BYPASSES_RESISTANCE);
        damageTags.add(DamageTypeTags.BYPASSES_COOLDOWN);
        damageTags.add(DamageTypeTags.BYPASSES_INVULNERABILITY);
        tag(DamageTypes.CALL_OF_SILENT, damageTags);
        tag(DamageTypes.CHANNEL_TO_VOID, damageTags);
        tag(DamageTypes.OVER_KNOWLEDGE, damageTags);
    }

    private void tag(ResourceKey<DamageType> type, ArrayList<TagKey<DamageType>> tags) {
        for (TagKey<DamageType> key : tags) {
            tag(key).add(type);
        }
    }
}
