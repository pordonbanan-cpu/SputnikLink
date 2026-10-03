package dev.satlink;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlockEntity extends BlockEntity {
    public static final int GENERATION_PER_TICK = 40;
    public static final int BUFFER = 10_000;

    private int energy = 0;
    private boolean active = false;
    private int sentEnergy = -1;
    private boolean sentActive = false;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.SOLAR_BE.get(), pos, state);
    }

    public int getEnergy() {
        return energy;
    }

    public int getGenerationPerTick() {
        return active ? GENERATION_PER_TICK : 0;
    }

    public int extractEnergy(int amount, boolean simulate) {
        int out = Math.max(0, Math.min(amount, energy));
        if (!simulate && out > 0) {
            energy -= out;
            setChanged();
        }
        return out;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolarPanelBlockEntity be) {
        long time = level.getGameTime();
        if (time % 20L == 0L) {
            be.active = level.isDay()
                    && level.canSeeSky(pos.above())
                    && !level.isRainingAt(pos.above())
                    && !level.isThundering();
        }
        if (be.active && be.energy < BUFFER) {
            be.energy = Math.min(BUFFER, be.energy + GENERATION_PER_TICK);
            be.setChanged();
        }
        if (time % 10L == 0L && (be.sentEnergy != be.energy || be.sentActive != be.active)) {
            be.sentEnergy = be.energy;
            be.sentActive = be.active;
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", energy);
        tag.putBoolean("Active", active);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy = tag.getInt("Energy");
        active = tag.getBoolean("Active");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
