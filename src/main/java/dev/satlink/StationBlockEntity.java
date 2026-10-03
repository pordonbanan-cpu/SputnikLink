package dev.satlink;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class StationBlockEntity extends BlockEntity {
    private int channel = 1;
    private int lastLevel = -1;

    public StationBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.STATION_BE.get(), pos, state);
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int c) {
        channel = c;
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, StationBlockEntity be) {
        if (level.getGameTime() % 20L != 0L) return;
        int now = SignalNetwork.level(be.channel);
        if (now != be.lastLevel) {
            be.lastLevel = now;
            level.updateNeighbourForOutputSignal(pos, state.getBlock());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Channel", channel);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        channel = tag.contains("Channel") ? tag.getInt("Channel") : 1;
    }
}
