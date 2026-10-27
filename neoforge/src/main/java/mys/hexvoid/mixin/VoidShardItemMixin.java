package mys.hexvoid.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mys.hexvoid.block.HexvoidBlocks;
import mys.hexvoid.items.HexvoidItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class VoidShardItemMixin {
    /**
     * default : <code>-1</code>
     * <p>
     * status code:
            * <table>
     *     <tr><td><code>-1</code></td><td><code>not detected yet</code></td></tr>
            *     <tr><td><code>-2</code></td><td><code>not the target</code></td></tr>
            *     <tr><td><code>1</code></td><td><code>fuser</code></td></tr>
            * </table>
     */
    @Unique private int fuse = -1;

    @Unique
    private static boolean isTarget(ItemEntity item) {
        return item.getItem().getItem() == HexvoidItems.void_shard.get();
    }

    @WrapMethod(method = "getDefaultGravity")
    private double antiGravity(Operation<Double> original) {
        return isTarget((ItemEntity) (Object) this) ? -0.0006 : original.call();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void countdown(CallbackInfo ci) {
        var self = (ItemEntity) (Object) this;
        var lv = self.level();
        if (lv.isClientSide || self.isRemoved()) return;

        if (fuse == -2) return;              // 已排除，永久跳过
        if (fuse == -1) {                    // 首次服务端 tick 惰性判定
            if (!isTarget(self)) { fuse = -2; return; }
            fuse = 0;
        }

        if (++fuse < 100) return;            // 100 tick = 5 秒

        var pos = self.blockPosition();
        self.discard();
        lv.explode(
                null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                3.0F,
                Level.ExplosionInteraction.NONE
        );
        lv.setBlockAndUpdate(pos, HexvoidBlocks.test_block.get().defaultBlockState());
    }
}