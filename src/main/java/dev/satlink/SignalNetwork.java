package dev.satlink;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class SignalNetwork {
    public static final int MAX_CHANNEL = 16;
    public static final int MAX_LEVEL = 15;

    private static final Map<Integer, Set<GlobalPos>> DISHES = new HashMap<>();

    public static synchronized void add(int channel, GlobalPos pos) {
        DISHES.computeIfAbsent(channel, k -> new HashSet<>()).add(pos);
    }

    public static synchronized void remove(int channel, GlobalPos pos) {
        Set<GlobalPos> set = DISHES.get(channel);
        if (set != null) {
            set.remove(pos);
            if (set.isEmpty()) DISHES.remove(channel);
        }
    }

    private static boolean valid(int channel, GlobalPos gp) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return true;
        ServerLevel level = server.getLevel(gp.dimension());
        if (level == null) return false;
        BlockPos bp = gp.pos();
        if (!level.isLoaded(bp)) return false;
        return level.getBlockEntity(bp) instanceof DishBlockEntity d
                && !d.isRemoved() && d.getChannel() == channel;
    }

    public static synchronized int count(int channel) {
        Set<GlobalPos> set = DISHES.get(channel);
        if (set == null) return 0;
        set.removeIf(gp -> !valid(channel, gp));
        if (set.isEmpty()) {
            DISHES.remove(channel);
            return 0;
        }
        return set.size();
    }

    public static int level(int channel) {
        return Math.min(MAX_LEVEL, count(channel));
    }

    public static synchronized String summary() {
        if (DISHES.isEmpty()) return "SatLink: антенн нет";
        StringBuilder sb = new StringBuilder("SatLink: ");
        for (Integer ch : new java.util.TreeSet<>(DISHES.keySet())) {
            Set<GlobalPos> set = DISHES.get(ch);
            if (set == null) continue;
            set.removeIf(gp -> !valid(ch, gp));
            if (set.isEmpty()) continue;
            sb.append("[канал ").append(ch).append(": ").append(set.size()).append(" антенн");
            int shown = 0;
            for (GlobalPos gp : set) {
                if (shown++ >= 3) { sb.append(" ..."); break; }
                sb.append(" ").append(gp.pos().toShortString());
            }
            sb.append("] ");
        }
        return sb.toString();
    }

    public static synchronized void clear() {
        DISHES.clear();
    }

    private SignalNetwork() {}
}
