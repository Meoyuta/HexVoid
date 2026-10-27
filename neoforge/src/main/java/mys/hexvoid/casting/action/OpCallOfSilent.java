package mys.hexvoid.casting.action;

import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import mys.hexvoid.damagesource.DamageSources;
import mys.hexvoid.datagen.DamageTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OpCallOfSilent implements SpellAction {

    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public @NotNull Result execute(@NotNull List<? extends Iota> list, @NotNull CastingEnvironment castingEnvironment) {
        return new Result(new Spell(), 0, List.of(), 0);
    }

    private static class Spell implements RenderedSpell {

        @Override
        public void cast(@NotNull CastingEnvironment castingEnvironment) {
            var entity = castingEnvironment.getCastingEntity();
            if (entity != null) {
                if (entity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.getInventory().dropAll();
                    serverPlayer.displayClientMessage(Component.translatable("hexvoid.msg.spell.cos").withStyle(ChatFormatting.RED), false);
                    entity.remove(Entity.RemovalReason.KILLED);
                } else {
                    entity.die(DamageSources.buildDamage(entity, DamageTypes.CALL_OF_SILENT));
                }
            }
        }

    }
}
