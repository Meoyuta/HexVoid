package mys.hexvoid.casting.action;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity;
import at.petrak.hexcasting.api.misc.MediaConstants;
import mys.hexvoid.helper.onWeaving;
import mys.hexvoid.items.EnlighteningAppleItem;
import mys.hexvoid.items.HexvoidItems;
import mys.hexvoid.items.MindStaffItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OpWeavingMedia implements SpellAction {
    private static void weaveOrThrow(@NotNull ItemEntity itemEntity, @NotNull Item targetItem, @NotNull onWeaving weaveAction, boolean isLastMatch) {
        var stack = itemEntity.getItem();
        if (!stack.is(targetItem)) {
            if (!isLastMatch) return;
            else throwMishap(itemEntity);
            return;
        }
        weaveAction.weave(stack);
        itemEntity.setItem(stack);
    }

    private static void throwMishap(Entity entity) {
        throw new MishapBadEntity(entity, Component.translatable("hexvoid.msg.spell.wm"));
    }

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public @NotNull Result execute(@NotNull List<? extends Iota> args, @NotNull CastingEnvironment castingEnvironment) throws Mishap {
        var entity = OperatorUtils.getEntity(args, castingEnvironment.getWorld(), 0, getArgc());
        if (entity instanceof ItemEntity item) {
            if (!item.getItem().is(HexvoidItems.mind_staff.get()) && !item.getItem().is(HexvoidItems.enlightening_apple.get()))
                throwMishap(item);
            return new Result(new Spell(item), MediaConstants.CRYSTAL_UNIT * 10, List.of(), 0);
        }
        throw new MishapBadEntity(entity, Component.translatable("hexvoid.msg.spell.wm"));
    }

    private record Spell(ItemEntity item) implements RenderedSpell {

        @Override
            public void cast(@NotNull CastingEnvironment castingEnvironment) {
                if (!item.isAlive()) return;
                // mind staff
                weaveOrThrow(item, HexvoidItems.mind_staff.get(), MindStaffItem::setCanStore, false);
                // enlightening apple
                weaveOrThrow(item, HexvoidItems.enlightening_apple.get(), EnlighteningAppleItem::madeWeaved, true);
            }

        }
}
