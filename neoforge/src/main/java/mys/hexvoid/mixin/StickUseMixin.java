package mys.hexvoid.mixin;

import mys.hexvoid.Hexvoid;
import mys.hexvoid.damagesource.DamageSources;
import mys.hexvoid.item.tags.HexvoidTags;
import mys.hexvoid.items.HexvoidItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class StickUseMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (!level.isClientSide()) {
            if (player.getItemInHand(usedHand).is(Items.STICK) && player.isShiftKeyDown()) {
                if (player.getItemInHand(usedHand).getCount() == 1) {
                    var res = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("hexcasting", "overcast"));
                    if (player.getHealth() >= 3.0f) {
                        player.hurt(DamageSources.buildDamage(player, res), 2.0f);
                        if (player.getHealth() < 1.5f && level.getServer() != null && level instanceof ServerLevel sLevel) {
                            var advs = sLevel.getServer().getAdvancements().get(HexvoidTags.ADV_EXCHANGE_LIFE);
                            if (player instanceof ServerPlayer sPlayer && advs != null) {
                                sPlayer.getAdvancements().award(advs, HexvoidTags.ADV_CRITERION_EXCHANGE_LIFE);
                            } else if (advs == null) {
                                Hexvoid.LOGGER.error("advs is null", new NullPointerException());
                            }
                        } else if (level.getServer() == null) {
                            Hexvoid.LOGGER.error("server is null", new NullPointerException());
                        }
                        cir.setReturnValue(InteractionResultHolder.success(new ItemStack(HexvoidItems.mind_staff.get(), 1)));
                    } else {
                        player.displayClientMessage(Component.translatable("hexvoid.msg.item.mind_staff.health"), true);
                    }
                } else {
                    player.displayClientMessage(Component.translatable("hexvoid.msg.item.mind_staff.count"), true);
                }
            }
        }
    }
}
