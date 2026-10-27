package mys.hexvoid.casting.action;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity;
import mys.hexvoid.HexvoidUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class OpHealing implements SpellAction {
    @Override
    public int getArgc() {
        return 2;
    }

    @Override
    public boolean hasCastingSound(@NotNull CastingEnvironment castEnv) {
        return true;
    }

    @Override
    public @NotNull Result execute(@NotNull List<? extends Iota> iotas, @NotNull CastingEnvironment castEnv) throws Mishap {
        var cost = 0L;
        var num = (float) OperatorUtils.getPositiveDouble(iotas, 1, 1);
        var entity = OperatorUtils.getEntity(iotas, castEnv.getWorld(), 0, 1);
        if (entity instanceof LivingEntity livingEntity) {
            var healed = Math.min(livingEntity.getMaxHealth() - livingEntity.getHealth(), num);
            cost = calCost(healed);
            return new Result(new Spell(livingEntity, healed), cost, List.of(), 0);
        } else throw new MishapBadEntity(entity, Component.translatable("hexvoid.msg.spell.healing"));
    }

    private long calCost(float healed) {
        if (healed <= 1e-10) return 0;
        var gen = new Random();
        var P = 0.1 + gen.nextDouble(0.6);
        var K = 0.2 + gen.nextDouble(0.2) + gen.nextDouble(0.6);
        return (long) (Math.floor(HexvoidUtils.k(healed, (P + 1e-8) * 1e8, (K + 1e-8) * 1e9)) / 1000 * healed) + 1000;
    }

    private static class Spell implements RenderedSpell {
        private final LivingEntity target;
        private final float amount;

        private Spell(LivingEntity target, float amount) {
            this.target = target;
            this.amount = amount;
        }

        @Override
        public void cast(@NotNull CastingEnvironment castingEnvironment) {
            if (target.isAlive()) target.heal(amount);
        }

    }
}
