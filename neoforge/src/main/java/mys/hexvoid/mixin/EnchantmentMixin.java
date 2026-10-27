package mys.hexvoid.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A mixin to fix an issue where the Enchantment levels shows an exception when the level is large
 */
@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Unique
    @NotNull
    private static String getNumerals(int number, int[] values, String[] numerals) {
        StringBuilder out = new StringBuilder();
        int n = number;

        for (int i = 0; i < values.length; i++) {
            while (n >= values[i]) {
                out.append(numerals[i]);
                n -= values[i];
            }
        }
        return out.toString();
    }

    @Unique
    private static String toRomanCompressed(int number) {
        StringBuilder out = new StringBuilder();

        // process 'M' on 1k
        int thousands = number / 1000;
        int remainder = number % 1000;

        if (thousands > 0) {
            if (thousands > 5) {
                out.append("M(").append(thousands).append(")");
            } else {
                out.repeat("M", thousands);
            }
        }

        // process 0-999 part
        out.append(toRomanUnder1k(remainder));

        return out.toString();
    }

    @Unique
    private static String toRomanUnder1k(int number) {
        // we make the 'M' repeatable so that it meets the upper limit of the level
        int[] values = {
                900, 500, 400,
                100, 90, 50, 40,
                10, 9, 5, 4, 1
        };

        String[] numerals = {
                "CM", "D", "CD",
                "C", "XC", "L", "XL",
                "X", "IX", "V", "IV", "I"
        };

        return getNumerals(number, values, numerals);
    }

    @Inject(method = "getFullname(Lnet/minecraft/core/Holder;I)Lnet/minecraft/network/chat/Component;", at = @At("HEAD"), cancellable = true)
    private static void romanFullName(Holder<Enchantment> enchantment, int level, CallbackInfoReturnable<Component> cir) {
        MutableComponent text = enchantment.value().description().copy();

        if (enchantment.is(EnchantmentTags.CURSE)) {
            text.withStyle(ChatFormatting.RED);
        } else {
            text.withStyle(ChatFormatting.GRAY);
        }

        // if the num is illegal, it will not be processed directly
        if (level <= 0) {
            text.append(" ").append(Component.literal(String.valueOf(level)));
            text.append(Component.literal(" [" + level + "]"));
            cir.setReturnValue(text);
            return;
        }

        // we made the enchantment not show "I" at level 1
        if (level != 1 || enchantment.value().getMaxLevel() != 1) {
            text.append(" ").append(Component.literal(toRomanCompressed(level)));
        }

        // add [level] made the tooltip simply
        if (Minecraft.getInstance().player != null && level > 12 && Minecraft.getInstance().player.isShiftKeyDown())
            text.append(Component.literal(" [" + level + "]").withStyle(style -> style.withBold(true).withColor(ChatFormatting.GRAY)));

        cir.setReturnValue(text);
    }

}