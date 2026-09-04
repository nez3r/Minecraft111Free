package net.minecraft.src;

import java.util.Random;

/**
 * Visual glitch effects that occur when player turns camera 180 degrees behind them.
 * Blocks randomly change: torches turn off/red, leaves drop, stone becomes mossy.
 */
public class VisualGlitch {
    private static final long GLITCH_INTERVAL = 200L; // 10 ticks
    private static final int BLOCKS_AFFECTED = 3; // Number of blocks to randomly change
    private static final int RANGE = 8; // Block range

    private static Random rand = new Random();
    private static World world;
    private static EntityPlayer player;
    private static boolean active = false;
    private static long lastCheckTime = 0;
    private static float lastYaw = 0;

    public static void init() {
        active = true;
    }

    public static void setWorld(World w, EntityPlayer p) {
        world = w;
        player = p;
    }

    public static void updatePosition(double x, double y, double z) {
        if (player != null) {
            player.posX = x;
            player.posY = y;
            player.posZ = z;
        }
    }

    public static void checkBehindYouGlitch() {
        if (world == null || player == null) return;
        if (HorrorState.safeMode) return;

        // Throttle checks
        if (System.currentTimeMillis() - lastCheckTime < GLITCH_INTERVAL) return;
        lastCheckTime = System.currentTimeMillis();

        // Calculate player's yaw and check if looking behind
        float yaw = player.rotationYaw;
        float yawDelta = Math.abs(yaw - lastYaw);
        lastYaw = yaw;

        // Detect a rapid 180-degree turn (significant yaw change)
        boolean rapidTurn = yawDelta > 90 && yawDelta < 270;

        if (!rapidTurn) {
            return;
        }

        // Player just turned 180 - glitch blocks in radius
        applyBlockGlitches();
    }

    private static void applyBlockGlitches() {
        int startX = (int)player.posX - RANGE;
        int startY = (int)player.posY - RANGE;
        int startZ = (int)player.posZ - RANGE;

        for (int i = 0; i < 5; i++) {
            int targetX = startX + rand.nextInt(RANGE * 2);
            int targetY = startY + rand.nextInt(RANGE * 2);
            int targetZ = startZ + rand.nextInt(RANGE * 2);

            int blockId = world.getBlockId(targetX, targetY, targetZ);
            if (blockId == 0) continue;

            // Modify certain block types
            if (blockId == Block.torchWood.blockID) {
                // Torches - turn off or red
                if (rand.nextBoolean()) {
                    world.setBlock(targetX, targetY, targetZ, 0);
                } else {
                    world.setBlock(targetX, targetY, targetZ, Block.torchRed.blockID);
                }
            } else if (blockId == Block.leaves.blockID) {
                // Remove leaf block
                world.setBlock(targetX, targetY, targetZ, 0);
            } else if (blockId == Block.stoneBrick.blockID) {
                // Change to mossy variant - set to air as placeholder
                world.setBlock(targetX, targetY, targetZ, 0);
            } else if (blockId == Block.cobblestone.blockID) {
                // Stone -> mossy cobblestone (use cobblestone as placeholder)
                // Set to air to simulate change
                if (rand.nextBoolean()) {
                    world.setBlock(targetX, targetY, targetZ, 0);
                }
            }
        }
    }
}