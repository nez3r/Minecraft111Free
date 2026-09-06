package net.minecraft.src;

/**
 * TemporalVoidDrop - Мгновенный выброс в пустоту (2.5 сек)
 */
public class TemporalVoidDrop {
    private static boolean isActive = false;
    private static double origX, origY, origZ;
    private static float origYaw, origPitch;
    private static int ticksRemaining = 0;

    private static EntityPlayer player;

    public static void setPlayer(EntityPlayer p) { player = p; }

    public static void trigger() {
        if (isActive) return;
        if (player == null) return;

        origX = player.posX;
        origY = player.posY;
        origZ = player.posZ;
        origYaw = player.rotationYaw;
        origPitch = player.rotationPitch;

        player.setPosition(origX, 500.0D, origZ);
        // Camera looks down (pitch = 90)
        player.rotationPitch = 90.0F;
        isActive = true;
        ticksRemaining = 50; // 2.5 сек (20 тиков = 1 сек)
    }

    public static void onTick() {
        if (!isActive) return;
        ticksRemaining--;
        if (ticksRemaining <= 0) {
            player.setPositionAndRotation(origX, origY, origZ, origYaw, origPitch);
            player.worldObj.playSoundAtEntity(player, "damage.fallbig", 1.0F, 0.5F);
            isActive = false;
        }
    }
}