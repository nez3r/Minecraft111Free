package net.minecraft.src;

/**
 * Global state manager for horror mod features
 */
public class HorrorState {
    // Timing
    public static long worldStartTime = 0;
    public static long totalPlayTime = 0;

    // Entity tracking
    public static Entity404 stalkerEntity = null;
    public static boolean stalkerActive = true;

    // Glitch system
    public static long lastGlitchTime = 0;
    public static int glitchIntensity = 0;
    public static int glitchCount = 0;
    public static boolean finalGlitchTriggered = false;
    public static long lastDayNightFlickerTime = 0;
    public static long lastCursorGlitchTime = 0;
    public static long lastWindowShakeTime = 0;

    // Screamer system
    public static long lastScreamerTime = 0;
    public static int screamerCount = 0;

    // Exit trap
    public static boolean exitTrapActive = false;
    public static int windowCloseAttempts = 0;

    // Attack sequence
    public static boolean attackSequenceActive = false;
    public static boolean diamondsTaken = false;
    public static EntityBlackDemon attackEntity = null;
    public static long attackStartTime = 0;

    // Safety
    public static boolean safeMode = false;

    // Speed multiplier for horror effects
    public static float horrorSpeedMultiplier = 1.0F;

    // Portal and tunnel
    public static boolean portalGenerated = false;
    public static int portalX = 0;
    public static int portalY = 0;
    public static int portalZ = 0;

    // Bedrock tunnel
    public static boolean tunnelGenerated = false;
    public static int tunnelX = 0;
    public static int tunnelY = 0;
    public static int tunnelZ = 0;
    public static boolean tunnelEntranceClosed = false;
    public static boolean tunnelChestOpened = false;

    // Error404 house
    public static int error404HouseX = 0;
    public static int error404HouseY = 0;
    public static int error404HouseZ = 0;
    public static boolean error404Triggered = false;

    /**
     * Reset all state (for new world)
     */
    public static void reset() {
        worldStartTime = System.currentTimeMillis();
        totalPlayTime = 0;
        stalkerEntity = null;
        stalkerActive = true;
        lastGlitchTime = 0;
        glitchIntensity = 0;
        glitchCount = 0;
        finalGlitchTriggered = false;
        lastDayNightFlickerTime = 0;
        lastCursorGlitchTime = 0;
        lastWindowShakeTime = 0;
        lastScreamerTime = 0;
        screamerCount = 0;
        exitTrapActive = false;
        windowCloseAttempts = 0;
        attackSequenceActive = false;
        diamondsTaken = false;
        attackEntity = null;
        attackStartTime = 0;
        safeMode = false;
        portalGenerated = false;
        tunnelGenerated = false;
        tunnelEntranceClosed = false;
        tunnelChestOpened = false;
    }

    /**
     * Update play time
     */
    public static void updatePlayTime() {
        if (worldStartTime > 0) {
            totalPlayTime = System.currentTimeMillis() - worldStartTime;
        }
    }

    /**
     * Check if enough time has passed for exit trap
     */
    public static boolean shouldActivateExitTrap() {
        long requiredTime = (long)(30 * 60 * 1000 / horrorSpeedMultiplier);
        return totalPlayTime >= requiredTime;
    }

    /**
     * Check if enough time has passed for screamers
     */
    public static boolean canTriggerScreamer() {
        long requiredTime = (long)(10 * 60 * 1000 / horrorSpeedMultiplier);
        return totalPlayTime >= requiredTime;
    }
}
