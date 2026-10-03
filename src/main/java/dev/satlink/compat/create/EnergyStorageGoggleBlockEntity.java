package dev.satlink.compat.create;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.satlink.EnergyStorageBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class EnergyStorageGoggleBlockEntity extends EnergyStorageBlockEntity implements IHaveGoggleInformation {
    public EnergyStorageGoggleBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal("    ")
                .append(Component.translatable("goggles.satlink.storage_title").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("    ")
                .append(Component.translatable("goggles.satlink.buffer",
                        Component.literal(String.format("%,d", getEnergy())).withStyle(ChatFormatting.AQUA),
                        Component.literal(String.format("%,d", getMaxBuffer())).withStyle(ChatFormatting.DARK_GRAY))
                        .withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("    ")
                .append(Component.translatable("goggles.satlink.income",
                        Component.literal(String.format("%,d", getIncome())).withStyle(ChatFormatting.AQUA))
                        .withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("    ")
                .append(Component.translatable("goggles.satlink.panels",
                        Component.literal(String.valueOf(getPanelCount())).withStyle(ChatFormatting.AQUA))
                        .withStyle(ChatFormatting.GRAY)));
        return true;
    }
}
