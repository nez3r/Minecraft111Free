package net.minecraft.src;

/**
 * Blood time cycle - fixes time at midnight (18000) and periodically
 * tints sky and fog dark red on damage or cave exploration.
 */
public class BloodTimeCycle {
    private static final long MIDNIGHT_TICK = 18000L;
    private static final float NORMAL_SKY_RED = 0.0F;
    private static final float BLOOD_SKY_RED = 0.6F;
    private static final float NORMAL_SKY_GREEN = 0.0F;
    private static final float BLOOD_SKY_GREEN = 0.0F;
    private static final float NORMAL_SKY_BLUE = 0.0F;
    private static final float BLOOD_SKY_BLUE = 0.0F;

    private static World world;
    private static EntityPlayer player;

    // Timers
    private static long lastBloodTintTime = 0;
    private static long bloodTintDuration = 3000L; // 3 seconds
    private static boolean bloodModeActive = false;
    private static float currentSkyRed = NORMAL_SKY_RED;
    private static float currentSkyGreen = NORMAL_SKY_GREEN;
    private static float currentSkyBlue = NORMAL_SKY_BLUE;
    private static int caveExplorationTicks = 0;

    public static void init(World world, EntityPlayer player) {
        BloodTimeCycle.world = world;
        BloodTimeCycle.player = player;
    }

    public static void setWorld(World w, EntityPlayer p) {
        world = w;
        player = p;
    }

    /**
     * Updates time cycle - fixes at midnight and applies blood tint when needed.
     */
    public static void updateTimeCycle() {
        if (world == null || player == null) return;

        // Always fix time at midnight (tick 18000)
        world.worldInfo.setWorldTime(MIDNIGHT_TICK);

        // Check for blood tint triggers
        boolean triggerBlood = false;

        // Trigger on player taking damage
        if (player.hurtTime > 0) {
            triggerBlood = true;
        }

        // Trigger on cave exploration (below certain light level and underground)
        if (player.posY < 40) {
            // Check sky light level - lower means more underground
            int skyLight = world.getBlockLightValue((int)player.posX, (int)player.posY + 2, (int)player.posZ);
            if (skyLight < 4) {
                caveExplorationTicks++;
                if (caveExplorationTicks > 100) { // After 5 seconds underground
                    triggerBlood = true;
                }
            } else {
                caveExplorationTicks = 0;
            }
        } else {
            caveExplorationTicks = 0;
        }

        // Random chance to trigger blood sky
        if (!bloodModeActive && Math.random() < 0.001) { // 0.1% chance per tick
            triggerBlood = true;
        }

        if (triggerBlood && !bloodModeActive) {
            activateBloodMode();
        }

        // Update blood mode visuals
        updateBloodVisuals();
    }

    private static void activateBloodMode() {
        bloodModeActive = true;
        lastBloodTintTime = System.currentTimeMillis();
    }

    private static void updateBloodVisuals() {
        if (!bloodModeActive) {
            // Reset to normal
            currentSkyRed = NORMAL_SKY_RED;
            currentSkyGreen = NORMAL_SKY_GREEN;
            currentSkyBlue = NORMAL_SKY_BLUE;
            return;
        }

        // Calculate blood tint based on time since activation
        long elapsed = System.currentTimeMillis() - lastBloodTintTime;

        if (elapsed > bloodTintDuration) {
            // End blood mode
            bloodModeActive = false;
            currentSkyRed = NORMAL_SKY_RED;
            currentSkyGreen = NORMAL_SKY_GREEN;
            currentSkyBlue = NORMAL_SKY_BLUE;
            return;
        }

        // Fade in then out
        float progress = (float)elapsed / (float)bloodTintDuration;
        float intensity;

        if (progress < 0.2f) {
            // Fade in (first 20%)
            intensity = progress / 0.2f;
        } else if (progress < 0.8f) {
            // Full intensity (20-80%)
            intensity = 1.0f;
        } else {
            // Fade out (80-100%)
            intensity = 1.0f - ((progress - 0.8f) / 0.2f);
        }

        currentSkyRed = BLOOD_SKY_RED * intensity;
        currentSkyGreen = BLOOD_SKY_GREEN * intensity;
        currentSkyBlue = BLOOD_SKY_BLUE * intensity;
    }

    public static float getSkyRed() {
        return currentSkyRed;
    }

    public static float getSkyGreen() {
        return currentSkyGreen;
    }

    public static float getSkyBlue() {
        return currentSkyBlue;
    }

    public static boolean isBloodModeActive() {
        return bloodModeActive;
    }

    /**
     * Trigger blood time effect manually
     */
    public static void triggerBloodTime() {
        activateBloodMode();
    }

    /**
     * Reset blood time to normal
     */
    public static void reset() {
        bloodModeActive = false;
        lastBloodTintTime = 0;
        caveExplorationTicks = 0;
        currentSkyRed = NORMAL_SKY_RED;
        currentSkyGreen = NORMAL_SKY_GREEN;
        currentSkyBlue = NORMAL_SKY_BLUE;
    }
}