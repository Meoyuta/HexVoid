package mys.hexvoid.items;


import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.lib.HexAttributes;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.common.lib.hex.HexImageComponents;
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C;
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C;
import at.petrak.hexcasting.forge.xplat.ForgeXplatImpl;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import mys.hexvoid.Hexvoid;
import mys.hexvoid.components.HexvoidComponents;
import mys.hexvoid.helper.ClientInputHelper;
import mys.hexvoid.helper.ForgeXplatImplMixinHelper;
import mys.hexvoid.item.tags.HexvoidTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;


public class MindStaffItem extends Item {
    public static final TextColor HEX_COLOR = TextColor.fromRgb(11767539);
    private static final DecimalFormat PERCENTAGE = new DecimalFormat("####");
    private static final DecimalFormat DUST_AMOUNT;

    static {
        PERCENTAGE.setRoundingMode(RoundingMode.DOWN);
        DUST_AMOUNT = new DecimalFormat("###,###.##");
    }

    public MindStaffItem(Properties pProperties) {
        super(pProperties);
    }

    public static boolean canStore(@NotNull ItemStack stack) {
        return stack.getOrDefault(HexvoidComponents.CAN_STORE_MEDIA.get(), false);
    }

    public static void setCanStore(@NotNull ItemStack stack) {
        stack.set(HexvoidComponents.CAN_STORE_MEDIA.get(), true);
    }

    public static long getStoredMedia(ItemStack stack) {
        return stack.getOrDefault(HexvoidComponents.STORED_MEDIA.get(), 0L);
    }

    public static long getMaxMedia(ItemStack stack) {
        var max_last = stack.getOrDefault(HexvoidComponents.MAX_MEDIA.get(), 0L);
        if (max_last <= 0) max_last = 1L;
        return max_last;
    }

    public static void setStoredMedia(ItemStack stack, long media) {
        if (Long.MAX_VALUE - getStoredMedia(stack) < media)
            stack.set(HexvoidComponents.STORED_MEDIA.get(), Long.MAX_VALUE);
        else
            stack.set(HexvoidComponents.STORED_MEDIA.get(), media);
    }

    public static void setMaxMedia(ItemStack stack, long media) {
        stack.set(HexvoidComponents.MAX_MEDIA.get(), media);
    }

    public static long extractStoredMedia(ItemStack stack, long requested, boolean simulate) {
        long stored = getStoredMedia(stack);
        long paid = Math.min(requested, stored);

        if (!simulate) {
            setStoredMedia(stack, stored - paid);
        }

        return requested - paid;
    }

    @SuppressWarnings("unused")
    public static long insertStoredMedia(ItemStack stack, long amount, boolean simulate) {
        long stored = getStoredMedia(stack);
        long last_max = getMaxMedia(stack);
        long media_current = stored + amount;

        if (!simulate) {
            setStoredMedia(stack, media_current);
            if (media_current > last_max) setMaxMedia(stack, media_current);
        }

        return amount;
    }

    private static InteractionHand getAnotherHand(InteractionHand hand) {
        if (hand.equals(InteractionHand.MAIN_HAND)) return InteractionHand.OFF_HAND;
        else return InteractionHand.MAIN_HAND;
    }

    private static void tryStore(@NotNull ItemStack source, @NotNull ItemStack staff) {
        if (source.isEmpty()) return;

        ADMediaHolder holder = IXplatAbstractions.INSTANCE.findMediaHolder(source);
        if (holder == null || !holder.canProvide()) return;

        long extracted = MediaHelper.extractMedia(source, Long.MAX_VALUE - getStoredMedia(staff));
        if (extracted <= 0) return;


        setStoredMedia(staff, Math.min(Long.MAX_VALUE - getStoredMedia(staff), extracted) + getStoredMedia(staff));
        if (getStoredMedia(staff) > getMaxMedia(staff))
            setMaxMedia(staff, getStoredMedia(staff));
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        if (player.getAttributeValue(HexAttributes.FEEBLE_MIND) > (double) 0.0F) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        } else {

            if (canStore(player.getItemInHand(hand))) {
                var staff = player.getItemInHand(hand);
                var stackAnother = player.getItemInHand(getAnotherHand(hand));
                var holder = IXplatAbstractions.INSTANCE.findMediaHolder(stackAnother);

                boolean hasMedia;
                if (holder == null) hasMedia = false;
                else
                    hasMedia = (holder.canProvide() && MediaHelper.extractMedia(stackAnother, Long.MAX_VALUE - getStoredMedia(staff), false, true) > 0);

                if (Hexvoid.IS_DEBUG_MODE) Hexvoid.LOGGER.debug(String.valueOf(hasMedia));

                if (hasMedia && getStoredMedia(staff) < Long.MAX_VALUE) {
                    tryStore(stackAnother, staff);
                    return InteractionResultHolder.success(player.getItemInHand(hand));
                } else if (hasMedia && getStoredMedia(staff) == Long.MAX_VALUE) {
                    player.displayClientMessage(Component.translatable("hexvoid.msg.item.mind_staff.store"), true);
                    return InteractionResultHolder.fail(player.getItemInHand(hand));
                }

                if (getStoredMedia(staff) > getMaxMedia(staff)) {
                    if (getStoredMedia(staff) * 1.2 > Long.MAX_VALUE) {
                        setMaxMedia(staff, (long) (Long.MAX_VALUE - (Long.MAX_VALUE - getStoredMedia(staff)) * 0.3));
                    } else {
                        setMaxMedia(staff, (long) (getStoredMedia(staff) * 1.2));
                    }
                }
            }

            if (player.isShiftKeyDown()) {
                if (world.isClientSide()) {
                    player.playSound(HexSounds.STAFF_RESET.value(), 1.0F, 1.0F);
                } else if (player instanceof ServerPlayer serverPlayer) {
                    IXplatAbstractions.INSTANCE.clearCastingData(serverPlayer);
                    MsgClearSpiralPatternsS2C packet = new MsgClearSpiralPatternsS2C(player.getUUID());
                    IXplatAbstractions.INSTANCE.sendPacketToPlayer(serverPlayer, packet);
                    IXplatAbstractions.INSTANCE.sendPacketTracking(serverPlayer, packet);
                }
            }

            var showCost = false;
            if (world.isClientSide()) {
                showCost = ClientInputHelper.isCtrlDown();
                ((ForgeXplatImplMixinHelper) ForgeXplatImpl.INSTANCE).setShowCost(showCost);
            }

            if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                var vm = IXplatAbstractions.INSTANCE.getStaffcastVM(serverPlayer, hand);
                var image = vm.getImage();
                IXplatAbstractions.INSTANCE.sendPacketToPlayer(serverPlayer, new MsgOpenSpellGuiS2C(hand,
                        IXplatAbstractions.INSTANCE.getPatternsSavedInUi(serverPlayer), image.getStack(),
                        image.getComponent(HexImageComponents.RAVENMIND.get()), 0));
                serverPlayer.getPersistentData().put(HexvoidTags.TAG_PATTERN_CLEARED, new CompoundTag());
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!canStore(player.getItemInHand(hand))) {
                player.getItemInHand(hand).hurtAndBreak(1, player,
                        hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                player.setItemInHand(hand, new ItemStack(Items.STICK, 1));
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        if (canStore(stack)) {
            long maxMedia = getMaxMedia(stack);
            if (maxMedia > 0L) {
                long media = getStoredMedia(stack);
                float fullness = this.getMediaFullness(stack);
                TextColor color = TextColor.fromRgb(MediaHelper.mediaBarColor(media, maxMedia));
                MutableComponent mediaMount = Component.literal(DUST_AMOUNT.format((float) media / 10000.0F));
                MutableComponent percentFull = Component.literal(PERCENTAGE.format(100.0F * fullness) + "%");
                MutableComponent maxCapacity = Component.translatable("hexcasting.tooltip.media", DUST_AMOUNT.format((float) maxMedia / 10000.0F));
                mediaMount.withStyle((style) -> style.withColor(HEX_COLOR));
                maxCapacity.withStyle((style) -> style.withColor(HEX_COLOR));
                percentFull.withStyle((style) -> style.withColor(color));
                pTooltipComponents.add(Component.translatable("hexvoid.tooltip.media_amount", mediaMount, maxCapacity, percentFull));
            }
        }
        super.appendHoverText(stack, context, pTooltipComponents, pIsAdvanced);
    }

    private float getMediaFullness(ItemStack stack) {
        return getMaxMedia(stack) == 0L ? 0.0F : (float) getStoredMedia(stack) / getMaxMedia(stack);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return canStore(stack);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return MediaHelper.mediaBarWidth(getStoredMedia(stack), getMaxMedia(stack));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return MediaHelper.mediaBarColor(getStoredMedia(stack), getMaxMedia(stack));
    }
}
