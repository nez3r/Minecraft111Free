package net.minecraft.src;

import net.minecraft.client.Minecraft;
import java.util.Random;

/**
 * Manager for screamer effects
 */
public class ScreamerManager {
    private static Random rand = new Random();
    private static boolean screamerActive = false;
    private static long screamerStartTime = 0;

    /**
     * Check and trigger screamers
     */
    public static void update() {
        if (!HorrorState.canTriggerScreamer()) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Apply speed multiplier to timing
        long screamerInterval = (long)((8 + rand.nextInt(7)) * 60 * 1000 / HorrorState.horrorSpeedMultiplier);

        // Check if enough time has passed since last screamer
        if (currentTime - HorrorState.lastScreamerTime > screamerInterval) {
            triggerRandomScreamer();
            HorrorState.lastScreamerTime = currentTime;
            HorrorState.screamerCount++;
        }
    }

    /**
     * Trigger a random screamer type
     */
    private static void triggerRandomScreamer() {
        Minecraft mc = Minecraft.theMinecraft;
        if (mc == null || mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        // Increase intensity based on screamer count
        int screamerType = rand.nextInt(4);

        // First screamer is always mild (audio only)
        if (HorrorState.screamerCount == 0) {
            screamerType = 0;
        }

        switch (screamerType) {
            case 0:
                triggerAudioScreamer();
                break;
            case 1:
                triggerVisualScreamer();
                break;
            case 2:
                triggerEntityScreamer();
                break;
            case 3:
                triggerCombinedScreamer();
                break;
        }
    }

    /**
     * Audio screamer - loud distorted sound
     */
    private static void triggerAudioScreamer() {
        final Minecraft mc = Minecraft.theMinecraft;
        if (mc == null) {
            return;
        }

        try {
            // Play loud cave sound or damage sound at max volume
            mc.sndManager.playSoundFX("damage.fallbig", 2.0F, 0.5F);

            // Play multiple overlapping sounds for distortion effect
            new Thread(new Runnable() {
                public void run() {
                    try {
                        for (int i = 0; i < 5; i++) {
                            if (mc.sndManager != null) {
                                mc.sndManager.playSoundFX("random.explode", 2.0F, 0.3F + rand.nextFloat() * 0.4F);
                            }
                            Thread.sleep(50);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Visual screamer - flash screen red/black
     */
    private static void triggerVisualScreamer() {
        screamerActive = true;
        screamerStartTime = System.currentTimeMillis();

        new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(100);
                    }
                    screamerActive = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Also play sound
        triggerAudioScreamer();
    }

    /**
     * Entity screamer - Entity404 appears close for 1 second
     */
    private static void triggerEntityScreamer() {
        final Minecraft mc = Minecraft.theMinecraft;
        if (mc == null || HorrorState.stalkerEntity == null || mc.thePlayer == null) {
            triggerAudioScreamer();
            return;
        }

        // Teleport Entity404 very close to player
        final double originalX = HorrorState.stalkerEntity.posX;
        final double originalY = HorrorState.stalkerEntity.posY;
        final double originalZ = HorrorState.stalkerEntity.posZ;

        // Position 5 blocks in front of player
        double angle = Math.toRadians(mc.thePlayer.rotationYaw);
        double offsetX = -Math.sin(angle) * 5.0;
        double offsetZ = Math.cos(angle) * 5.0;

        HorrorState.stalkerEntity.setPosition(
            mc.thePlayer.posX + offsetX,
            mc.thePlayer.posY,
            mc.thePlayer.posZ + offsetZ
        );

        // Play sound
        triggerAudioScreamer();

        // Teleport back after 1 second
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    if (HorrorState.stalkerEntity != null) {
                        HorrorState.stalkerEntity.setPosition(originalX, originalY, originalZ);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Combined screamer - all effects at once
     */
    private static void triggerCombinedScreamer() {
        triggerVisualScreamer();

        // Delay entity screamer slightly
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(200);
                    triggerEntityScreamer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Check if screamer is currently active
     */
    public static boolean isScreamerActive() {
        return screamerActive;
    }

    /**
     * Get screamer flash color (for rendering)
     */
    public static int getScreamerFlashColor() {
        if (!screamerActive) {
            return 0;
        }

        long elapsed = System.currentTimeMillis() - screamerStartTime;
        int flashIndex = (int)(elapsed / 100) % 2;

        // Alternate between red and black
        if (flashIndex == 0) {
            return 0xFFFF0000; // Red
        } else {
            return 0xFF000000; // Black
        }
    }
}
