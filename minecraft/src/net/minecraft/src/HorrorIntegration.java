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
        DynamicFogHandler.init(world, player);
        BloodTimeCycle.init(world, player);
        FootstepEcho.init(world, player);
        InventoryGlitch.setPlayer(player);
        InventoryDeletionLies.setPlayer(player);
        WorldCorruptorGenerator.init(world, player);
        SleepDisruption.setWorld(world, player);
        initialized = true;
    }

    /**
     * Main tick method - call every game tick. Integrates all new horror features.
     */
    public static void tick() {
        if (!initialized || world == null || player == null) return;
        if (HorrorState.safeMode) return;

        HorrorEffectsManager.processNetworkEvents();
        // Call all horror system tick methods
        DynamicFogHandler.updateFog();
        BloodTimeCycle.updateTimeCycle();
        FootstepEcho.tick();
        TemporalVoidDrop.onTick();
        RenderHorrorEffects.tick();
        PartisanInterference.tick();
        InventoryGlitch.tick();
        FalseBackgroundSounds.tick();
        DisappearingAnomalies.tick();
        InventoryDeletionLies.tick();
        WorldCorruptorGenerator.tick();
        HallucinatorySoundPan.tick();
        BedtimeTrappedDimension.tick();
    }

    /**
     * Call when player moves or updates.
     */
    public static void onPlayerUpdate(EntityPlayer player) {
        if (player == null) return;
        HorrorIntegration.player = player;

        FootstepEcho.tick();
        FakeFrameFreeze.applyDisplacement(player);
        DynamicWindowTitle.updateTitle(player);
    }

    /**
     * Call when a block is broken.
     */
    public static void onBlockBroken(int blockId, EntityPlayer player) {
        if (player == null) return;
        FakeFrameFreeze.triggerOnBlockBreak(blockId, player);
    }

    /**
     * Call when inventory opens/closes.
     */
    public static void onInventoryOpen() {
        FalseBackgroundSounds.onInventoryOpen();
    }

    public static void onInventoryClose() {
        FalseBackgroundSounds.onInventoryClose();
    }

    /**
     * Call for sleep attempts.
     */
    public static boolean onSleepAttempt(EntityPlayer player) {
        if (player == null) return false;
        return SleepDisruption.onPlayerSleep(player);
    }

    public static boolean isSafe() {
        return HorrorState.safeMode;
    }
}
