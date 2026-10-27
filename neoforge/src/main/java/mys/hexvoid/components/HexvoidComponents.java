package mys.hexvoid.components;

import com.mojang.serialization.Codec;
import mys.hexvoid.Hexvoid;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class HexvoidComponents {
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Hexvoid.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> STORED_MEDIA =
            COMPONENTS.register("stored_media", () -> DataComponentType.<Long>builder()
                    .persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> MAX_MEDIA =
            COMPONENTS.register("max_media", () -> DataComponentType.<Long>builder()
                    .persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CAN_STORE_MEDIA =
            COMPONENTS.register("can_store_media", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> WEAVED =
            COMPONENTS.register("weaved", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    private HexvoidComponents() {
    }

    public static void register(IEventBus bus) {
        COMPONENTS.register(bus);
    }
}
