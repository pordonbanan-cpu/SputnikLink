package dev.satlink.compat.create;

import dev.satlink.EnergyStorageBlockEntity;
import dev.satlink.SolarPanelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Factories for goggle-aware BEs. Only called when Create is loaded. */
public final class CreateBridge {
    public static SolarPanelBlockEntity solar(BlockPos pos, BlockState state) {
        return new SolarPanelGoggleBlockEntity(pos, state);
    }

    public static EnergyStorageBlockEntity storage(BlockPos pos, BlockState state) {
        return new EnergyStorageGoggleBlockEntity(pos, state);
    }

    private CreateBridge() {}
}
