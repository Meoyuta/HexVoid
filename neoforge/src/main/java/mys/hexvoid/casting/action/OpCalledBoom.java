package mys.hexvoid.casting.action;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.casting.actions.selectors.OpGetEntitiesBy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class OpCalledBoom implements SpellAction {
    private final boolean fire = false;

    @Override
    public int getArgc() {
        return 2;
    }

    @Override
    public @NotNull Result execute(@NotNull List<? extends Iota> args, @NotNull CastingEnvironment env) {
        // 获取位置参数
        Vec3 pos = OperatorUtils.getVec3(args, 0, getArgc());
        // 获取爆炸强度参数
        double strength = OperatorUtils.getPositiveDoubleUnderInclusive(args, 1, 64.0, getArgc());
        // 确保位置在有效范围内
        try {
            env.assertVecInRange(pos);
        } catch (MishapBadLocation e) {
            throw new RuntimeException(e);
        }

        // 防止爆炸点恰好位于实体眼睛位置
        double eps = 0.000001;
        Vec3 epsv = new Vec3(eps, eps, eps);
        AABB aabb = new AABB(pos.subtract(epsv), pos.add(epsv));

        // Java 中需要显式创建 Predicate
        Predicate<Object> predicate = entity -> OpGetEntitiesBy.Companion.isReasonablySelectable(env, (Entity) entity);
        Vec3 finalPos = pos;
        boolean tooCloseToEyePos = env.getWorld().getEntities((Entity) null, aabb, predicate).stream()
                .anyMatch(entity -> entity.getEyePosition().distanceToSqr(finalPos) == 0.0);

        if (tooCloseToEyePos) {
            pos = pos.add(0.0, 0.000001, 0.0);
        }

        // 法术消耗
        long cost = MediaConstants.DUST_UNIT * 2;

        // 创建粒子效果列表
        var particles = List.of(ParticleSpray.burst(pos, strength, 50));

        // 返回结果
        return new Result(
                new Spell(pos, strength, this.fire),
                cost,
                particles,
                0);
    }

    @NotNull
    public String toString() {
        return "OpCalledBoom(fire=" + this.fire + ")";
    }

    public int hashCode() {
        return Boolean.hashCode(fire);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof OpCalledBoom boom) {
            return boom.fire == fire;
        } else {
            return false;
        }
    }


    // 内部类，表示爆炸法术
    private static class Spell implements RenderedSpell {

        private final Vec3 pos;
        private final double strength;
        private final boolean fire;

        public Spell(Vec3 pos, double strength, boolean fire) {
            this.pos = pos;
            this.strength = strength;
            this.fire = fire;
        }

        @Override
        public void cast(CastingEnvironment env) {
            // 检查是否可以在此位置编辑方块
            if (!env.canEditBlockAt(BlockPos.containing(pos))) {
                return;
            }

            // 执行爆炸
            env.getWorld().explode(
                    env.getCastingEntity(),
                    pos.x, pos.y, pos.z,
                    (float) strength,
                    fire,
                    Level.ExplosionInteraction.TNT
            );
        }

        // 手动实现 equals, hashCode, toString
        @Override
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            } else if (other instanceof Spell spell) {
                return spell.toString().equals(this.toString());
            } else return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, strength, fire);
        }

        @Override
        public String toString() {
            return "Spell(pos=" + pos + ", strength=" + strength + ", fire=" + fire + ")";
        }

    }
}
