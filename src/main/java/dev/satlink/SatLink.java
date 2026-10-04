package dev.satlink;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Mod(SatLink.MODID)
public class SatLink {
    public static final String MODID = "satlink";

    public SatLink(IEventBus modBus) {
        ModRegistry.BLOCKS.register(modBus);
        ModRegistry.ITEMS.register(modBus);
        ModRegistry.BLOCK_ENTITIES.register(modBus);
        ModRegistry.TABS.register(modBus);
        ModRegistry.MENUS.register(modBus);
        modBus.addListener(this::registerCapabilities);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);
        NeoForge.EVENT_BUS.addListener(this::onCommands);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ModRegistry.STORAGE_BE.get(), (be, side) -> be.getEnergyCapability());
    }

    private void onServerStopped(ServerStoppedEvent event) {
        SignalNetwork.clear();
        LinkCableItem.clear();
    }

    private void onCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("satlink")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("status").executes(ctx -> {
                            String text = SignalNetwork.summary();
                            ctx.getSource().sendSuccess(() -> Component.literal(text), false);
                            return 1;
                        })));
    }
}
