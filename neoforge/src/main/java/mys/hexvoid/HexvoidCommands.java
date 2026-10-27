package mys.hexvoid;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Hexvoid.MODID)
public class HexvoidCommands {
    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("hexvoid")
                        .then(
                                Commands.literal("test_output")
                                        .executes(ctx -> {
                                            ctx.getSource().sendSuccess(() -> Component.literal("a test for command output."), true);
                                            return 0;
                                        })
                                        .requires(source -> source.hasPermission(Commands.LEVEL_ALL))
                        ).then(
                                Commands.literal("test_do_no_run_this")
                                        .executes(ctx -> {
                                            if (ctx.getSource().getPlayer() != null)
                                                ctx.getSource().getPlayer().kill();
                                            return 0;
                                        })
                                        .requires(source -> source.hasPermission(Commands.LEVEL_ADMINS))
                        ).then(
                                Commands.literal("debug")
                                        .executes(ctx -> {
                                            Hexvoid.debug();
                                            ctx.getSource().sendSuccess(() -> Component.literal("debug mode " + (Hexvoid.IS_DEBUG_MODE ? "enabled" : "disabled")), true);
                                            return 0;
                                        })
                                        .requires(source -> source.hasPermission(Commands.LEVEL_ADMINS))
                        )
        );


    }
}
