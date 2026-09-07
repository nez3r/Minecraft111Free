package net.minecraft.src;

/**
 * GUI Glitches - random item name replacements, phantom items in slots.
 */
public class InventoryGlitch {
    private static boolean glitchActive = false;
    private static long glitchEndTime = 0;
    private static final long GLITCH_DURATION = 5000L;
    private static EntityPlayer player;

    public static void setPlayer(EntityPlayer p) {
        player = p;
    }

    public static boolean isGlitchActive() {
        if (glitchActive && System.currentTimeMillis() >= glitchEndTime) {
            glitchActive = false;
        }
        return glitchActive;
    }

    public static void triggerGlitch() {
        glitchActive = true;
        glitchEndTime = System.currentTimeMillis() + GLITCH_DURATION;
    }

    /**
     * Modify item display name with glitched text.
     */
    public static String glitchItemName(String originalName) {
        if (!isGlitchActive()) return originalName;

        // Replace with random glitched text or symbols
        String[] glitchedNames = {
            "???",
            "GLITCH",
            "ERROR404",
            originalName.replaceAll("[aeiou]", "?")
        };
        String newName = glitchedNames[(int)(Math.random() * glitchedNames.length)];

        return newName;
    }

    /**
     * Create phantom item in slot - returns item that disappears when clicked.
     */
    public static boolean isPhantomItem(Item item) {
        if (!isGlitchActive()) return false;
        return Math.random() < 0.15; // 15% chance phantom
    }

    public static void tick() {
        isGlitchActive();
    }

    /**
     * Trigger minor glitch (Stage 1)
     */
    public static void triggerMinorGlitch() {
        glitchActive = true;
        glitchEndTime = System.currentTimeMillis() + GLITCH_DURATION;
    }

    /**
     * Trigger major glitch (Stage 2-4)
     */
    public static void triggerMajorGlitch() {
        glitchActive = true;
        glitchEndTime = System.currentTimeMillis() + GLITCH_DURATION;
    }

    /**
     * Reset inventory glitch system
     */
    public static void reset() {
        glitchActive = false;
        glitchEndTime = 0;
    }
}