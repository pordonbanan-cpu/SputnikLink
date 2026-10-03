package dev.satlink;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class StationBlockEntity extends BlockEntity {
    /** Points per chip. Signal 15 ~40s/chip, signal 1 ~10 min/chip. */
    public static final int POINTS_PER_CHIP = 600;
    public static final int MAX_CHIPS = 64;

    private int channel = 1;
    private int lastLevel = -1;
    private int progress = 0;
    private int chips = 0;

    public StationBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.STATION_BE.get(), pos, state);
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int c) {
        channel = c;
        progress = 0;
        setChanged();
    }

    public int getChips() {
        return chips;
    }

    /** Take all stored chips and clear storage. */
    public int takeChips() {
        int n = chips;
        if (n > 0) {
            chips = 0;
            setChanged();
        }
        return n;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, StationBlockEntity be) {
        if (level.getGameTime() % 20L != 0L) return;
        int now = SignalNetwork.level(be.channel);
        if (now != be.lastLevel) {
            be.lastLevel = now;
            level.updateNeighbourForOutputSignal(pos, state.getBlock());
        }
        if (now > 0 && be.chips < MAX_CHIPS) {
            be.progress += now;
            while (be.progress >= POINTS_PER_CHIP && be.chips < MAX_CHIPS) {
                be.progress -= POINTS_PER_CHIP;
                be.chips++;
            }
            be.setChanged();
        }
        if (be.chips >= MAX_CHIPS) be.progress = 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Channel", channel);
        tag.putInt("Progress", progress);
        tag.putInt("Chips", chips);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        channel = tag.contains("Channel") ? tag.getInt("Channel") : 1;
        progress = tag.getInt("Progress");
        chips = tag.getInt("Chips");
    }
}
