package dev.satlink;

/**
 * Optional Create presence check. Used only to avoid hard dependency at runtime.
 */
public final class CreateCompat {
    public static final boolean PRESENT;

    static {
        boolean present;
        try {
            Class.forName("com.simibubi.create.Create");
            present = true;
        } catch (ClassNotFoundException e) {
            present = false;
        }
        PRESENT = present;
    }

    private CreateCompat() {}
}
