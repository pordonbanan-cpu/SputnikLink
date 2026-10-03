package dev.satlink;

import net.neoforged.fml.ModList;

/** Only place that asks if Create is loaded. Does NOT import Create. */
public final class CreateCompat {
    public static boolean isCreateLoaded() {
        ModList list = ModList.get();
        return list != null && list.isLoaded("create");
    }

    private CreateCompat() {}
}
