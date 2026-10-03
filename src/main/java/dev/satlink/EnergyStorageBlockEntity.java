package dev.satlink;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class EnergyStorageBlockEntity extends BlockEntity {
    public static final int BASE_BUFFER = 100_000;
    public static final int PER_PANEL_BONUS = 10_000;
    public static final int MAX_PUSH = 2_000;
    public static final int MAX_NETWORK = 256;

    private int energy = 0;
    private int panelCount = 0;
    private int income = 0;

    private int sentEnergy = -1;
    private int sentPanels = -1;
    private int sentIncome = -1;

    private final IEnergyStorage outputOnly = new IEnergyStorage() {
        @Override public int receiveEnergy(int toReceive, boolean simulate) { return 0; }
        @Override public int extractEnergy(int toExtract, boolean simulate) {
            int out = Math.max(0, Math.min(toExtract, energy));
            if (!simulate && out > 0) { energy -= out; setChanged(); }
            return out;
        }
        @Override public int getEnergyStored() { return energy; }
        @Override public int getMaxEnergyStored() { return getMaxBuffer(); }
        @Override public boolean canExtract() { return true; }
        @Override public boolean canReceive() { return false; }
    };

    public EnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.STORAGE_BE.get(), pos, state);
    }

    public IEnergyStorage getEnergyCapability() { return outputOnly; }
    public int getEnergy() { return energy; }
    public int getPanelCount() { return panelCount; }
    public int getIncome() { return income; }

    public int getMaxBuffer() {
        return Math.max(BASE_BUFFER + panelCount * PER_PANEL_BONUS, energy);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EnergyStorageBlockEntity be) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        for (Direction dir : Direction.values()) {
            BlockPos np = pos.relative(dir);
            if (level.getBlockEntity(np) instanceof SolarPanelBlockEntity) {
                queue.add(np);
            }
        }

        int panels = 0;
        int pulled = 0;

        while (!queue.isEmpty() && panels < MAX_NETWORK) {
            BlockPos cur = queue.poll();
            if (!visited.add(cur)) continue;
            if (!(level.getBlockEntity(cur) instanceof SolarPanelBlockEntity panel)) continue;

            panels++;
            pulled += panel.extractEnergy(SolarPanelBlockEntity.BUFFER, false);

            for (Direction dir : Direction.values()) {
                BlockPos np = cur.relative(dir);
                if (!visited.contains(np) && level.getBlockEntity(np) instanceof SolarPanelBlockEntity) {
                    queue.add(np);
                }
            }
        }

        be.panelCount = panels;
        be.income = pulled;

        int cap = BASE_BUFFER + panels * PER_PANEL_BONUS;
        int space = Math.max(0, cap - be.energy);
        int added = Math.min(space, pulled);
        if (added > 0) {
            be.energy += added;
            be.setChanged();
        }

        if (be.energy > 0) {
            for (Direction dir : Direction.values()) {
                BlockPos np = pos.relative(dir);
                if (level.getBlockEntity(np) instanceof SolarPanelBlockEntity) continue;
                IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, np, dir.getOpposite());
                if (target == null || !target.canReceive()) continue;
                int offer = Math.min(be.energy, MAX_PUSH);
                int accepted = target.receiveEnergy(offer, false);
                if (accepted > 0) {
                    be.energy -= accepted;
                    be.setChanged();
                }
                if (be.energy <= 0) break;
            }
        }

        if (level.getGameTime() % 10L == 0L
                && (be.sentEnergy != be.energy || be.sentPanels != be.panelCount || be.sentIncome != be.income)) {
            be.sentEnergy = be.energy;
            be.sentPanels = be.panelCount;
            be.sentIncome = be.income;
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", energy);
        tag.putInt("Panels", panelCount);
        tag.putInt("Income", income);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy = tag.getInt("Energy");
        panelCount = tag.getInt("Panels");
        income = tag.getInt("Income");
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
