package dev.satlink;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

/** Same panel, centered in the block (Y 7–9 px). */
public class SolarPanelMiddleBlock extends SolarPanelBlock {
    public static final MapCodec<SolarPanelMiddleBlock> CODEC = simpleCodec(SolarPanelMiddleBlock::new);

    public SolarPanelMiddleBlock(Properties properties) {
        super(properties, 7);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
