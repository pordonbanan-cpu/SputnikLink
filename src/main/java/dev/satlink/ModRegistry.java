package dev.satlink;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SatLink.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SatLink.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SatLink.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SatLink.MODID);

    private static BlockBehaviour.Properties props() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.0F, 6.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }

    public static final DeferredBlock<DishBlock> DISH =
            BLOCKS.register("dish", () -> new DishBlock(props()));
    public static final DeferredBlock<StationBlock> STATION =
            BLOCKS.register("ground_station", () -> new StationBlock(props()));
    public static final DeferredBlock<SolarPanelBlock> SOLAR =
            BLOCKS.register("solar_panel", () -> new SolarPanelBlock(props().noOcclusion()));
    public static final DeferredBlock<SolarPanelMiddleBlock> SOLAR_MIDDLE =
            BLOCKS.register("solar_panel_middle", () -> new SolarPanelMiddleBlock(props().noOcclusion()));
    public static final DeferredBlock<EnergyStorageBlock> STORAGE =
            BLOCKS.register("energy_storage", () -> new EnergyStorageBlock(props()));

    public static final DeferredItem<BlockItem> DISH_ITEM =
            ITEMS.register("dish", () -> new BlockItem(DISH.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> STATION_ITEM =
            ITEMS.register("ground_station", () -> new BlockItem(STATION.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> SOLAR_ITEM =
            ITEMS.register("solar_panel", () -> new BlockItem(SOLAR.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> SOLAR_MIDDLE_ITEM =
            ITEMS.register("solar_panel_middle", () -> new BlockItem(SOLAR_MIDDLE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> STORAGE_ITEM =
            ITEMS.register("energy_storage", () -> new BlockItem(STORAGE.get(), new Item.Properties()));

    public static final Supplier<BlockEntityType<DishBlockEntity>> DISH_BE =
            BLOCK_ENTITIES.register("dish",
                    () -> BlockEntityType.Builder.of(DishBlockEntity::new, DISH.get()).build(null));
    public static final Supplier<BlockEntityType<StationBlockEntity>> STATION_BE =
            BLOCK_ENTITIES.register("ground_station",
                    () -> BlockEntityType.Builder.of(StationBlockEntity::new, STATION.get()).build(null));
    public static final Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_BE =
            BLOCK_ENTITIES.register("solar_panel",
                    () -> BlockEntityType.Builder.of(SolarPanelBlockEntity::new,
                            SOLAR.get(), SOLAR_MIDDLE.get()).build(null));
    public static final Supplier<BlockEntityType<EnergyStorageBlockEntity>> STORAGE_BE =
            BLOCK_ENTITIES.register("energy_storage",
                    () -> BlockEntityType.Builder.of(EnergyStorageBlockEntity::new,
                            STORAGE.get()).build(null));

    public static final Supplier<CreativeModeTab> MAIN_TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.satlink"))
                    .icon(() -> new ItemStack(DISH_ITEM.get()))
                    .displayItems((params, output) -> {
                        output.accept(DISH_ITEM.get());
                        output.accept(STATION_ITEM.get());
                        output.accept(SOLAR_ITEM.get());
                        output.accept(SOLAR_MIDDLE_ITEM.get());
                        output.accept(STORAGE_ITEM.get());
                    })
                    .build());

    private ModRegistry() {}
}
