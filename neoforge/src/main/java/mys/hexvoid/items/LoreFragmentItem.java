package mys.hexvoid.items;

import at.petrak.hexcasting.common.lib.HexSounds;
import mys.hexvoid.HexvoidUtils;
import mys.hexvoid.item.tags.HexvoidTags;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class LoreFragmentItem extends Item {

    public LoreFragmentItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        player.playSound(HexSounds.READ_LORE_FRAGMENT.value(), 1.0F, 1.0F);
        if (!level.isClientSide()) {
            if (level instanceof ServerLevel serverlevel) {
                if (player instanceof ServerPlayer serverPlayer) {
                    var isAwarded = HexvoidUtils.awardAdvancement(serverPlayer, serverlevel, HexvoidTags.ADV_LORE_RESEARCH, HexvoidTags.ADV_CRITERION_GRANT);
                    if (isAwarded == 1) {
                        serverPlayer.displayClientMessage(Component.translatable("hexvoid.msg.item.lore_fragment"), true);
                        serverPlayer.giveExperiencePoints(20);
                    }
                }
            }
        }

        var itemStack = player.getItemInHand(usedHand);
        itemStack.shrink(1);
        return InteractionResultHolder.success(itemStack);
    }
}
