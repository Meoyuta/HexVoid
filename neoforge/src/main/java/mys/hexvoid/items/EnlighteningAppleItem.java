package mys.hexvoid.items;

import at.petrak.hexcasting.api.HexAPI;
import mys.hexvoid.components.HexvoidComponents;
import mys.hexvoid.damagesource.DamageSources;
import mys.hexvoid.datagen.DamageTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class EnlighteningAppleItem extends Item {
    public static final String TAG_WEAVED = "isWeaved";

    public EnlighteningAppleItem(Properties properties) {
        super(properties);
    }

    public static boolean hasWaved(@NotNull ItemStack itemStack) {
        return itemStack.getOrDefault(HexvoidComponents.WEAVED.get(), false);
    }

    public static void madeWeaved(@NotNull ItemStack itemStack) {
        itemStack.set(HexvoidComponents.WEAVED.get(), true);
    }

    private static @NotNull AdvancementHolder getAdvancement(ServerLevel serverlevel, ResourceLocation location) {
        return Objects.requireNonNull(serverlevel.getServer().getAdvancements().get(location));
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity consumer) {
        if (level instanceof ServerLevel serverlevel) {
            if (consumer instanceof ServerPlayer serverPlayer) {
                var playerAdvs = serverPlayer.getAdvancements();
                playerAdvs.award(getAdvancement(serverlevel, HexAPI.modLoc("root")), "has_charged_amethyst");
                playerAdvs.award(getAdvancement(serverlevel, HexAPI.modLoc("y_u_no_cast_angy")), "did_the_thing");
                playerAdvs.award(getAdvancement(serverlevel, HexAPI.modLoc("opened_eyes")), "health_used");
                playerAdvs.award(getAdvancement(serverlevel, HexAPI.modLoc("enlightenment")), "health_used");
            }
        }

        if (!hasWaved(stack)) {
            var mobEffect = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 140, 254, false, false, false);
            consumer.addEffect(mobEffect);

            mobEffect = new MobEffectInstance(MobEffects.JUMP, 140, 249, false, false, false);
            consumer.addEffect(mobEffect);

            mobEffect = new MobEffectInstance(MobEffects.BLINDNESS, 140, 254, false, false, false);
            consumer.addEffect(mobEffect);

            consumer.hurt(DamageSources.buildDamage(consumer, DamageTypes.OVER_KNOWLEDGE), 4.0f);
        }

        var stackReturn = stack.copy();
        stackReturn.shrink(1);
        super.finishUsingItem(stack, level, consumer);
        return stackReturn;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (hasWaved(stack))
            return Component.translatable("item.hexvoid.enlightening_apple.weaved").withStyle(ChatFormatting.GOLD);
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltips, @NotNull TooltipFlag isAdvanced) {
        if (hasWaved(stack)) {
            tooltips.add(
                    Component.translatable("tooltip.hexvoid.lightening_apple.1")
                            .withStyle(
                                    style ->
                                            style
                                                    .withColor(11767539)
                                                    .withBold(true)
                                                    .withItalic(true)
                            )
            );
            tooltips.add(
                    Component.translatable("tooltip.hexvoid.lightening_apple.2")
                            .withStyle(
                                    style -> style
                                            .withColor(ChatFormatting.GRAY)
                                            .withItalic(true)
                            )
            );
            return;
        }
        super.appendHoverText(stack, context, tooltips, isAdvanced);
    }
}
