package net.minecraft.src;

/**
 * False background sounds - gradually lowers pitch of ambient.cave
 * sounds and mixes in low-frequency hum when inventory opens.
 */
public class FalseBackgroundSounds {
    private static float originalPitch = 1.0F;
    private static float currentPitch = 1.0F;
    private static boolean inventoryOpen = false;
    private static long inventoryOpenTime = 0;
    private static final long HUM_DURATION = 5000L; // 5 seconds after closing

    public static void onInventoryOpen() {
        inventoryOpen = true;
        inventoryOpenTime = System.currentTimeMillis();
        // Lower pitch of cave ambient sounds
        currentPitch = 0.7F + (float)Math.random() * 0.2F; // 0.7-0.9 pitch
        playLowHum();
    }

    public static void onInventoryClose() {
        inventoryOpen = false;
        // Keep low hum for some time after closing
    }

    public static void tick() {
        // Gradually restore pitch
        if (currentPitch < 1.0F && !inventoryOpen) {
            currentPitch += 0.01F;
            if (currentPitch > 1.0F) currentPitch = 1.0F;
        }

        // If inventory was recently closed, keep hum playing
        if (!inventoryOpen) {
            long elapsed = System.currentTimeMillis() - inventoryOpenTime;
            if (elapsed < HUM_DURATION) {
                // Keep playing low hum
                if (Math.random() < 0.05) playLowHum();
            }
        }
    }

    private static void playLowHum() {
        // Play low-frequency hum sound
        // This uses a custom sound or modified ambient sound
        // Simplified: play ambient.cave at lower pitch
        // Actual implementation would need sound file modifications
    }

    public static float getAmbientPitch() {
        return currentPitch;
    }
}