package mys.hexvoid.mixin;

import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.forge.xplat.ForgeXplatImpl;
import mys.hexvoid.casting.vm.MindStaffEnv;
import mys.hexvoid.helper.ForgeXplatImplMixinHelper;
import mys.hexvoid.item.tags.HexvoidTags;
import mys.hexvoid.items.HexvoidItems;
import mys.hexvoid.items.MindStaffItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ForgeXplatImpl.class)
public class ForgeXplatImplMixin implements ForgeXplatImplMixinHelper {

    @Unique
    private boolean showCost = false;

    @Unique
    public void setShowCost(boolean showCost) {
        this.showCost = showCost;
    }

    @Inject(method = "getPatternsSavedInUi", at = @At("HEAD"), remap = false)
    private void clearSavedPatterns(ServerPlayer player, CallbackInfoReturnable<List<ResolvedPattern>> cir) {
        if (player.getPersistentData().contains(HexvoidTags.TAG_PATTERN_CLEARED)) {
            at.petrak.hexcasting.xplat.IXplatAbstractions.INSTANCE.setPatterns(player, List.of());
            player.getPersistentData().remove(HexvoidTags.TAG_PATTERN_CLEARED);
        }
    }

    @Inject(method = "getStaffcastVM", at = @At("RETURN"), remap = false, cancellable = true)
    private void wrapStaffcastVM(ServerPlayer player, InteractionHand hand, CallbackInfoReturnable<at.petrak.hexcasting.api.casting.eval.vm.CastingVM> cir) {
        var staff = player.getItemInHand(hand);
        if (staff.is(HexvoidItems.mind_staff.get()) && MindStaffItem.canStore(staff)) {
            var image = cir.getReturnValue().getImage();
            cir.setReturnValue(new at.petrak.hexcasting.api.casting.eval.vm.CastingVM(
                    image, new MindStaffEnv(player, hand, showCost)));
        }
    }
}
