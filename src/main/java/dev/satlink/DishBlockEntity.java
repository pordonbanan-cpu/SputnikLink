package dev.satlink;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DishBlockEntity extends BlockEntity {
    private int channel = 1;

    public DishBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.DISH_BE.get(), pos, state);
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int newChannel) {
        if (level != null && !level.isClientSide) {
            GlobalPos gp = GlobalPos.of(level.dimension(), worldPosition);
            SignalNetwork.remove(channel, gp);
            channel = newChannel;
            SignalNetwork.add(channel, gp);
        } else {
            channel = newChannel;
        }
        setChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            SignalNetwork.add(channel, GlobalPos.of(level.dimension(), worldPosition));
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide) {
            SignalNetwork.remove(channel, GlobalPos.of(level.dimension(), worldPosition));
        }
        super.setRemoved();
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
