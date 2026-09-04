package net.minecraft.src;

/**
 * Fake frame freeze - freezes render for 1-2 seconds when breaking certain blocks,
 * then displaces player several meters in a random direction.
 */
public class FakeFrameFreeze {
    private static boolean freezeActive = false;
    private static long freezeStartTime = 0;
    private static final long FREEZE_DURATION = 1500L; // 1.5 seconds
    private static double targetX, targetY, targetZ;
    private static int freezeCounter = 0;

    public static boolean isFreezeActive() {
        if (!freezeActive) return false;

        long elapsed = System.currentTimeMillis() - freezeStartTime;
        if (elapsed >= FREEZE_DURATION) {
            freezeActive = false;
            return false;
        }
        return true;
    }

    /**
     * Trigger freeze when player breaks certain blocks.
     * @param blockId The block being broken
     * @param player The player breaking the block
     */
    public static void triggerOnBlockBreak(int blockId, EntityPlayer player) {
        if (HorrorState.safeMode) return;
        if (freezeActive) return;

        // Only trigger on specific blocks (ores, special blocks)
        if (blockId == Block.oreDiamond.blockID ||
            blockId == Block.oreGold.blockID ||
            blockId == Block.oreIron.blockID ||
            blockId == Block.oreLapis.blockID ||
            blockId == Block.oreRedstone.blockID ||
            blockId == Block.blockGold.blockID ||
            blockId == Block.blockSteel.blockID ||
            blockId == Block.blockDiamond.blockID) {

            // Random chance to trigger (not always)
            if (Math.random() < 0.4) {
                activateFreeze(player);
            }
        }
    }

    private static void activateFreeze(EntityPlayer player) {
        freezeActive = true;
        freezeStartTime = System.currentTimeMillis();
        freezeCounter++;

        // Play scary sound
        if (player.worldObj != null) {
            player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "random.glass", 1.0F, 0.5F);
        }

        // Calculate displacement target
        double angle = Math.random() * Math.PI * 2;
        double distance = 5.0D + Math.random() * 10.0D; // 5-15 blocks

        targetX = player.posX + Math.cos(angle) * distance;
        targetY = player.posY + (Math.random() - 0.5) * 4; // Slight Y change
        targetZ = player.posZ + Math.sin(angle) * distance;

        // Snap to ground level
        if (player.worldObj != null) {
            targetY = player.worldObj.getHeightValue((int)targetX, (int)targetZ) + 1;
        }
    }

    /**
     * Get the displacement target X coordinate.
     */
    public static double getTargetX() {
        return targetX;
    }

    /**
     * Get the displacement target Y coordinate.
     */
    public static double getTargetY() {
        return targetY;
    }

    /**
     * Get the displacement target Z coordinate.
     */
    public static double getTargetZ() {
        return targetZ;
    }

    /**
     * Apply displacement to player after freeze ends.
     */
    public static void applyDisplacement(EntityPlayer player) {
        if (!freezeActive) return;

        long elapsed = System.currentTimeMillis() - freezeStartTime;
        if (elapsed < FREEZE_DURATION) return;

        // Apply displacement once
        if (targetX != 0 || targetZ != 0) {
            player.setPosition(targetX, targetY, targetZ);
            targetX = 0;
            targetY = 0;
            targetZ = 0;
        }
    }
}