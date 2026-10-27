package mys.hexvoid.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

public class GlobalLootModifierProvider extends net.neoforged.neoforge.common.data.GlobalLootModifierProvider {
    public GlobalLootModifierProvider(PackOutput output, java.util.concurrent.CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookupProvider, String modid) {
        super(output, lookupProvider, modid);
    }

    @Override
    protected void start() {
        this.add(
                "lootable_woodland_mansion",
                new LootTableModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                                LootItemRandomChanceCondition.randomChance(0.10f).build() // 10% 概率
                        }
                )
        );
        this.add(
                "lootable_nether_bridge",
                new LootTableModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                                LootItemRandomChanceCondition.randomChance(0.15f).build() // 15% 概率
                        }
                )
        );
        this.add(
                "lootable_desert_pyramid",
                new LootTableModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/desert_pyramid")).build(),
                                LootItemRandomChanceCondition.randomChance(0.01f).build() // 1% 概率
                        }
                )
        );
        this.add(
                "lootable_end_city_treasure",
                new LootTableModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.25f).build() // 25% 概率
                        }
                )
        );
        this.add(
                "lootable_stronghold_library",
                new LootTableModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/stronghold_library")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build() // 5% 概率
                        }
                )
        );
        this.add(
                "lootable_buried_treasure",
                new LootTableModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/buried_treasure")).build(),
                                LootItemRandomChanceCondition.randomChance(0.001f).build() // 0.1% 概率
                        }
                )
        );
        this.add(
                "lootable_igloo_chest",
                new LootTableModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/igloo_chest")).build(),
                                LootItemRandomChanceCondition.randomChance(0.001f).build() // 0.1% 概率
                        }
                )
        );
        this.add(
                "lootable_ancient_city",
                new LootTableModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build(),
                                LootItemRandomChanceCondition.randomChance(0.10f).build() // 10% 概率
                        }
                )
        );
        this.add(
                "lootable_ancient_city_ice_box",
                new LootTableModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/ancient_city_ice_box")).build(),
                                LootItemRandomChanceCondition.randomChance(0.05f).build() // 5% 概率
                        }
                )
        );
        this.add(
                "lootable_simple_dungeon",
                new LootTableModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                                LootItemRandomChanceCondition.randomChance(0.001f).build() // 0.1% 概率
                        }
                )
        );
    }

}
