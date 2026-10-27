package mys.hexvoid.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Unique
    private static final double KB_LIMIT_DISTANCE = 1024.0D;

    // coefficient K
    @Unique
    private static final double KB_CURVE_K = 1696.6666667D;

    // value V
    @Unique
    private static final double DISTANCE_TO_STRENGTH = 0.08D;

    // instant speed
    @Unique
    private static final double MAX_SAFE_STRENGTH = 32.0D;

    @Unique
    private static double knockbackStrengthFromLevel(int level) {
        double designDistance = knockbackDistance(level);
        double strength = designDistance * DISTANCE_TO_STRENGTH;
        return Math.min(MAX_SAFE_STRENGTH, strength);
    }

    /**
     *
     * @param level the level of knockback
     * @return the knockback distance
     * @apiNote this algorithm use the func:
     * <p>
     * <code>d(level) = LIMIT * level / (level + K)</code>
     * </p>
     */
    @Unique
    private static double knockbackDistance(int level) {
        if (level <= 0) {
            return 0.0D;
        }

        // d(level) = LIMIT * level / (level + K)
        return KB_LIMIT_DISTANCE * ((double) level) / ((double) level + KB_CURVE_K);
    }

    @WrapOperation(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"
            )
    )
    private void applyBoundedKnockback(
            LivingEntity entity,
            double strength,
            double x,
            double z,
            Operation<Void> original,
            Entity target
    ) {
        Player self = (Player) (Object) this;

        int kbLevel = self.getMainHandItem().getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)
                .getLevel(self.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.KNOCKBACK));

        if (kbLevel <= 0) {
            original.call(entity, strength, x, z);
            return;
        }

        // vanilla: level * 0.5
        double vanillaEnchantStrength = kbLevel * 0.5D;

        // keep vanilla
        double baseStrength = Math.max(0.0D, strength - vanillaEnchantStrength);

        // new algorithm
        double customEnchantStrength = knockbackStrengthFromLevel(kbLevel);

        double finalStrength = baseStrength + customEnchantStrength;

        // keep the instant speed is safely
        if (finalStrength > MAX_SAFE_STRENGTH) {
            finalStrength = MAX_SAFE_STRENGTH;
        }

        if (!Double.isFinite(finalStrength) || finalStrength < 0.0D) {
            finalStrength = 0.0D;
        }

        original.call(entity, finalStrength, x, z);
    }
}