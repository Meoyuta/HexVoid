package mys.hexvoid.casting.action;

import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import at.petrak.hexcasting.common.lib.hex.HexImageComponents;
import mys.hexvoid.item.tags.HexvoidTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class OpClearDraws implements SpellAction {

    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public boolean hasCastingSound(@NotNull CastingEnvironment castingEnvironment) {
        return true;
    }

    @Override
    public @NotNull Result execute(@NotNull List<? extends Iota> list, @NotNull CastingEnvironment castingEnvironment) {
        if (!(castingEnvironment instanceof StaffCastEnv)) throw new MishapBadCaster();
        return new Result(new Spell(), 0, List.of(), 0);
    }

    private static class Spell implements RenderedSpell {
        @Override
        public void cast(@NotNull CastingEnvironment castingEnvironment) {
            if (castingEnvironment.getCastingEntity() instanceof ServerPlayer serverPlayer) {
                var hand = castingEnvironment.getCastingHand();
                CastingVM vm = IXplatAbstractions.INSTANCE.getStaffcastVM(serverPlayer, hand);
                var image = vm.getImage();
                IXplatAbstractions.INSTANCE.sendPacketToPlayer(serverPlayer, new MsgOpenSpellGuiS2C(hand,
                        IXplatAbstractions.INSTANCE.getPatternsSavedInUi(serverPlayer), image.getStack(),
                        image.getComponent(HexImageComponents.RAVENMIND.get()), 0));
                serverPlayer.getPersistentData().put(HexvoidTags.TAG_PATTERN_CLEARED, new CompoundTag());
            }
        }

    }
}
