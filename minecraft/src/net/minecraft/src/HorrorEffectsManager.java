package net.minecraft.src;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;

/**
 * Main manager for all new horror effects with progressive staging system.
 * Simplified version that works with existing methods only.
 */
public class HorrorEffectsManager {
    private static Random rand = new Random();
    private static World world;
    private static EntityPlayer player;
    private static boolean initialized = false;

    // Optimization: track last tick time to reduce update frequency
    private static long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 1000L; // Update only once per second

    // A stage must exhaust its complete shuffled effect list before advancing.
    private static final long MIN_EFFECT_INTERVAL = 4 * 60 * 1000L;
    private static final long MAX_EFFECT_INTERVAL = 8 * 60 * 1000L;
    private static final int STAGE_COUNT = 4;
    // Stage 4 contains the original extreme effects plus the ten render glitches.
    // Event 50 remains the final tunnel spawn and is not scheduled as a regular effect.
    private static final int[] STAGE_EFFECT_COUNTS = {11, 8, 17, 26};
    private static List<Integer> stageEffectOrder = new ArrayList<Integer>();
    private static int stageEffectIndex = 0;
    private static int currentEffectId = -1;
    private static int currentEffectRepeats = 0;
    private static int remainingEffectRepeats = 0;
    private static long nextEffectTime = 0;
    private static long nextEffectBaseInterval = 5 * 60 * 1000L;
    private static boolean tunnelPhaseStarted = false;
    private static boolean finalGlitchPhase = false;
    private static int finalGlitchIndex = 0;
    private static long nextFinalGlitchTime = 0L;
    private static long manualTunnelTime = 0L;
    private static List<Packet72HorrorEvent> pendingNetworkEvents = new ArrayList<Packet72HorrorEvent>();
    private static long networkEffectBusyUntil = 0L;

    public static void setSpeedMultiplier(float multiplier) {
        HorrorState.horrorSpeedMultiplier = Math.max(0.1F, Math.min(100.0F, multiplier));
        lastUpdateTime = 0;
        if (initialized && !tunnelPhaseStarted && nextEffectTime > 0) {
            nextEffectTime = System.currentTimeMillis()
                + (long)(nextEffectBaseInterval / HorrorState.horrorSpeedMultiplier);
        }

    }

    public static void stopFinalBsod() {
        UnknownEffects.setEnabled(false);
        UnknownEffects.restoreGamma();
        UnknownEffects.setEnabled(true);
    }

    public static long getMillisUntilNextEffect() {
        if (tunnelPhaseStarted || HorrorState.currentEffectStage >= STAGE_COUNT) {
            return 0L;
        }
        return Math.max(0L, nextEffectTime - System.currentTimeMillis());
    }

    /**
     * Initialize the horror effects system with world and player
     */
    public static void init(World w, EntityPlayer p) {
        if (w == null || p == null) return;
        world = w;
        player = p;
        initialized = true;
        resetScheduler();

        // Инициализировать Unknown.dll для нативных эффектов
        UnknownEffects.init();
    }

    /**
     * Main update method - call every tick from game loop
     */
    public static void update() {
        if (world == null || player == null) {
            return;
        }
        if (HorrorState.safeMode) return;

        long currentTime = System.currentTimeMillis();

        // Optimization: only update once per second to reduce lag
        if (currentTime - lastUpdateTime < UPDATE_INTERVAL) {
            return;
        }
        lastUpdateTime = currentTime;

        HorrorState.updatePlayTime();

        if (manualTunnelTime > 0L && currentTime >= manualTunnelTime) {
            manualTunnelTime = 0L;
            finalGlitchPhase = false;
            nextFinalGlitchTime = 0L;
            RenderHorrorEffects.reset();
            triggerTunnelEvent();
            return;
        }

        if (finalGlitchPhase) {
            updateFinalGlitchSequence(currentTime);
            return;
        }

        if (!tunnelPhaseStarted && currentTime >= nextEffectTime) {
            runScheduledEffect(currentTime);
        }

        // Update continuous effects that have tick methods (LESS FREQUENTLY)
        updateContinuousEffects();
    }

    /**
     * Trigger next horror effect based on current stage
     */
    private static void triggerNextEffect() {
        int effectNum = currentEffectId;


        switch (HorrorState.currentEffectStage) {
            case 0:
                triggerStage1Effect(effectNum);
                break;
            case 1:
                triggerStage2Effect(effectNum);
                break;
            case 2:
                triggerStage3Effect(effectNum);
                break;
            case 3:
            default:
                triggerStage4Effect(effectNum);
                break;
        }
    }

    private static void resetScheduler() {
            stageEffectOrder.clear();
            stageEffectIndex = 0;
            currentEffectId = -1;
            currentEffectRepeats = 0;
            remainingEffectRepeats = 0;
            nextEffectTime = 0;
            nextEffectBaseInterval = 5 * 60 * 1000L;
            tunnelPhaseStarted = false;
            finalGlitchPhase = false;
            finalGlitchIndex = 0;
            nextFinalGlitchTime = 0L;
            manualTunnelTime = 0L;
            HorrorState.currentEffectStage = 0;
            HorrorState.effectsTriggeredCount = 0;
            prepareStage(0);
            nextEffectTime = System.currentTimeMillis()
                + (long)(nextEffectBaseInterval
                / Math.max(0.1F, HorrorState.horrorSpeedMultiplier));
        }

    private static void prepareStage(int stage) {
            stageEffectOrder.clear();
            int count = STAGE_EFFECT_COUNTS[stage];
            for (int i = 0; i < count; i++) {
                stageEffectOrder.add(Integer.valueOf(i));
            }
            stageEffectIndex = 0;
            currentEffectId = -1;
            remainingEffectRepeats = 0;
            nextEffectBaseInterval = randomIntervalBase();
            nextEffectTime = System.currentTimeMillis()
                + (long)(nextEffectBaseInterval
                / Math.max(0.1F, HorrorState.horrorSpeedMultiplier));
        }

    private static long randomIntervalBase() {
            return MIN_EFFECT_INTERVAL
                + (long)(rand.nextDouble() * (MAX_EFFECT_INTERVAL - MIN_EFFECT_INTERVAL));
    }

    private static long nextInterval(boolean repeat) {
            long interval = randomIntervalBase();
            if (repeat) {
                interval = interval / (currentEffectRepeats + 1);
            }
            float multiplier = Math.max(0.01F, HorrorState.horrorSpeedMultiplier);
            return Math.max(250L, (long)(interval / multiplier));
        }

    private static void runScheduledEffect(long currentTime) {
            if (stageEffectIndex >= stageEffectOrder.size()) {
                if (HorrorState.currentEffectStage < STAGE_COUNT - 1) {
                    HorrorState.currentEffectStage++;
                    prepareStage(HorrorState.currentEffectStage);
                    nextEffectTime = currentTime + nextInterval(false);
                    return;
                }
                startFinalGlitchSequence(currentTime);
                return;
            }

            if (remainingEffectRepeats == 0) {
                currentEffectId = stageEffectOrder.get(stageEffectIndex).intValue();
                currentEffectRepeats = 1 + rand.nextInt(3);
                remainingEffectRepeats = currentEffectRepeats;
            }

            triggerNextEffect();
            remainingEffectRepeats--;
            HorrorState.effectsTriggeredCount++;
            HorrorState.lastEffectTriggerTime = currentTime;

            if (remainingEffectRepeats > 0) {
                nextEffectBaseInterval = randomIntervalBase()
                    / (currentEffectRepeats + 1);
                nextEffectTime = currentTime + (long)(nextEffectBaseInterval
                    / Math.max(0.1F, HorrorState.horrorSpeedMultiplier));
            } else {
                stageEffectIndex++;
                nextEffectBaseInterval = randomIntervalBase();
                nextEffectTime = currentTime + (long)(nextEffectBaseInterval
                    / Math.max(0.1F, HorrorState.horrorSpeedMultiplier));
            }
        }

    private static void spawnTunnelAndEnterFinalPhase() {
            if (tunnelPhaseStarted || player == null || world == null) {
                return;
            }
            tunnelPhaseStarted = true;
            try {
                int spawnX = (int)(player.posX + 30 + rand.nextInt(70) - 35);
                int spawnZ = (int)(player.posZ + 30 + rand.nextInt(70) - 35);
                int spawnY = world.getHeightValue(spawnX, spawnZ);
                new WorldGenBedrockTunnel().generate(world, rand, spawnX, spawnY, spawnZ);
                HorrorState.tunnelSpawned = true;
                HorrorState.currentEffectStage = STAGE_COUNT;
            } catch (RuntimeException e) {
                tunnelPhaseStarted = false;
        }
    }

    private static void startFinalGlitchSequence(long currentTime) {
        if (finalGlitchPhase || tunnelPhaseStarted) {
            return;
        }
        finalGlitchPhase = true;
        finalGlitchIndex = 0;
        nextFinalGlitchTime = currentTime;
    }

    private static void updateFinalGlitchSequence(long currentTime) {
        if (currentTime < nextFinalGlitchTime) {
            return;
        }

        if (finalGlitchIndex == 0) {
            triggerRenderEffect(56, 22000L, 1, "error.glitch11", 22000L);
            nextFinalGlitchTime = currentTime + 22000L;
            finalGlitchIndex++;
        } else if (finalGlitchIndex == 1) {
            triggerRenderEffect(57, 12000L, 2, "error.glitch6", 12000L);
            nextFinalGlitchTime = currentTime + 24000L;
            finalGlitchIndex++;
        } else if (finalGlitchIndex == 2) {
            triggerRenderEffect(58, 20000L, 1, "error.glitch2", 20000L);
            nextFinalGlitchTime = currentTime + 20000L;
            finalGlitchIndex++;
        } else {
            finalGlitchPhase = false;
            nextFinalGlitchTime = 0L;
            triggerTunnelEvent();
        }

    }

    private static void triggerRenderEffect(int id, long duration, int repeats,
                                            String sound, long soundDuration) {
        RenderHorrorEffects.trigger(id, duration, repeats, sound, soundDuration);
    }

    private static void triggerTunnelEvent() {
        if (tunnelPhaseStarted) {
            return;
        }
        triggerSpecificEffect(50);
        tunnelPhaseStarted = true;
        HorrorState.tunnelSpawned = true;
        HorrorState.currentEffectStage = STAGE_COUNT;
    }

    /**
     * Stage 1: Weak effects
     */
    private static void triggerStage1Effect(int effectNum) {
        try {
            switch (effectNum % 11) {
                case 0:
                    HorrorEffects.triggerFootstepEcho(player);
                    break;
                case 1:
                    HorrorEffects.triggerInventoryGlitchMinor();
                    break;
                case 2:
                    HorrorEffects.playAmbientSound(world, player);
                    break;
                case 3:
                    HorrorEffects.increaseFogSlightly();
                    break;
                case 4:
                    UnknownEffects.hardwareBeep(37, 300);
                    break;
                case 5:
                    UnknownEffects.possessCursor(400, 300, 10, 2000);
                    break;
                case 6:
                    int x1 = (int)player.posX;
                    int y1 = (int)player.posY;
                    int z1 = (int)player.posZ;
                    UnknownEffects.whisperClipboard("X: " + x1 + " Y: " + y1 + " Z: " + z1);
                    break;
                case 7:
                    UnknownEffects.jitterWindow(5, 1000);
                    break;
                case 8:
                    // NEW: WindowTransparencyGhosting (3s) — Stage 1
                    UnknownEffects.windowTransparency(3000);
                    break;
                case 9:
                    // NEW: KeyboardInjectedTyping (3s) — Stage 1
                    triggerKeyboardInjection();
                    break;
                case 10:
                    // NEW: FakeTaskkillAlert (5s) — Stage 1
                    DynamicWindowTitle.triggerTitle(player, "???", 5000L);
                    break;
            }
        } catch (Exception e) {}
    }

    /**
     * Stage 2: Medium effects
     */
    private static void triggerStage2Effect(int effectNum) {
        try {
            switch (effectNum % 8) {
                case 0:
                    HorrorEffects.checkBehindYouGlitch(world, player);
                    break;
                case 1:
                    HorrorEffects.triggerModerateFog();
                    break;
                case 2:
                    HorrorEffects.triggerInventoryGlitchMajor();
                    break;
                case 3:
                    HorrorEffects.playLowHum(world, player);
                    break;
                case 4:
                    HorrorEffects.triggerFootstepEcho(player);
                    break;
                case 5:
                    // Средняя тряска окна
                    UnknownEffects.jitterWindow(10, 1500);
                    break;
                case 6:
                    // Притяжение курсора средней силы
                    UnknownEffects.possessCursor(200, 200, 25, 3000);
                    break;
                case 7:
                    // Системный писк высокой частоты
                    UnknownEffects.hardwareBeep(4000, 400);
                    break;
            }
        } catch (Exception e) {}
    }

    /**
     * Stage 3: Strong effects (отдельные эффекты)
     */
    private static void triggerStage3Effect(int effectNum) {
        try {
            switch (effectNum % 17) {
                case 0:
                    HorrorEffects.triggerBloodTime();
                    break;
                case 1:
                    HorrorEffects.triggerPartisanInterference(world, player);
                    break;
                case 2:
                    HorrorEffects.triggerHeavyFog();
                    break;
                case 3:
                    HorrorEffects.checkBehindYouGlitch(world, player);
                    break;
                case 4:
                    HorrorEffects.triggerInventoryGlitchMajor();
                    break;
                case 5:
                    UnknownEffects.corruptGamma(0, 2000);
                    break;
                case 6:
                    UnknownEffects.jitterWindow(15, 2000);
                    break;
                case 7:
                    UnknownEffects.possessCursor(100, 100, 50, 4000);
                    break;
                case 8:
                    UnknownEffects.ghostOverlay(1000);
                    break;
                case 9:
                    UnknownEffects.hardwareBeep(200, 500);
                    break;
                case 10:
                    UnknownEffects.corruptGamma(1, 1500);
                    break;
                case 11:
                    HorrorEffects.playLowHum(world, player);
                    break;
                case 12:
                    UnknownEffects.aggressiveTaskbar(5000);
                    break;
                case 13:
                    HorrorEffects.triggerFootstepEcho(player);
                    break;
                case 14:
                    // NEW: MicrophoneFeedbackScreamer (2.5s) - Stage 3
                    DynamicWindowTitle.triggerTitle(player, "CAN YOU HEAR ME?", 2500L);
                    break;
                case 15:
                    // NEW: ScreenStrobeDeconstruction (2s) - Stage 3
                    ScreenStrobeEffect.trigger();
                    DynamicWindowTitle.triggerTitle(player, "LOOK AWAY", 2000L);
                    break;
                case 16:
                    // NEW: FakeHardwareFreezeAudioLoop (4s) - Stage 3
                    DynamicWindowTitle.triggerTitle(player, "PROCESSING...", 4000L);
                    break;
            }
        } catch (Exception e) {}
    }

    /**
     * Stage 4: Extreme effects (отдельные эффекты)
     */
    private static void triggerStage4Effect(int effectNum) {
        try {
            switch (effectNum % 26) {
                case 0:
                    HorrorEffects.triggerBloodTime();
                    break;
                case 1:
                    HorrorEffects.triggerHeavyFog();
                    break;
                case 2:
                    HorrorEffects.triggerPartisanInterference(world, player);
                    break;
                case 3:
                    HorrorEffects.checkBehindYouGlitch(world, player);
                    break;
                case 4:
                    HorrorEffects.triggerInventoryGlitchMajor();
                    break;
                case 5:
                    HorrorEffects.triggerFootstepEcho(player);
                    break;
                case 6:
                    HorrorEffects.playLowHum(world, player);
                    break;
                case 7:
                    UnknownEffects.screenMelt(2000, 10);
                    break;
                case 8:
                    UnknownEffects.ghostOverlay(3000);
                    break;
                case 9:
                    UnknownEffects.corruptGamma(2, 3000);
                    break;
                case 10:
                    UnknownEffects.jitterWindow(25, 4000);
                    break;
                case 11:
                    UnknownEffects.possessCursor(0, 0, 90, 6000);
                    break;
                case 12:
                    UnknownEffects.aggressiveTaskbar(8000);
                    break;
                case 13:
                    int x = (int)player.posX;
                    int y = (int)player.posY;
                    int z = (int)player.posZ;
                    UnknownEffects.whisperClipboard("HELP ME... X:" + x + " Y:" + y + " Z:" + z);
                    break;
                case 14:
                    // NEW: DisplayResolutionSnap (3s) - Stage 4
                    UnknownEffects.resolutionSnap(3000);
                    break;
                case 15:
                    // NEW: EntityTeleportJumpscareVoid (2.5s) - Stage 4
                    if (player instanceof EntityPlayerSP) {
                        TemporalVoidDrop.setPlayer((EntityPlayerSP)player);
                        TemporalVoidDrop.trigger();
                    } else {
                        DynamicWindowTitle.triggerTitle(player, "FALLING...", 2500L);
                    }
                    break;
                case 16:
                    RenderHorrorEffects.trigger(45, 5000L);
                    break;
                case 17:
                    triggerRenderEffect(46, 5000L, 1, "error.glitch1", 5000L);
                    break;
                case 18:
                    triggerRenderEffect(47, 3000L, 1, "error.glitch3", 3000L);
                    break;
                case 19:
                    RenderHorrorEffects.trigger(48, 5000L);
                    break;
                case 20:
                    triggerRenderEffect(49, 18000L, 1, "error.glitch5", 18000L);
                    break;
                case 21:
                    triggerRenderEffect(51, 4000L, 3, "error.glitch8", 4000L);
                    break;
                case 22:
                    triggerRenderEffect(52, 2000L, 2, "error.glitch12", 2000L);
                    break;
                case 23:
                    triggerRenderEffect(53, 7000L, 1, "error.glitch4", 7000L);
                    break;
                case 24:
                    triggerRenderEffect(54, 30000L, 1, "error.glitch9", 30000L);
                    break;
                case 25:
                    RenderHorrorEffects.trigger(55, 5000L);
                    break;
            }
        } catch (Exception e) {}
    }

    /**
     * Update effects that run continuously in background
     * OPTIMIZED: Heavy effects run less frequently to reduce lag
     */
    private static void updateContinuousEffects() {
        // Light effects - run every second
        try {
            HorrorEffects.tickFootstepEcho();
        } catch (Exception e) {}

        // Heavy effects - run only every 5 seconds to reduce lag
        long currentTime = System.currentTimeMillis();
        if (currentTime % 5000 < 1000) { // Only run once every 5 seconds
            try {
                // Continuous tick для других систем (если нужно)
            } catch (Exception e) {}
        }
    }

    private static void scheduleTunnelAfterFinalEffect(long duration) {
        finalGlitchPhase = true;
        tunnelPhaseStarted = false;
        finalGlitchIndex = 3;
        nextFinalGlitchTime = System.currentTimeMillis() + duration;
        lastUpdateTime = 0L;
    }

    /**
     * Trigger all effects immediately (for /x*n* command)
     */
    public static void triggerAllImmediate(float multiplier) {
        HorrorState.currentEffectStage = 4;


        try {
            // ===== БАЗОВЫЕ ЭФФЕКТЫ (всегда) =====
            HorrorEffects.triggerFootstepEcho(player);
            HorrorEffects.triggerInventoryGlitchMajor();
            HorrorEffects.playLowHum(world, player);
            HorrorEffects.triggerHeavyFog();
            HorrorEffects.triggerBloodTime();
            HorrorEffects.checkBehindYouGlitch(world, player);
            HorrorEffects.triggerGlitchedWindowTitle(player);

            // ===== СЛАБЫЕ НАТИВНЫЕ ЭФФЕКТЫ (x >= 1) =====
            if (multiplier >= 1.0F) {
                UnknownEffects.hardwareBeep(37, 500);
                UnknownEffects.jitterWindow(10, 2000);
            }

            // ===== СРЕДНИЕ ЭФФЕКТЫ (x >= 5) =====
            if (multiplier >= 5.0F) {
                UnknownEffects.corruptGamma(0, 2000);
                UnknownEffects.possessCursor(200, 200, 40, 3000);

                // Дополнительные визуальные эффекты
                HorrorEffects.triggerFakeFrameFreeze(player);
            }

            // ===== СИЛЬНЫЕ ЭФФЕКТЫ (x >= 10) =====
            if (multiplier >= 10.0F) {
                HorrorEffects.triggerPartisanInterference(world, player);
                HorrorEffects.generateDisappearingAnomaly(world, player);

                // Экстремальные нативные эффекты
                UnknownEffects.screenMelt(1500, 8);
                UnknownEffects.ghostOverlay(2000);
                UnknownEffects.aggressiveTaskbar(5000);

                int x = (int)player.posX;
                int y = (int)player.posY;
                int z = (int)player.posZ;
                UnknownEffects.whisperClipboard("ANOMALY DETECTED X:" + x + " Y:" + y + " Z:" + z);
            }

            // ===== МАКСИМАЛЬНЫЕ ЭФФЕКТЫ (x >= 50) =====
            if (multiplier >= 50.0F) {
                // Спавн сущностей
                HorrorEffects.spawnPhantomObserver(world, player);
                HorrorEffects.spawnMirrorDouble(world, player);

                // Дополнительные нативные эффекты
                UnknownEffects.corruptGamma(1, 3000); // Ч/Б контраст
                UnknownEffects.hardwareBeep(4000, 800); // Высокочастотный писк

                // Дополнительные глитчи
                HorrorEffects.triggerViolentShake();
            }

            // ===== ЭКСТРЕМАЛЬНЫЕ ЭФФЕКТЫ (x >= 100) =====
            if (multiplier >= 100.0F) {
                // Критические визуальные глитчи
                for (int i = 0; i < 3; i++) {
                    HorrorEffects.checkBehindYouGlitch(world, player);
                }

                // Множественные нативные эффекты
                UnknownEffects.screenMelt(2000, 10); // Максимальное таяние
                UnknownEffects.jitterWindow(25, 4000); // Максимальная тряска
                UnknownEffects.possessCursor(0, 0, 90, 6000); // Максимальный захват курсора
                UnknownEffects.ghostOverlay(3000); // Длинный оверлей

                // Критическая порча гаммы
                UnknownEffects.corruptGamma(2, 4000); // Темно-красный

                // Спавн всех сущностей
                HorrorEffects.spawnPhantomObserver(world, player);
                HorrorEffects.spawnMirrorDouble(world, player);

                // Интерференция и аномалии
                HorrorEffects.triggerPartisanInterference(world, player);
                HorrorEffects.generateDisappearingAnomaly(world, player);

                // Максимальные глитчи
                GlitchManager.triggerImmediateGlitches(multiplier);
                HorrorEffects.triggerViolentShake();

                // Новые эффекты
                UnknownEffects.corruptWallpaper();
                UnknownEffects.fakeBSOD(3000);
                UnknownEffects.ghostIcon(1);
                InventoryDeletionLies.trigger(player, 15000);
                HallucinatorySoundPan.trigger(player, 8000);
                BedtimeTrappedDimension.setWorld(world, player);

                // Финальный буфер обмена
                UnknownEffects.whisperClipboard("SYSTEM COMPROMISED... NO ESCAPE...");
            }
        } catch (Exception e) {
        }

        HorrorState.lastEffectTriggerTime = System.currentTimeMillis();
        HorrorState.effectsTriggeredCount += 5;
    }

    /**
     * Stop all effects (for /safe command)
     */
    public static void stopAll() {
        resetScheduler();
        HorrorState.lastEffectTriggerTime = 0;

        // Сброс всех игровых эффектов
        try {
            HorrorEffects.resetAll();
        } catch (Exception e) {}

        // Восстановить гамму монитора и отключить Unknown.dll эффекты
        try {
            UnknownEffects.restoreGamma();
            UnknownEffects.setEnabled(false);
        } catch (Exception e) {}
    }

    /**
     * Display system info (for /mstinfo command)
     */
    public static void displaySystemInfo(EntityPlayer player) {
        if (player == null) return;

        try {
            // Этап (Stage)
            String stageName = "Unknown";
            switch (HorrorState.currentEffectStage) {
                case 0: stageName = "Stage 1 (Weak)"; break;
                case 1: stageName = "Stage 2 (Medium)"; break;
                case 2: stageName = "Stage 3 (Strong)"; break;
                case 3: stageName = "Stage 4 (Extreme)"; break;
                case 4: stageName = "Tunnel phase"; break;
            }
            player.addChatMessage("§6=== Monster Info ===");
            player.addChatMessage("§eStage: §f" + stageName);

            // Скорость (Speed multiplier)
            player.addChatMessage("§eSpeed: §fx" + HorrorState.horrorSpeedMultiplier);

            // Таймер до следующего эффекта
            long timeUntilNext = getMillisUntilNextEffect();

            int secondsUntilNext = (int)(timeUntilNext / 1000);
            int minutesUntilNext = secondsUntilNext / 60;
            secondsUntilNext = secondsUntilNext % 60;

            player.addChatMessage("§eNext effect in: §f" + minutesUntilNext + "m " + secondsUntilNext + "s");

            // Следующий эффект
            int nextEffectNum = HorrorState.effectsTriggeredCount % 14;
            String nextEffect = getEffectName(currentEffectId >= 0 ? currentEffectId : nextEffectNum,
                Math.min(HorrorState.currentEffectStage, STAGE_COUNT - 1));
            player.addChatMessage("§eNext effect: §f" + nextEffect);

            // Повторения
            player.addChatMessage("§eRepeats remaining: §f" + remainingEffectRepeats
                + "/" + currentEffectRepeats);

            // Общая статистика
            player.addChatMessage("§eTotal triggered: §f" + HorrorState.effectsTriggeredCount);

        } catch (Exception e) {
            player.addChatMessage("§cError displaying info");
        }
    }

    /**
     * Get effect name by ID and stage
     */
    private static String getEffectName(int effectNum, int stage) {
        switch (stage) {
            case 0: // Stage 1
                switch (effectNum % 8) {
                    case 0: return "Footstep Echo";
                    case 1: return "Minor Inventory Glitch";
                    case 2: return "Ambient Sound";
                    case 3: return "Slight Fog";
                    case 4: return "Hardware Beep (37Hz)";
                    case 5: return "Cursor Pull (Weak)";
                    case 6: return "Clipboard Whisper";
                    case 7: return "Window Jitter (5px)";
                }
                break;
            case 1: // Stage 2
                switch (effectNum % 8) {
                    case 0: return "Behind You Glitch";
                    case 1: return "Moderate Fog";
                    case 2: return "Major Inventory Glitch";
                    case 3: return "Low Hum";
                    case 4: return "Footstep Echo";
                    case 5: return "Window Jitter (10px)";
                    case 6: return "Cursor Pull (Medium)";
                    case 7: return "Hardware Beep (4000Hz)";
                }
                break;
            case 2: // Stage 3
                switch (effectNum % 8) {
                    case 0: return "Blood Time + Gamma (Red)";
                    case 1: return "Partisan Interference";
                    case 2: return "Heavy Fog + Window Jitter";
                    case 3: return "Behind You + Cursor Pull";
                    case 4: return "Blood Time + Fog";
                    case 5: return "Ghost Overlay (1s)";
                    case 6: return "Gamma B/W Contrast";
                    case 7: return "Taskbar Aggression";
                }
                break;
            case 3:
            case 4: // Stage 4
                switch (effectNum % 8) {
                    case 0: return "Blood Time Combo";
                    case 1: return "Visual Chaos + Cursor";
                    case 2: return "Screen Melt + Blood";
                    case 3: return "Visual Collapse";
                    case 4: return "Extreme Screen Melt";
                    case 5: return "Ghost + Taskbar + Clipboard";
                    case 6: return "Max Jitter + Cursor + Gamma";
                    case 7: return "FINAL COMBO (All)";
                }
                break;
        }
        return "Unknown Effect #" + effectNum;
    }

    /**
     * Trigger specific effect by ID (for /event command)
     */
    public static void triggerSpecificEffect(int eventId) {
        // Получить текущего игрока и мир из статических переменных
        if (player == null || world == null) {
            return;
        }

        try {
            switch (eventId) {
                // === Basic Effects (0-9) ===
                case 0:
                    HorrorEffects.triggerFootstepEcho(player);
                    break;
                case 1:
                    HorrorEffects.triggerInventoryGlitchMinor();
                    break;
                case 2:
                    HorrorEffects.triggerInventoryGlitchMajor();
                    break;
                case 3:
                    HorrorEffects.playAmbientSound(world, player);
                    DynamicWindowTitle.triggerTitle(player, "AMBIENT WHISPER", 3000L);
                    break;
                case 4:
                    HorrorEffects.playLowHum(world, player);
                    break;
                case 5:
                    HorrorEffects.increaseFogSlightly();
                    break;
                case 6:
                    HorrorEffects.triggerModerateFog();
                    break;
                case 7:
                    HorrorEffects.triggerHeavyFog();
                    break;
                case 8:
                    BloodTimeCycle.triggerBloodTime();
                    HorrorEffects.triggerBloodTime();
                    break;
                case 9:
                    HorrorEffects.checkBehindYouGlitch(world, player);
                    break;

                // === Unknown.dll Effects (10-19) ===
                case 10:
                    DynamicWindowTitle.triggerTitle(player, "LOW FREQUENCY", 1500L);
                    break;
                case 11:
                    DynamicWindowTitle.triggerTitle(player, "HIGH FREQUENCY", 1500L);
                    break;
                case 12:
                    UnknownEffects.jitterWindow(10, 2000);
                    break;
                case 13:
                    UnknownEffects.jitterWindow(20, 3000);
                    break;
                case 14:
                    UnknownEffects.possessCursor(400, 300, 30, 3000);
                    break;
                case 15:
                    UnknownEffects.possessCursor(200, 200, 60, 4000);
                    break;
                case 16:
                    UnknownEffects.corruptGamma(0, 2000); // Red
                    break;
                case 17:
                    UnknownEffects.corruptGamma(1, 2000); // B/W
                    break;
                case 18:
                    UnknownEffects.corruptGamma(2, 2000); // Dark Red
                    BloodTimeCycle.triggerBloodTime();
                    break;
                case 19:
                    UnknownEffects.screenMelt(1500, 8);
                    break;

                // === Advanced Unknown.dll Effects (20-29) ===
                case 20:
                    UnknownEffects.ghostOverlay(2000);
                    break;
                case 21:
                    UnknownEffects.aggressiveTaskbar(5000);
                    break;
                case 22:
                    int x = (int)player.posX;
                    int y = (int)player.posY;
                    int z = (int)player.posZ;
                    UnknownEffects.whisperClipboard("X:" + x + " Y:" + y + " Z:" + z);
                    break;
                case 23:
                    UnknownEffects.testMessageBox();
                    break;

                // === New effects 24-29 ===
                case 24:
                    // WindowTransparencyGhosting (10s)
                    UnknownEffects.windowTransparency(10000);
                    break;
                case 25:
                    // InvertedCameraInversion (4s)
                    HorrorEffects.triggerInvertedCamera();
                    DynamicWindowTitle.triggerTitle(player, "UPSIDE DOWN", 4000L);
                    break;
                case 26:
                    // Fixed title glitch (5s)
                    DynamicWindowTitle.triggerTitle(player, "???", 5000L);
                    break;
                case 27:
                    // Heartbeat pulse (6s)
                    HorrorEffects.triggerHeartbeat(player);
                    break;
                case 28:
                    // DisplayResolutionSnap (3s)
                    UnknownEffects.resolutionSnap(3000);
                    break;
                case 29:
                    // GhostIcon (2s)
                    UnknownEffects.ghostIcon(1);
                    break;

                // === Entity Spawns (30-34) ===
                case 30:
                    HorrorEffects.spawnPhantomObserver(world, player);
                    DynamicWindowTitle.triggerTitle(player, "PHANTOM OBSERVER", 4000L);
                    break;
                case 31:
                    HorrorEffects.spawnMirrorDouble(world, player);
                    break;
                case 32:
                    triggerKeyboardInjection();
                    break;

                // === Advanced Effects (35-44) ===
                case 35:
                    HorrorEffects.triggerPartisanInterference(world, player);
                    DynamicWindowTitle.triggerTitle(player, "PARTISAN INTERFERENCE", 4000L);
                    break;
                case 36:
                    HorrorEffects.generateDisappearingAnomaly(world, player);
                    DynamicWindowTitle.triggerTitle(player, "DISAPPEARING ANOMALY", 4000L);
                    break;
                case 37:
                    HorrorEffects.triggerGlitchedWindowTitle(player);
                    break;
                case 38:
                    DynamicWindowTitle.triggerTitle(player, "SIGNAL LOST", 3000L);
                    break;
                case 39:
                    ScreenStrobeEffect.trigger();
                    DynamicWindowTitle.triggerTitle(player, "DON'T MOVE", 3000L);
                    break;
                case 40:
                    GlitchManager.triggerImmediateGlitches(10.0F);
                    break;

                // === New effects 41-44 ===
                case 41:
                    // MicrophoneFeedbackScreamer (2.5s)
                    ScreenStrobeEffect.trigger();
                    HorrorEffects.triggerInvertedCamera();
                    DynamicWindowTitle.triggerTitle(player, "CAN YOU HEAR ME?", 2500L);
                    break;
                case 42:
                    // ScreenStrobeDeconstruction (2s)
                    HorrorEffects.triggerInvertedCamera();
                    DynamicWindowTitle.triggerTitle(player, "LOOK AWAY", 2000L);
                    break;
                case 43:
                    // FakeHardwareFreezeAudioLoop (4s)
                    ScreenStrobeEffect.trigger();
                    HorrorEffects.triggerInvertedCamera();
                    DynamicWindowTitle.triggerTitle(player, "PROCESSING...", 4000L);
                    break;
                case 44:
                    // EntityTeleportJumpscareVoid (2.5s)
                    TemporalVoidDrop.setPlayer((EntityPlayerSP)player);
                    TemporalVoidDrop.trigger();
                    break;

                case 45:
                    RenderHorrorEffects.trigger(45, 5000L);
                    break;
                case 46:
                    triggerRenderEffect(46, 5000L, 1, "error.glitch1", 5000L);
                    break;
                case 47:
                    triggerRenderEffect(47, 3000L, 1, "error.glitch3", 3000L);
                    break;
                case 48:
                    RenderHorrorEffects.trigger(48, 5000L);
                    break;
                case 49:
                    triggerRenderEffect(49, 18000L, 1, "error.glitch5", 18000L);
                    break;
                case 50:
                    // ФИНАЛ - Спавн бедрокового туннеля
                    HorrorEffects.spawnBedrockTunnel(player);
                    break;
                case 51:
                    triggerRenderEffect(51, 4000L, 3, "error.glitch8", 4000L);
                    break;
                case 52:
                    triggerRenderEffect(52, 2000L, 2, "error.glitch12", 2000L);
                    break;
                case 53:
                    triggerRenderEffect(53, 7000L, 1, "error.glitch4", 7000L);
                    break;
                case 54:
                    triggerRenderEffect(54, 30000L, 1, "error.glitch9", 30000L);
                    break;
                case 55:
                    RenderHorrorEffects.trigger(55, 5000L);
                    break;
                case 56:
                    triggerRenderEffect(56, 22000L, 1, "error.glitch11", 22000L);
                    break;
                case 57:
                    triggerRenderEffect(57, 12000L, 2, "error.glitch6", 12000L);
                    break;
                case 58:
                    triggerRenderEffect(58, 20000L, 1, "error.glitch2", 20000L);
                    scheduleTunnelAfterFinalEffect(20000L);
                    manualTunnelTime = System.currentTimeMillis() + 20000L;
                    break;

                default:
                    if (player != null) {
                        player.addChatMessage("§cUnknown event ID: " + eventId);
                    }
                    break;
            }
        } catch (Exception e) {
        }
    }

    /**
     * Execute an event authorized by the server.
     */
    public static void triggerNetworkEvent(Packet72HorrorEvent event) {
        if (event == null || player == null || world == null) {
            return;
        }
        pendingNetworkEvents.add(event);
        processNetworkEvents();
    }

    public static void processNetworkEvents() {
        if (player == null || world == null || pendingNetworkEvents.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now < networkEffectBusyUntil) {
            return;
        }
        Packet72HorrorEvent event = pendingNetworkEvents.remove(0);
        if (event.eventId == 59) {
            stopFinalBsod();
            HorrorState.safeMode = true;
            return;
        }
        if (event.stage >= 0 && event.stage < STAGE_COUNT) {
            triggerScheduledStageEffect(event.stage, event.eventId);
        } else {
            triggerSpecificEffect(event.eventId);
        }
        networkEffectBusyUntil = now + Math.max(250L, event.parameter);
    }

    private static void triggerScheduledStageEffect(int stage, int effectNum) {
        switch (stage) {
            case 0:
                triggerStage1Effect(effectNum);
                break;
            case 1:
                triggerStage2Effect(effectNum);
                break;
            case 2:
                triggerStage3Effect(effectNum);
                break;
            case 3:
                triggerStage4Effect(effectNum);
                break;
            default:
                break;
        }
    }

    /**
     * Set world and player (for reinitialization)
     */
    public static void setWorld(World w, EntityPlayer p) {
        if (w != null && p != null && (!initialized || world != w)) {
            init(w, p);
        } else {
            world = w;
            player = p;
        }
    }

    // =============================================================================
    // HELPER METHODS FOR NEW EFFECTS
    // =============================================================================

    /**
     * 5. KeyboardInjectedTyping - Принудительный ввод в чат (3s)
     * Открывает чат и посимвольно печатает текст с именем Windows
     */
    private static void triggerKeyboardInjection() {
        if (player == null) return;
        try {
            String username = System.getProperty("user.name", "User");
            String message = "i can hear you typing, " + username;
            Minecraft mc = Minecraft.theMinecraft;
            if (mc == null || mc.ingameGUI == null) return;

            GuiChat chat = new GuiChat();
            mc.displayGuiScreen(chat);
            chat.message = message;
            mc.ingameGUI.addChatMessage("\u00a78" + message);
        } catch (Exception e) {
        }
    }

    /**
     * 3. FakeTaskkillAlert - Фальшивый системный алерт (5s)
     * Замораживает рендер и показывает нативный MessageBox
     */
    private static void triggerFakeTaskkillAlert() {
        try {
            // Воспроизвести громкий звук ошибки
            UnknownEffects.hardwareBeep(800, 200);

            // Показать фальшивый системный алерт через DLL
            new Thread(new Runnable() { public void run() {
                try {
                    Thread.sleep(50);
                    // Используем MessageBoxA нативно
                    UnknownEffects.UnknownDLL.INSTANCE.TestMessageBox();
                } catch (Exception e) {
                    // ignore
                }
            }}).start();
        } catch (Exception e) {
        }
    }

    /**
     * Проверить, активна ли инвертированная камера
     */
    public static boolean isInvertedCameraActive() {
        return HorrorEffects.isInvertedCameraActive();
    }

    /**
     * Проверить, активен ли фриз
     */
    public static boolean isHardwareFreezeActive() {
        return FakeFreezeEffect.isActive();
    }

    /**
     * Остановить фриз
     */
    public static void stopHardwareFreeze() {
        FakeFreezeEffect.reset();
    }

    // =============================================================================
    // MISSING EFFECTS 4, 6, 9 - ADDED
    // =============================================================================

    /**
     * 4. MicrophoneFeedbackScreamer - Эхо микрофона (2.5 сек)
     * Записывает 2 сек звука и воспроизводит с пониженным питчем
     */
    private static void triggerMicrophoneFeedbackScreamer() {
        // NOTE: Полная реализация требует JNI доступ к микрофону
        // Временно: громкий звук с эффектом демона
        try {
            Minecraft mc = Minecraft.theMinecraft;
            if (mc != null && mc.sndManager != null) {
                // Воспроизводим несколько искаженных звуков
                mc.sndManager.playSoundFX("mob.zombie.say", 2.0F, 0.5F);
                mc.sndManager.playSoundFX("damage.hurt", 2.0F, 0.3F);
            }
        } catch (Exception e) {}
    }

    /**
     * 6. ScreenStrobeDeconstruction - Стробоскоп (2 сек)
     * Чередование чёрного экрана и искажённого кадра
     */
    public static void triggerScreenStrobe() {
        ScreenStrobeEffect.trigger();
    }

    /**
     * 9. FakeHardwareFreezeAudioLoop - Зависание (4 сек)
     * Картинка застывает, звук зацикливается
     */
    public static void triggerHardwareFreeze() {
        FakeFreezeEffect.trigger();
    }
}
