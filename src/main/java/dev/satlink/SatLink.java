package dev.satlink;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Mod(SatLink.MODID)
public class SatLink {
    public static final String MODID = "satlink";

    public SatLink(IEventBus modBus) {
        ModRegistry.BLOCKS.register(modBus);
        ModRegistry.ITEMS.register(modBus);
        ModRegistry.BLOCK_ENTITIES.register(modBus);
        modBus.addListener(this::addCreative);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModRegistry.DISH_ITEM.get());
            event.accept(ModRegistry.STATION_ITEM.get());
        }
    }

    private void onServerStopped(ServerStoppedEvent event) {
        SignalNetwork.clear();
    }
}
