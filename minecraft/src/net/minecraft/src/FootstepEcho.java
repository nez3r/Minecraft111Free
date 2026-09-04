package net.minecraft.src;

/**
 * Footstep echo system - plays footstep sounds 0.5-1 second AFTER player stops.
 * Creates unsettling echo effect that makes player think they're being followed.
 */
public class FootstepEcho {
    private static final long ECHO_DELAY_MIN = 500L; // 0.5 seconds
    private static final long ECHO_DELAY_MAX = 1000L; // 1 second

    private static World world;
    private static EntityPlayer player;

    private static boolean wasMoving = false;
    private static long stopTime = 0;
    private static boolean echoScheduled = false;
    private static String lastFootstepSound = "";
    private static long lastScheduledEchoTime = 0;

    public static void init(World world, EntityPlayer player) {
        FootstepEcho.world = world;
        FootstepEcho.player = player;
    }

    /**
     * Call this every tick to track player movement and schedule echoes.
     */
    public static void tick() {
        if (world == null || player == null) return;
        if (HorrorState.safeMode) return;

        boolean isMoving = isPlayerMoving();

        if (isMoving) {
            wasMoving = true;
            stopTime = System.currentTimeMillis();
            // Schedule an echo when they stop
            if (!echoScheduled) {
                scheduleEcho();
            }
        } else if (wasMoving && echoScheduled) {
            // Player just stopped - check if echo should play
            checkEchoPlayback();
        }
    }

    private static boolean isPlayerMoving() {
        if (player == null) return false;

        // Check if player is moving (using motion or position changes)
        double dx = player.posX - player.prevPosX;
        double dz = player.posZ - player.prevPosZ;

        // Check for significant movement
        double movement = Math.sqrt(dx * dx + dz * dz);

        // Also check if on ground (avoid false triggers when falling)
        return movement > 0.01D && player.onGround;
    }

    private static void scheduleEcho() {
        echoScheduled = true;
        lastScheduledEchoTime = System.currentTimeMillis();
        lastFootstepSound = getCurrentFootstepSound();
    }

    private static void checkEchoPlayback() {
        if (!echoScheduled) return;

        long elapsed = System.currentTimeMillis() - lastScheduledEchoTime;
        long delay = ECHO_DELAY_MIN + (long)(Math.random() * (ECHO_DELAY_MAX - ECHO_DELAY_MIN));

        if (elapsed >= delay) {
            playEcho();
            echoScheduled = false;
            wasMoving = false;
        }
    }

    private static void playEcho() {
        if (world == null || player == null) return;
        if (lastFootstepSound.isEmpty()) return;

        // Play the echo sound slightly offset from player's position
        // This makes it seem like footsteps from behind or to the side
        double offsetX = (Math.random() - 0.5) * 4; // +/- 2 blocks
        double offsetZ = (Math.random() - 0.5) * 4;

        double echoX = player.posX + offsetX;
        double echoY = player.posY;
        double echoZ = player.posZ + offsetZ;

        // Play sound at the echo position
        world.playSoundEffect(echoX, echoY, echoZ, lastFootstepSound, 0.5F, 0.8F + (float)(Math.random() * 0.4));

        // Sometimes add a second echo from another direction
        if (Math.random() < 0.3) {
            double offsetX2 = (Math.random() - 0.5) * 6;
            double offsetZ2 = (Math.random() - 0.5) * 6;

            world.playSoundEffect(player.posX + offsetX2, player.posY, player.posZ + offsetZ2,
                lastFootstepSound, 0.4F, 0.7F + (float)(Math.random() * 0.4));
        }
    }

    private static String getCurrentFootstepSound() {
        if (player == null) return "step.wood";

        // Get the block player is standing on
        int blockX = (int)player.posX;
        int blockY = (int)(player.posY - 0.5);
        int blockZ = (int)player.posZ;

        int blockId = 0;
        if (world != null) {
            blockId = world.getBlockId(blockX, blockY, blockZ);
        }

        // Determine appropriate footstep sound based on block type
        if (blockId == Block.stone.blockID ||
            blockId == Block.cobblestone.blockID ||
            blockId == Block.stoneBrick.blockID ||
            blockId == Block.brick.blockID ||
            blockId == Block.sandStone.blockID) {
            return "step.stone";
        } else if (blockId == Block.wood.blockID ||
                   blockId == Block.planks.blockID ||
                   blockId == Block.stairCompactPlanks.blockID ||
                   blockId == Block.doorWood.blockID) {
            return "step.wood";
        } else if (blockId == Block.grass.blockID ||
                   blockId == Block.dirt.blockID ||
                   blockId == Block.sand.blockID) {
            return "step.gravel";
        } else if (blockId == Block.glass.blockID) {
            return "step.stone";
        } else if (blockId == Block.oreGold.blockID ||
                   blockId == Block.oreIron.blockID ||
                   blockId == Block.oreCoal.blockID ||
                   blockId == Block.blockGold.blockID ||
                   blockId == Block.blockSteel.blockID) {
            return "step.stone";
        } else {
            return "step.stone";
        }
    }

    /**
     * Force an immediate echo (for other horror effects to trigger).
     */
    public static void triggerImmediateEcho() {
        playEcho();
    }
}