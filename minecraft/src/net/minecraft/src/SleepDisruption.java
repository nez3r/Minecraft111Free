package net.minecraft.src;

/**
 * Sleep disruption - when player tries to sleep, night doesn't skip,
 * breaking bed sound plays, bed is destroyed, and red torches spawn around player.
 */
public class SleepDisruption {
    private static boolean sleepInterrupted = false;
    private static long interruptTime = 0;
    private static final long INTERRUPT_DURATION = 5000L; // 5 seconds
    private static World world;
    private static EntityPlayer player;

    /**
     * Called when player attempts to sleep. Returns true to block normal sleep.
     */
    public static boolean onPlayerSleep(EntityPlayer player) {
        if (HorrorState.safeMode) return false;

        // Speed multiplier reduces the cooldown
        // Effect is always active when /x is used

        // Always interrupt sleep (horror effect)
        sleepInterrupted = true;
        interruptTime = System.currentTimeMillis();

        // Play breaking bed/boards sound
        if (player.worldObj != null) {
            player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "step.wood", 1.0F, 0.5F);
        }

        // Destroy the bed
        destroyBed(player);

        // Spawn red torches around player
        spawnRedTorches(player);

        return true; // Block the sleep
    }

    private static void destroyBed(EntityPlayer player) {
        if (player == null || player.worldObj == null) return;

        // Find and destroy bed at player's position
        int bedX = (int)Math.floor(player.posX);
        int bedY = (int)player.posY;
        int bedZ = (int)Math.floor(player.posZ);

        // Check nearby blocks for bed
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int checkX = bedX + dx;
                    int checkY = bedY + dy;
                    int checkZ = bedZ + dz;

                    int blockId = player.worldObj.getBlockId(checkX, checkY, checkZ);
                    if (blockId == Block.bed.blockID) {
                        // Destroy the bed
                        player.worldObj.setBlock(checkX, checkY, checkZ, 0);
                    }
                }
            }
        }
    }

    private static void spawnRedTorches(EntityPlayer player) {
        if (player == null || player.worldObj == null) return;

        // Spawn red torches around the player (5-8 torches)
        int torchCount = 5 + (int)(Math.random() * 4);

        for (int i = 0; i < torchCount; i++) {
            double angle = Math.random() * Math.PI * 2;
            double distance = 2.0D + Math.random() * 3.0D; // 2-5 blocks away

            int x = (int)(player.posX + Math.cos(angle) * distance);
            int z = (int)(player.posZ + Math.sin(angle) * distance);
            int y = (int)player.posY;

            // Find valid position (on top of solid block or wall)
            if (player.worldObj.getBlockId(x, y, z) == 0 &&
                player.worldObj.getBlockId(x, y - 1, z) != 0) {
                // Place red torch
                player.worldObj.setBlock(x, y, z, Block.torchRed.blockID);
            }
        }
    }

    /**
     * Check if sleep is currently interrupted.
     */
    public static boolean isInterrupted() {
        if (!sleepInterrupted) return false;

        long elapsed = System.currentTimeMillis() - interruptTime;
        if (elapsed > INTERRUPT_DURATION) {
            sleepInterrupted = false;
            return false;
        }
        return true;
    }

    /**
     * Reset the interruption state.
     */
    public static void reset() {
        sleepInterrupted = false;
    }

    public static void setWorld(World w, EntityPlayer p) {
        world = w;
        player = p;
    }

    public static void armTrap() {
        // Arm the sleep trap for next attempt
        sleepInterrupted = false;
    }
}
