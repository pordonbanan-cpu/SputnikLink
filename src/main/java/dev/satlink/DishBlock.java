package dev.satlink;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DishBlock extends BaseEntityBlock {
    public static final MapCodec<DishBlock> CODEC = simpleCodec(DishBlock::new);

    public DishBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DishBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof DishBlockEntity be) {
            int max = SignalNetwork.MAX_CHANNEL;
            int c = be.getChannel();
            c = player.isShiftKeyDown() ? ((c - 2 + max) % max) + 1 : (c % max) + 1;
            be.setChannel(c);
            player.displayClientMessage(Component.translatable("message.satlink.channel", c), true);
        }
        return InteractionResult.CONSUME;
    }
}
