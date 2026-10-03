package dev.satlink.compat.create;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.satlink.SolarPanelBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SolarPanelGoggleBlockEntity extends SolarPanelBlockEntity implements IHaveGoggleInformation {
    public SolarPanelGoggleBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal("    ")
                .append(Component.translatable("goggles.satlink.solar_title").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("    ")
                .append(Component.translatable("goggles.satlink.generation",
                        Component.literal(String.format("%,d", getGenerationPerTick())).withStyle(ChatFormatting.AQUA))
                        .withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("    ")
                .append(Component.translatable("goggles.satlink.buffer",
                        Component.literal(String.format("%,d", getEnergy())).withStyle(ChatFormatting.AQUA),
                        Component.literal(String.format("%,d", BUFFER)).withStyle(ChatFormatting.DARK_GRAY))
                        .withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("    ")
                .append(Component.translatable(isActive() ? "goggles.satlink.active" : "goggles.satlink.idle")
                        .withStyle(isActive() ? ChatFormatting.GREEN : ChatFormatting.RED)));
        return true;
    }
}
