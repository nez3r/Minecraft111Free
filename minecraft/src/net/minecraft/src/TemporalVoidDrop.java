package net.minecraft.src;

/**
 * TemporalVoidDrop - безопасное падение игрока (4 сек)
 */
public class TemporalVoidDrop {
    private static boolean isActive = false;
    private static double origX, origY, origZ;
    private static float origYaw, origPitch;
    private static long dropEnd = 0L;

    private static EntityPlayer player;

    public static void setPlayer(EntityPlayer p) { player = p; }

    public static void trigger() {
        if (isActive) return;
        if (player == null) return;
        if (player.worldObj != null && player.worldObj.multiplayerWorld) {
            DynamicWindowTitle.triggerTitle(player, "FALLING...", 2500L);
            return;
        }

        origX = player.posX;
        origY = player.posY;
        origZ = player.posZ;
        origYaw = player.rotationYaw;
        origPitch = player.rotationPitch;

        player.setPosition(origX, 90.0D, origZ);
        player.motionY = -0.35D;
        player.fallDistance = 0.0F;
        isActive = true;
        dropEnd = System.currentTimeMillis() + 4000L;
    }

    public static void onTick() {
        if (!isActive) return;
        player.fallDistance = 0.0F;
        if (System.currentTimeMillis() >= dropEnd) {
            player.fallDistance = 0.0F;
            isActive = false;
        }
    }

    public static void reset() {
        isActive = false;
        dropEnd = 0L;
        if (player != null) {
            player.fallDistance = 0.0F;
        }
    }
}