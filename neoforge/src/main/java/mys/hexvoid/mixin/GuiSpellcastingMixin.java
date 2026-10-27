package mys.hexvoid.mixin;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import mys.hexvoid.items.HexvoidItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiSpellcasting.class)
public abstract class GuiSpellcastingMixin {

    @SuppressWarnings({"UnresolvedMixinReference", "LocalMayBeArgsOnly"})
    @WrapWithCondition(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/client/gui/GuiSpellcasting;closeForReal()V")
    )
    private boolean bypassClose(GuiSpellcasting instance, @Local(ordinal = 0) ItemStack heldItem) {
        return !heldItem.is(HexvoidItems.mind_staff.get()) && !heldItem.is(Items.STICK);
    }
}
