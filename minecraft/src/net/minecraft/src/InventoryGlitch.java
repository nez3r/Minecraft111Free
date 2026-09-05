package net.minecraft.src;

/**
 * GUI Glitches - random item name replacements, phantom items in slots.
 */
public class InventoryGlitch {
    private static boolean glitchActive = false;
    private static long lastGlitchTime = 0;
    private static final long GLITCH_INTERVAL = 15000L; // 15 seconds
    private static EntityPlayer player;

    public static void setPlayer(EntityPlayer p) {
        player = p;
    }

    public static boolean isGlitchActive() {
        return glitchActive && (System.currentTimeMillis() - lastGlitchTime > GLITCH_INTERVAL);
    }

    public static void triggerGlitch() {
        glitchActive = true;
        lastGlitchTime = System.currentTimeMillis();
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

        // End glitch after one call (random single occurrence)
        if (Math.random() < 0.7) {
            glitchActive = false;
        }
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
        // Random trigger of glitches
        if (Math.random() < 0.01) { // 1% chance per tick
            triggerGlitch();
        }
    }

    /**
     * Trigger minor glitch (Stage 1)
     */
    public static void triggerMinorGlitch() {
        glitchActive = true;
        lastGlitchTime = System.currentTimeMillis();
    }

    /**
     * Trigger major glitch (Stage 2-4)
     */
    public static void triggerMajorGlitch() {
        glitchActive = true;
        lastGlitchTime = System.currentTimeMillis();
    }

    /**
     * Reset inventory glitch system
     */
    public static void reset() {
        glitchActive = false;
        lastGlitchTime = 0;
    }
}