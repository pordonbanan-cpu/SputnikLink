package dev.satlink;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** Same as SolarPanelBlock but the thin panel sits in the middle of the block (Y 7–9 px). */
public class SolarPanelMiddleBlock extends SolarPanelBlock {
    public static final MapCodec<SolarPanelMiddleBlock> CODEC = simpleCodec(SolarPanelMiddleBlock::new);

    public SolarPanelMiddleBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends SolarPanelBlock> codec() {
        return CODEC;
    }

    @Override
    protected float panelMinY() {
        return 7f / 16f;
    }

    @Override
    protected float panelMaxY() {
        return 9f / 16f;
    }
}
