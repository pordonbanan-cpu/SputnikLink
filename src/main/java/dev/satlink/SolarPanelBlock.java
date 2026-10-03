package dev.satlink;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class SolarPanelBlock extends BaseEntityBlock {
    public static final MapCodec<SolarPanelBlock> CODEC = simpleCodec(SolarPanelBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final int THICKNESS = 2;
    public static final int MIDDLE_OFFSET = 7;

    private final boolean flush;
    private final Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);

    public SolarPanelBlock(Properties properties) {
        this(properties, true);
    }

    public SolarPanelBlock(Properties properties, boolean flush) {
        super(properties);
        this.flush = flush;
        for (Direction d : Direction.values()) {
            shapes.put(d, makeShape(d, flush));
        }
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.DOWN));
    }

    private static VoxelShape makeShape(Direction face, boolean flush) {
        if (flush) {
            return switch (face) {
                case DOWN  -> Block.box(0, 0, 0, 16, THICKNESS, 16);
                case UP    -> Block.box(0, 16 - THICKNESS, 0, 16, 16, 16);
                case NORTH -> Block.box(0, 0, 0, 16, 16, THICKNESS);
                case SOUTH -> Block.box(0, 0, 16 - THICKNESS, 16, 16, 16);
                case WEST  -> Block.box(0, 0, 0, THICKNESS, 16, 16);
                case EAST  -> Block.box(16 - THICKNESS, 0, 0, 16, 16, 16);
            };
        } else {
            return switch (face) {
                case DOWN, UP ->
                        Block.box(0, MIDDLE_OFFSET, 0, 16, MIDDLE_OFFSET + THICKNESS, 16);
                case NORTH, SOUTH ->
                        Block.box(0, 0, MIDDLE_OFFSET, 16, 16, MIDDLE_OFFSET + THICKNESS);
                case WEST, EAST ->
                        Block.box(MIDDLE_OFFSET, 0, 0, MIDDLE_OFFSET + THICKNESS, 16, 16);
            };
        }
    }

    public boolean isFlush() { return flush; }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction face = ctx.getClickedFace().getOpposite();
        return defaultBlockState().setValue(FACING, face);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return shapes.get(state.getValue(FACING));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModRegistry.SOLAR_BE.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModRegistry.SOLAR_BE.get(), SolarPanelBlockEntity::serverTick);
    }
}
