package dev.satlink.compat.create;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.satlink.SolarPanelBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class SolarPanelGoggleBlockEntity extends SolarPanelBlockEntity implements IHaveGoggleInformation {
    public SolarPanelGoggleBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Level level = getLevel();
        int netPanels = 1;
        int netGen = getGenerationPerTick();
        int netEnergy = getEnergy();
        int netBuffer = BUFFER;

        if (level != null) {
            Set<BlockPos> visited = new HashSet<>();
            Queue<BlockPos> queue = new ArrayDeque<>();
            queue.add(worldPosition);
            netPanels = 0;
            netGen = 0;
            netEnergy = 0;
            while (!queue.isEmpty() && netPanels < 256) {
                BlockPos cur = queue.poll();
                if (!visited.add(cur)) continue;
                BlockEntity be = level.getBlockEntity(cur);
                if (!(be instanceof SolarPanelBlockEntity panel)) continue;
                netPanels++;
                netGen += panel.getGenerationPerTick();
                netEnergy += panel.getEnergy();
                for (Direction d : Direction.values()) {
                    BlockPos np = cur.relative(d);
                    if (!visited.contains(np) && level.getBlockEntity(np) instanceof SolarPanelBlockEntity) {
                        queue.add(np);
                    }
                }
            }
            netBuffer = netPanels * BUFFER;
        }

        tooltip.add(Component.literal("    ")
                .append(Component.translatable("goggles.satlink.solar_title").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("    ")
                .append(Component.translatable("goggles.satlink.panels",
                        Component.literal(String.valueOf(netPanels)).withStyle(ChatFormatting.AQUA))
                        .withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("    ")
                .append(Component.translatable("goggles.satlink.generation",
                        Component.literal(String.format("%,d", netGen)).withStyle(ChatFormatting.AQUA))
                        .withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("    ")
                .append(Component.translatable("goggles.satlink.buffer",
                        Component.literal(String.format("%,d", netEnergy)).withStyle(ChatFormatting.AQUA),
                        Component.literal(String.format("%,d", netBuffer)).withStyle(ChatFormatting.DARK_GRAY))
                        .withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("    ")
                .append(Component.translatable(isActive() ? "goggles.satlink.active" : "goggles.satlink.idle")
                        .withStyle(isActive() ? ChatFormatting.GREEN : ChatFormatting.RED)));
        return true;
    }
}
