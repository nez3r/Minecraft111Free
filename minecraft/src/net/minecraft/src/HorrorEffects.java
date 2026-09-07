package net.minecraft.src;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

/**
 * Централизованный класс для всех игровых хоррор-эффектов
 * (кроме нативных Unknown.dll эффектов)
 */
public class HorrorEffects {
    private static Random rand = new Random();

    // =============================================================================
    // 1. FOOTSTEP ECHO - Эхо шагов
    // =============================================================================
    private static long lastFootstepTime = 0;
    private static List<DelayedSound> delayedFootsteps = new ArrayList<DelayedSound>();

    public static void triggerFootstepEcho(EntityPlayer player) {
        if (player == null) return;


        try {
            // Задержанный звук шага (0.5-1 секунда)
            int delay = 500 + rand.nextInt(500);
            delayedFootsteps.add(new DelayedSound(
                player.worldObj,
                player.posX, player.posY, player.posZ,
                "step.stone",
                delay
            ));
        } catch (Exception e) {
        }
    }

    public static void tickFootstepEcho() {
        try {
            long currentTime = System.currentTimeMillis();
            List<DelayedSound> toRemove = new ArrayList<DelayedSound>();

            for (DelayedSound sound : delayedFootsteps) {
                if (currentTime >= sound.triggerTime) {
                    if (sound.world != null) {
                        sound.world.playSoundEffect(
                            sound.x, sound.y, sound.z,
                            sound.soundName, 1.0F, 1.0F
                        );
                    }
                    toRemove.add(sound);
                }
            }

            delayedFootsteps.removeAll(toRemove);
        } catch (Exception e) {}
    }

    // =============================================================================
    // 2. INVENTORY GLITCH - Глитч инвентаря
    // =============================================================================
    private static boolean inventoryGlitchActive = false;
    private static long inventoryGlitchEnd = 0;

    public static void triggerInventoryGlitchMinor() {
        inventoryGlitchActive = true;
        inventoryGlitchEnd = System.currentTimeMillis() + 3000; // 3 секунды
    }

    public static void triggerInventoryGlitchMajor() {
        inventoryGlitchActive = true;
        inventoryGlitchEnd = System.currentTimeMillis() + 5000; // 5 секунд
    }

    public static boolean isInventoryGlitchActive() {
        if (System.currentTimeMillis() > inventoryGlitchEnd) {
            inventoryGlitchActive = false;
        }
        return inventoryGlitchActive;
    }

    public static String glitchItemName(String originalName) {
        if (!isInventoryGlitchActive()) return originalName;

        // Рандомный глитч текста
        if (rand.nextInt(3) == 0) {
            String[] glitchChars = {"█", "▓", "▒", "░", "?", "�", "�", "�"};
            StringBuilder glitched = new StringBuilder();
            for (int i = 0; i < originalName.length(); i++) {
                if (rand.nextInt(4) == 0) {
                    glitched.append(glitchChars[rand.nextInt(glitchChars.length)]);
                } else {
                    glitched.append(originalName.charAt(i));
                }
            }
            return glitched.toString();
        }
        return originalName;
    }

    // =============================================================================
    // 3. BACKGROUND SOUNDS - Фоновые звуки
    // =============================================================================
    private static long lastAmbientSound = 0;
    private static long lastLowHum = 0;

    public static void playAmbientSound(World world, EntityPlayer player) {
        if (world == null || player == null) return;

        lastAmbientSound = System.currentTimeMillis();
        DynamicWindowTitle.triggerTitle(player, "...", 3000L);
    }

    public static void playLowHum(World world, EntityPlayer player) {
        if (world == null || player == null) return;
        DynamicWindowTitle.triggerTitle(player, "I hear you", 3000L);
    }

    public static void triggerHeartbeat(EntityPlayer player) {
        if (player == null || player.worldObj == null) return;
        Minecraft mc = Minecraft.theMinecraft;
        DynamicWindowTitle.triggerTitle(player, "HEARTBEAT", 6000L);
        player.addChatMessage("\u00a74\u00a7lHEARTBEAT");
        player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ,
            "random.click", 1.0F, 0.45F);
        player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ,
            "random.click", 1.0F, 0.35F);
        if (mc != null && mc.sndManager != null) {
            mc.sndManager.playSoundFX("random.click", 1.0F, 0.45F);
        }
    }

    // =============================================================================
    // 4. DYNAMIC FOG - Динамический туман
    // =============================================================================
    private static float currentFogMultiplier = 1.0F;
    private static long fogEffectEnd = 0;

    public static void increaseFogSlightly() {
        currentFogMultiplier = 0.7F;
        fogEffectEnd = System.currentTimeMillis() + 5000;
    }

    public static void triggerModerateFog() {
        currentFogMultiplier = 0.4F;
        fogEffectEnd = System.currentTimeMillis() + 8000;
    }

    public static void triggerHeavyFog() {
        currentFogMultiplier = 0.15F;
        fogEffectEnd = System.currentTimeMillis() + 10000;
    }

    public static float getFogMultiplier() {
        if (System.currentTimeMillis() > fogEffectEnd) {
            currentFogMultiplier = 1.0F;
        }
        return currentFogMultiplier;
    }

    public static void resetFog() {
        currentFogMultiplier = 1.0F;
        fogEffectEnd = 0;
    }

    // =============================================================================
    // 5. BLOOD TIME CYCLE - Кровавое время
    // =============================================================================
    private static boolean bloodTimeActive = false;
    private static long bloodTimeEnd = 0;
    private static long bloodTimeFrozenAt = 18000; // Полночь

    public static void triggerBloodTime() {
        bloodTimeActive = true;
        bloodTimeEnd = System.currentTimeMillis() + 15000; // 15 секунд
    }

    // =============================================================================
    // 8. INVERTED CAMERA - Переворот мира (4 секунды)
    // =============================================================================
    private static boolean invertedCameraActive = false;
    private static long invertedCameraEnd = 0;

    public static void triggerInvertedCamera() {
        invertedCameraActive = true;
        invertedCameraEnd = System.currentTimeMillis() + 4000;
    }

    public static boolean isInvertedCameraActive() {
        if (System.currentTimeMillis() > invertedCameraEnd) {
            invertedCameraActive = false;
        }
        return invertedCameraActive;
    }

    public static void resetInvertedCamera() {
        invertedCameraActive = false;
        invertedCameraEnd = 0;
    }

    public static boolean isBloodTimeActive() {
        if (System.currentTimeMillis() > bloodTimeEnd) {
            bloodTimeActive = false;
        }
        return bloodTimeActive;
    }

    public static long getBloodTimeFrozen() {
        return bloodTimeFrozenAt;
    }

    public static void resetBloodTime() {
        bloodTimeActive = false;
        bloodTimeEnd = 0;
    }

    // =============================================================================
    // 6. VISUAL GLITCH "Behind You" - Глитч "За тобой"
    // =============================================================================
    public static void checkBehindYouGlitch(World world, EntityPlayer player) {
        if (world == null || player == null) return;


        // This is a perception effect: never rewrite player-owned terrain.
        double angle = Math.atan2(player.posZ - player.prevPosZ,
            player.posX - player.prevPosX);
        double echoX = player.posX - Math.cos(angle) * 4.0D;
        double echoZ = player.posZ - Math.sin(angle) * 4.0D;
        world.playSoundEffect(echoX, player.posY, echoZ, "ambient.cave.cave",
            0.35F, 0.55F + rand.nextFloat() * 0.2F);
    }

    // =============================================================================
    // 7. PHANTOM OBSERVER - Спавн наблюдателя
    // =============================================================================
    public static void spawnPhantomObserver(World world, EntityPlayer player) {
        if (world == null || player == null) return;


        try {
            PhantomObserver.spawnNearPlayer(world, player);
        } catch (Exception e) {
        }
    }

    // =============================================================================
    // 8. MIRROR DOUBLE - Спавн двойника
    // =============================================================================
    public static void spawnMirrorDouble(World world, EntityPlayer player) {
        if (world == null || player == null) return;


        try {
            MirrorDouble.spawnDouble(world, player);
        } catch (Exception e) {
        }
    }

    // =============================================================================
    // 9. PARTISAN INTERFERENCE - Партизанская интерференция
    // =============================================================================
    public static void triggerPartisanInterference(World world, EntityPlayer player) {
        if (world == null || player == null) return;


        try {
            PartisanInterference.triggerInterference();
        } catch (Exception e) {
        }
    }

    // =============================================================================
    // 10. DISAPPEARING ANOMALIES - Исчезающие аномалии
    // =============================================================================
    public static void generateDisappearingAnomaly(World world, EntityPlayer player) {
        if (world == null || player == null) return;


        try {
            DisappearingAnomalies.generateAnomaly();
        } catch (Exception e) {
        }
    }

    // =============================================================================
    // 11. DYNAMIC WINDOW TITLE - Динамический заголовок окна
    // =============================================================================
    public static void triggerGlitchedWindowTitle(EntityPlayer player) {
        if (player == null) return;


        try {
            DynamicWindowTitle.triggerGlitchedTitle(player);
        } catch (Exception e) {
        }
    }

    // =============================================================================
    // 12. FAKE FRAME FREEZE - Ложное зависание кадра
    // =============================================================================
    public static void triggerFakeFrameFreeze(EntityPlayer player) {
        if (player == null) return;


        try {
            FakeFrameFreeze.triggerFreeze(player);
        } catch (Exception e) {
        }
    }

    // =============================================================================
    // 13. VIOLENT SHAKE - Сильная тряска экрана
    // =============================================================================
    public static void triggerViolentShake() {

        try {
            GlitchManager.triggerViolentShake();
        } catch (Exception e) {
        }
    }

    // =============================================================================
    // 14. BEDROCK TUNNEL - Спавн бедрокового туннеля
    // =============================================================================
    public static void spawnBedrockTunnel(EntityPlayer player) {
        if (player == null) return;


        try {
            WorldGenBedrockTunnel.generateNearPlayer(player);
        } catch (Exception e) {
        }
    }

    // =============================================================================
    // УТИЛИТЫ
    // =============================================================================

    /**
     * Сброс всех эффектов
     */
    public static void resetAll() {
        inventoryGlitchActive = false;
        inventoryGlitchEnd = 0;
        currentFogMultiplier = 1.0F;
        fogEffectEnd = 0;
        bloodTimeActive = false;
        bloodTimeEnd = 0;
        delayedFootsteps.clear();
        resetInvertedCamera();
        resetBloodTime();
        resetFog();
        FootstepEcho.reset();
        InventoryGlitch.reset();
        InventoryDeletionLies.reset();
        FakeFrameFreeze.reset();
        TemporalVoidDrop.reset();
        RenderHorrorEffects.reset();
        SleepDisruption.reset();
        DisappearingAnomalies.reset();
        WorldCorruptorGenerator.reset();
        PhantomObserver.removeAll();
        MirrorDouble.removeDouble();
    }

    /**
     * Вспомогательный класс для задержанных звуков
     */
    private static class DelayedSound {
        World world;
        double x, y, z;
        String soundName;
        long triggerTime;

        DelayedSound(World world, double x, double y, double z, String soundName, long delay) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.soundName = soundName;
            this.triggerTime = System.currentTimeMillis() + delay;
        }
    }
}
