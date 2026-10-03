package dev.satlink;

import net.minecraft.core.GlobalPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Signal network: channel -> set of loaded dishes.
 * Positions stored as GlobalPos (dimension + coords).
 * Server-side only.
 */
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

    public static synchronized int count(int channel) {
        Set<GlobalPos> set = DISHES.get(channel);
        return set == null ? 0 : set.size();
    }

    /** Signal strength 0..15. */
    public static int level(int channel) {
        return Math.min(MAX_LEVEL, count(channel));
    }

    public static synchronized void clear() {
        DISHES.clear();
    }

    private SignalNetwork() {}
}
