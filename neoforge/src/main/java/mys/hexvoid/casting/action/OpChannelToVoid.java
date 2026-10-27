package mys.hexvoid.casting.action;

import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.misc.MediaConstants;
import mys.hexvoid.HexvoidUtils;
import mys.hexvoid.damagesource.DamageSources;
import mys.hexvoid.datagen.DamageTypes;
import mys.hexvoid.item.tags.HexvoidTags;
import mys.hexvoid.items.HexvoidItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OpChannelToVoid implements SpellAction {

    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public @NotNull Result execute(@NotNull List<? extends Iota> list, @NotNull CastingEnvironment castingEnvironment) throws Mishap {
        return new Result(new Spell(), MediaConstants.CRYSTAL_UNIT * 2, List.of(), 0);
    }

    private static class Spell implements RenderedSpell {

        @Override
        public void cast(@NotNull CastingEnvironment env) {
            if (env.getCastingEntity() instanceof ServerPlayer player) {
                if (player.getBlockY() - 1 == -64) {
                    if (player.level() instanceof ServerLevel serverLevel) {
                        if (serverLevel.getBlockState(player.getOnPos()).is(Blocks.BEDROCK)) {
                            serverLevel.destroyBlock(player.getOnPos(), false);
                            if(HexvoidUtils.awardAdvancement(player, serverLevel, HexvoidTags.ADV_DEEPWORLD, HexvoidTags.ADV_CRITERION_TRY_CHANNEL) == 1){
                                serverLevel.addFreshEntity(new ItemEntity(serverLevel, player.getX(),player.getY(),player.getZ(), HexvoidItems.void_shard.get().getDefaultInstance()));
                            }
                        } else
                            player.displayClientMessage(Component.translatable("hexvoid.msg.spell.ctv.block"), true);
                    }
                } else player.displayClientMessage(Component.translatable("hexvoid.msg.spell.ctv.pos"), true);
            } else if (env.getCastingEntity() == null) {
                throw new MishapBadCaster();
            } else {
                env.getCastingEntity().die(DamageSources.buildDamage(env.getCastingEntity(), DamageTypes.CHANNEL_TO_VOID));
            }
        }

    }
}
