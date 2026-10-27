package mys.hexvoid.helper;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface onWeaving {
    void weave(@NotNull ItemStack stack);
}
