package dev.satlink;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class StationBlock extends BaseEntityBlock {
    public static final MapCodec<StationBlock> CODEC = simpleCodec(StationBlock::new);

    public StationBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.satlink.ground_station"));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StationBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModRegistry.STATION_BE.get(), StationBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof StationBlockEntity be) {
            if (player.isShiftKeyDown()) {
                int max = SignalNetwork.MAX_CHANNEL;
                be.setChannel((be.getChannel() % max) + 1);
                player.displayClientMessage(Component.translatable("message.satlink.channel", be.getChannel()), true);
            } else {
                int taken = be.takeChips();
                if (taken > 0) {
                    ItemStack stack = new ItemStack(ModRegistry.DATA_CHIP.get(), taken);
                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }
                }
                int ch = be.getChannel();
                player.displayClientMessage(Component.translatable("message.satlink.status",
                        ch, SignalNetwork.count(ch), SignalNetwork.level(ch), taken), true);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof StationBlockEntity be
                ? SignalNetwork.level(be.getChannel()) : 0;
    }
}
