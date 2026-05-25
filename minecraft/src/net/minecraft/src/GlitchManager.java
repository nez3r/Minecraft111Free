package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.util.Random;

/**
 * Manager for all glitch effects in the horror mod
 */
public class GlitchManager {
    private static Random rand = new Random();

    // Window shake state
    private static boolean isShaking = false;
    private static long shakeStartTime = 0;
    private static int originalWindowX = 0;
    private static int originalWindowY = 0;

    // Cursor glitch state
    private static boolean cursorGlitching = false;
    private static long cursorGlitchStartTime = 0;
    private static double cursorDriftX = 0;
    private static double cursorDriftY = 0;

    /**
     * Update all glitch effects - call every tick
     */
    public static void update() {
        Minecraft mc = Minecraft.theMinecraft;
        if (mc == null) {
            return;
        }

        HorrorState.updatePlayTime();
        long currentTime = System.currentTimeMillis();

        // Initialize lastGlitchTime on first update to prevent immediate glitch
        if (HorrorState.lastGlitchTime == 0) {
            HorrorState.lastGlitchTime = currentTime;
        }

        // Stop all glitches after final glitch triggered
        if (HorrorState.finalGlitchTriggered) {
            updateCursorGlitch();
            updateWindowShake();
            return;
        }

        // SEPARATE GLITCH EVENTS:
        // 4 minutes - window shake
        long windowShakeTime = (long)(4 * 60 * 1000 / HorrorState.horrorSpeedMultiplier);
        // 8 minutes - day/night flicker
        long dayNightFlickerTime = (long)(8 * 60 * 1000 / HorrorState.horrorSpeedMultiplier);
        // 15 minutes - cursor glitch
        long cursorGlitchTime = (long)(15 * 60 * 1000 / HorrorState.horrorSpeedMultiplier);
        // 17 minutes - tunnel generation (final glitch)
        long tunnelGenerationTime = (long)(17 * 60 * 1000 / HorrorState.horrorSpeedMultiplier);

        // Trigger window shake at 4 minutes
        if (HorrorState.glitchCount == 0 && currentTime - HorrorState.lastGlitchTime > windowShakeTime) {
            startWindowShake();
            HorrorState.glitchCount = 1;
            HorrorState.lastGlitchTime = currentTime;
        }

        // Trigger day/night flicker at 8 minutes
        if (HorrorState.glitchCount == 1 && currentTime - HorrorState.lastGlitchTime > (dayNightFlickerTime - windowShakeTime)) {
            triggerDayNightFlicker();
            HorrorState.glitchCount = 2;
            HorrorState.lastGlitchTime = currentTime;
        }

        // Trigger cursor glitch at 15 minutes
        if (HorrorState.glitchCount == 2 && currentTime - HorrorState.lastGlitchTime > (cursorGlitchTime - dayNightFlickerTime)) {
            startCursorGlitch();
            HorrorState.glitchCount = 3;
            HorrorState.lastGlitchTime = currentTime;
        }

        // Trigger tunnel generation at 17 minutes (final glitch)
        if (HorrorState.glitchCount == 3 && !HorrorState.finalGlitchTriggered) {
            if (currentTime - HorrorState.lastGlitchTime > (tunnelGenerationTime - cursorGlitchTime)) {
                triggerFinalGlitch();
                HorrorState.finalGlitchTriggered = true;
            }
        }

        // Update ongoing effects
        updateDayNightFlicker();
        updateCursorGlitch();
        updateWindowShake();
    }

    // Day/night flicker state
    private static boolean dayNightFlickering = false;
    private static long flickerStartTime = 0;
    private static int flickerCount = 0;
    private static long originalWorldTime = 0;
    private static boolean startedAtDay = false;

    /**
     * Start day/night flicker effect
     */
    private static void triggerDayNightFlicker() {
        final Minecraft mc = Minecraft.theMinecraft;
        if (mc == null || mc.theWorld == null || mc.theWorld.multiplayerWorld) {
            return;
        }

        dayNightFlickering = true;
        flickerStartTime = System.currentTimeMillis();
        flickerCount = 0;
        originalWorldTime = mc.theWorld.getWorldTime();
        startedAtDay = (originalWorldTime % 24000) < 12000;
    }

    /**
     * Update day/night flicker effect
     */
    private static void updateDayNightFlicker() {
        if (!dayNightFlickering) {
            return;
        }

        Minecraft mc = Minecraft.theMinecraft;
        if (mc == null || mc.theWorld == null) {
            dayNightFlickering = false;
            return;
        }

        long elapsed = System.currentTimeMillis() - flickerStartTime;
        if (elapsed > 3000) { // 3 seconds total
            dayNightFlickering = false;
            mc.theWorld.setWorldTime(originalWorldTime);
            return;
        }

        // Toggle every 100ms
        if (elapsed / 100 > flickerCount) {
            flickerCount++;
            if (flickerCount % 2 == 0) {
                mc.theWorld.setWorldTime(startedAtDay ? 18000 : 6000);
            } else {
                mc.theWorld.setWorldTime(startedAtDay ? 6000 : 18000);
            }
        }
    }

    /**
     * Start cursor drift effect
     */
    private static void startCursorGlitch() {
        cursorGlitching = true;
        cursorGlitchStartTime = System.currentTimeMillis();

        // Random drift direction
        double angle = rand.nextDouble() * Math.PI * 2;
        double speed = 0.5 + rand.nextDouble() * 1.0;
        cursorDriftX = Math.cos(angle) * speed;
        cursorDriftY = Math.sin(angle) * speed;
    }

    /**
     * Update cursor glitch effect
     */
    private static void updateCursorGlitch() {
        if (!cursorGlitching) {
            return;
        }

        Minecraft mc = Minecraft.theMinecraft;
        if (mc == null) {
            return;
        }

        long elapsed = System.currentTimeMillis() - cursorGlitchStartTime;
        if (elapsed > (2000 + rand.nextInt(3000))) { // 2-5 seconds
            cursorGlitching = false;
            return;
        }

        // Apply drift to mouse
        if (mc.inGameHasFocus && mc.thePlayer != null) {
            mc.thePlayer.rotationYaw += (float)cursorDriftX;
            mc.thePlayer.rotationPitch += (float)cursorDriftY;

            // Clamp pitch
            if (mc.thePlayer.rotationPitch > 90.0F) {
                mc.thePlayer.rotationPitch = 90.0F;
            }
            if (mc.thePlayer.rotationPitch < -90.0F) {
                mc.thePlayer.rotationPitch = -90.0F;
            }
        }
    }

    /**
     * Start window shake effect
     */
    private static void startWindowShake() {
        if (isShaking) {
            return;
        }

        isShaking = true;
        shakeStartTime = System.currentTimeMillis();

        try {
            originalWindowX = Display.getX();
            originalWindowY = Display.getY();
        } catch (Exception e) {
            // Ignore if can't get window position
        }
    }

    /**
     * Update window shake effect
     */
    private static void updateWindowShake() {
        if (!isShaking) {
            return;
        }

        long elapsed = System.currentTimeMillis() - shakeStartTime;
        if (elapsed > 5000) { // 5 seconds
            isShaking = false;
            // Restore original position
            try {
                Display.setLocation(originalWindowX, originalWindowY);
            } catch (Exception e) {
                // Ignore
            }
            return;
        }

        // Shake window
        try {
            int offsetX = rand.nextInt(11) - 5; // -5 to +5
            int offsetY = rand.nextInt(11) - 5;
            Display.setLocation(originalWindowX + offsetX, originalWindowY + offsetY);
        } catch (Exception e) {
            // Ignore if can't set window position
        }
    }

    /**
     * Trigger violent window shake (for attack sequence)
     */
    public static void triggerViolentShake() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    int origX = Display.getX();
                    int origY = Display.getY();

                    for (int i = 0; i < 500; i++) { // 10 seconds at 50Hz
                        int offsetX = rand.nextInt(41) - 20; // -20 to +20
                        int offsetY = rand.nextInt(41) - 20;
                        Display.setLocation(origX + offsetX, origY + offsetY);

                        Thread.sleep(20); // 50Hz
                    }

                    Display.setLocation(origX, origY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Trigger final glitch event
     */
    private static void triggerFinalGlitch() {
        final Minecraft mc = Minecraft.theMinecraft;
        if (mc == null) {
            return;
        }

        // Trigger effects at maximum intensity (but skip violent shake to avoid freeze)
        triggerDayNightFlicker();
        startCursorGlitch();

        // Play ominous sound
        if (mc.sndManager != null) {
            mc.sndManager.playSoundFX("portal.portal", 2.0F, 0.3F);
        }

        // Generate bedrock tunnel near player
        if (mc.thePlayer != null && !HorrorState.tunnelGenerated) {
            WorldGenBedrockTunnel.generateNearPlayer(mc.thePlayer);
        }
    }

    /**
     * Stop all glitch effects
     */
    public static void stopAll() {
        isShaking = false;
        cursorGlitching = false;

        try {
            if (originalWindowX != 0 || originalWindowY != 0) {
                Display.setLocation(originalWindowX, originalWindowY);
            }
        } catch (Exception e) {
            // Ignore
        }
    }
}
