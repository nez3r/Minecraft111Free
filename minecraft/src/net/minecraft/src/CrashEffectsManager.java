package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import java.util.Random;

/**
 * Manages visual effects during the final crash sequence
 */
public class CrashEffectsManager {
    private static Random rand = new Random();
    private static boolean effectsActive = false;
    private static long effectsStartTime = 0;

    // Window shake state
    private static boolean isShaking = false;
    private static int originalWindowX = 0;
    private static int originalWindowY = 0;

    // Flash state
    private static boolean isFlashing = false;
    private static int flashColor = 0xFFFFFFFF;

    // Message state
    private static String[] crashMessages = {
        "GET OUT",
        "LEAVE",
        "WHY ARE YOU HERE",
        "THIS ISN'T FOR YOU",
        "STOP",
        "GO AWAY",
        "YOU SHOULDN'T BE HERE",
        "LEAVE NOW"
    };
    private static int currentMessageIndex = 0;
    private static float messageX = 0;
    private static float messageY = 0;
    private static int messageColor = 0xFFFF0000;
    private static float messageScale = 1.0F;

    /**
     * Start crash effects
     */
    public static void startEffects() {
        if (effectsActive) {
            return;
        }

        effectsActive = true;
        effectsStartTime = System.currentTimeMillis();

        // Start window shake
        startWindowShake();

        // Start flashing
        isFlashing = true;

        // Block pause and exit
        HorrorState.exitTrapActive = true;
    }

    /**
     * Stop all effects
     */
    public static void stopEffects() {
        effectsActive = false;
        isShaking = false;
        isFlashing = false;

        try {
            if (originalWindowX != 0 || originalWindowY != 0) {
                Display.setLocation(originalWindowX, originalWindowY);
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    /**
     * Check if effects are active
     */
    public static boolean isActive() {
        return effectsActive;
    }

    /**
     * Update effects
     */
    public static void update() {
        if (!effectsActive) {
            return;
        }

        long elapsed = System.currentTimeMillis() - effectsStartTime;

        // Update message position and properties
        messageX = rand.nextFloat() * 0.8F + 0.1F; // 10-90% of screen width
        messageY = rand.nextFloat() * 0.8F + 0.1F; // 10-90% of screen height
        messageScale = 2.0F + rand.nextFloat() * 3.0F; // 2-5x scale

        // Cycle through messages
        if (elapsed / 100 > currentMessageIndex) {
            currentMessageIndex = (int)(elapsed / 100);
        }

        // Random color (red spectrum)
        int r = 200 + rand.nextInt(56); // 200-255
        int g = rand.nextInt(100); // 0-99
        int b = rand.nextInt(100); // 0-99
        messageColor = 0xFF000000 | (r << 16) | (g << 8) | b;

        // Update flash color
        if (rand.nextBoolean()) {
            flashColor = 0xFFFF0000; // Red
        } else {
            flashColor = 0xFF000000; // Black
        }

        // Update window shake
        updateWindowShake();
    }

    /**
     * Get current message to display
     */
    public static String getCurrentMessage() {
        if (!effectsActive) {
            return "";
        }
        return crashMessages[currentMessageIndex % crashMessages.length];
    }

    /**
     * Get message X position (0.0 - 1.0)
     */
    public static float getMessageX() {
        return messageX;
    }

    /**
     * Get message Y position (0.0 - 1.0)
     */
    public static float getMessageY() {
        return messageY;
    }

    /**
     * Get message color
     */
    public static int getMessageColor() {
        return messageColor;
    }

    /**
     * Get message scale
     */
    public static float getMessageScale() {
        return messageScale;
    }

    /**
     * Check if should render flash
     */
    public static boolean shouldFlash() {
        return isFlashing && rand.nextInt(3) == 0; // 33% chance each frame
    }

    /**
     * Get flash color
     */
    public static int getFlashColor() {
        return flashColor;
    }

    /**
     * Start window shake
     */
    private static void startWindowShake() {
        if (isShaking) {
            return;
        }

        isShaking = true;

        try {
            originalWindowX = Display.getX();
            originalWindowY = Display.getY();
        } catch (Exception e) {
            // Ignore
        }
    }

    /**
     * Update window shake
     */
    private static void updateWindowShake() {
        if (!isShaking) {
            return;
        }

        try {
            // Violent shake: -30 to +30 pixels
            int offsetX = rand.nextInt(61) - 30;
            int offsetY = rand.nextInt(61) - 30;
            Display.setLocation(originalWindowX + offsetX, originalWindowY + offsetY);
        } catch (Exception e) {
            // Ignore
        }
    }
}
