package net.minecraft.src;

/**
 * HorrorIntegration - simple facade that integrates all new horror mechanics.
 * This is a simplified version that calls each system's tick method.
 */
public class HorrorIntegration {

    private static boolean initialized = false;
    private static World world;
    private static EntityPlayer player;

    public static void init(World world, EntityPlayer player) {
        HorrorIntegration.world = world;
        HorrorIntegration.player = player;
        initialized = true;
    }

    /**
     * Main tick method - call every game tick. Integrates all new horror features.
     */
    public static void tick() {
        if (!initialized || world == null || player == null) return;
        if (HorrorState.safeMode) return;

        // Call all horror system tick methods
        try {
            DynamicFogHandler.updateFog();
        } catch (Exception e) {}
        try {
            BloodTimeCycle.updateTimeCycle();
        } catch (Exception e) {}
        try {
            FootstepEcho.tick();
        } catch (Exception e) {}
        try {
            PartisanInterference.tick();
        } catch (Exception e) {}
        try {
            InventoryGlitch.tick();
        } catch (Exception e) {}
        try {
            FalseBackgroundSounds.tick();
        } catch (Exception e) {}
        try {
            DisappearingAnomalies.tick();
        } catch (Exception e) {}
    }

    /**
     * Call when player moves or updates.
     */
    public static void onPlayerUpdate(EntityPlayer player) {
        if (player == null) return;
        HorrorIntegration.player = player;

        try {
            FootstepEcho.tick();
            DynamicWindowTitle.updateTitle(player);
        } catch (Exception e) {}
    }

    /**
     * Call when a block is broken.
     */
    public static void onBlockBroken(int blockId, EntityPlayer player) {
        if (player == null) return;
        try {
            FakeFrameFreeze.triggerOnBlockBreak(blockId, player);
        } catch (Exception e) {}
    }

    /**
     * Call when inventory opens/closes.
     */
    public static void onInventoryOpen() {
        try {
            FalseBackgroundSounds.onInventoryOpen();
        } catch (Exception e) {}
    }

    public static void onInventoryClose() {
        try {
            FalseBackgroundSounds.onInventoryClose();
        } catch (Exception e) {}
    }

    /**
     * Call for sleep attempts.
     */
    public static boolean onSleepAttempt(EntityPlayer player) {
        if (player == null) return false;
        try {
            return SleepDisruption.onPlayerSleep(player);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSafe() {
        return HorrorState.safeMode;
    }
}