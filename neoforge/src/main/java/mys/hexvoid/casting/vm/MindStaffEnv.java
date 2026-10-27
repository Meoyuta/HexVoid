package mys.hexvoid.casting.vm;

import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv;
import at.petrak.hexcasting.api.misc.MediaConstants;
import mys.hexvoid.Hexvoid;
import mys.hexvoid.items.HexvoidItems;
import mys.hexvoid.items.MindStaffItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public class MindStaffEnv extends StaffCastEnv {
    private final boolean showCost;

    public MindStaffEnv(ServerPlayer caster, InteractionHand castingHand, boolean showCost) {
        super(caster, castingHand);
        this.showCost = showCost;
    }

    @Override
    public long extractMediaEnvironment(long cost, boolean simulate) {
        if (caster.isCreative()) return 0L;

        var staff = caster.getItemInHand(getCastingHand());

        if (staff.is(HexvoidItems.mind_staff.get())) {
            var processedCost = MindStaffItem.extractStoredMedia(staff, cost, simulate);
            if (showCost && !simulate && cost != 0) {
                var outputCost = cost + 0.0d;
                if (processedCost != 0) outputCost = processedCost + 0.0d;
                Component components = Component.translatable("hexvoid.msg.item.mind_staff.showcost").withStyle(ChatFormatting.GRAY);
                components = components.copy().append(Component.literal(" " + (outputCost / MediaConstants.DUST_UNIT + " ")).withStyle(ChatFormatting.LIGHT_PURPLE));
                components = components.copy().append(Component.translatable("hexvoid.msg.item.mind_staff.showcost.media").withStyle(ChatFormatting.GRAY));
                caster.displayClientMessage(components.copy().withStyle(style -> style.withItalic(true)), true);
                Hexvoid.LOGGER.debug("cost:{},processedCost:{},outputCost:{}", cost, processedCost, outputCost);
            }
            return processedCost;
        } else return cost;
    }
}
