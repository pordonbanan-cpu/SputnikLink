package dev.satlink;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

/** Same panel, but centered in the block (middle of the axis matching FACING). */
public class SolarPanelMiddleBlock extends SolarPanelBlock {
    public static final MapCodec<SolarPanelMiddleBlock> CODEC = simpleCodec(SolarPanelMiddleBlock::new);

    public SolarPanelMiddleBlock(Properties properties) {
        super(properties, false);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
