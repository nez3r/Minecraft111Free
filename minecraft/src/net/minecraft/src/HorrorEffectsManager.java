package net.minecraft.src;

import java.util.Random;
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

    // Effect interval: 3-8 minutes (randomized)
    private static final long MIN_EFFECT_INTERVAL = 3 * 60 * 1000L; // 3 minutes
    private static final long MAX_EFFECT_INTERVAL = 8 * 60 * 1000L; // 8 minutes

    // Effect repeat count: 1-3 times (randomized)
    private static int currentEffectRepeats = 0;
    private static int maxEffectRepeats = 1;

    /**
     * Initialize the horror effects system with world and player
     */
    public static void init(World w, EntityPlayer p) {
        if (w == null || p == null) return;
        world = w;
        player = p;
        initialized = true;

        // Инициализировать Unknown.dll для нативных эффектов
        try {
            UnknownEffects.init();
        } catch (Exception e) {
            System.err.println("Failed to initialize Unknown.dll: " + e.getMessage());
        }
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

        // Initialize timer on first update
        if (HorrorState.lastEffectTriggerTime == 0) {
            HorrorState.lastEffectTriggerTime = currentTime;
            // Рандомизировать первый интервал (3-8 минут)
            maxEffectRepeats = 1 + rand.nextInt(3); // 1-3 повторения
            currentEffectRepeats = 0;
            // НЕ return - сразу вызвать первый эффект если множитель > 1
            if (HorrorState.horrorSpeedMultiplier > 1.0F) {
                triggerNextEffect();
                HorrorState.lastEffectTriggerTime = currentTime;
                HorrorState.effectsTriggeredCount++;
            }
            return;
        }

        // Использовать средний интервал (5.5 минут) с учётом множителя скорости
        // При x100 интервал должен быть ~3.3 секунды
        long baseInterval = 5 * 60 * 1000L + 30 * 1000L; // 5.5 минут
        long effectInterval = (long)(baseInterval / HorrorState.horrorSpeedMultiplier);

        // Проверить, пора ли запускать следующий эффект
        if (currentTime - HorrorState.lastEffectTriggerTime >= effectInterval) {
            triggerNextEffect();
            HorrorState.lastEffectTriggerTime = currentTime;
            HorrorState.effectsTriggeredCount++;

            // Увеличить счётчик повторений
            currentEffectRepeats++;

            // Если достигли максимума повторений, выбрать новое случайное количество
            if (currentEffectRepeats >= maxEffectRepeats) {
                maxEffectRepeats = 1 + rand.nextInt(3); // 1-3 повторения
                currentEffectRepeats = 0;
            }

            // Progress to next stage every 4 effects
            if (HorrorState.effectsTriggeredCount > 0 && HorrorState.effectsTriggeredCount % 4 == 0) {
                if (HorrorState.currentEffectStage < 4) {
                    HorrorState.currentEffectStage++;
                }
            }

            // FINAL STAGE (Stage 5) - spawn tunnel ONCE only
            if (HorrorState.currentEffectStage >= 4 && HorrorState.effectsTriggeredCount > 30 && !HorrorState.tunnelSpawned) {
                try {
                    System.out.println("[HorrorEffects] FINAL STAGE - Spawning Bedrock Tunnel ONCE (near player)");
                    WorldGenBedrockTunnel tunnelGen = new WorldGenBedrockTunnel();
                    int spawnX = (int)(player.posX + 30 + rand.nextInt(70) - 35); // 30-100 blocks from player
                    int spawnZ = (int)(player.posZ + 30 + rand.nextInt(70) - 35);
                    int spawnY = world.getHeightValue(spawnX, spawnZ);
                    tunnelGen.generate(world, rand, spawnX, spawnY, spawnZ);
                    HorrorState.tunnelSpawned = true; // Only spawn ONCE
                    HorrorState.currentEffectStage = 5; // Final stage completed
                    System.out.println("[HorrorEffects] Bedrock Tunnel spawned ONCE at: X=" + spawnX + ", Z=" + spawnZ + " (30-100 blocks from player at X=" + (int)player.posX + ", Z=" + (int)player.posZ + ")");
                } catch (Exception tunnelErr) {
                    System.err.println("[HorrorEffects] Tunnel spawn error: " + tunnelErr.getMessage());
                }
            }
        }

        // Update continuous effects that have tick methods (LESS FREQUENTLY)
        updateContinuousEffects();
    }

    /**
     * Trigger next horror effect based on current stage
     */
    private static void triggerNextEffect() {
        int effectNum = HorrorState.effectsTriggeredCount % 14;

        System.out.println("[HorrorEffectsManager] Triggering effect - Stage: " + HorrorState.currentEffectStage + ", EffectNum: " + effectNum);

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
                    System.out.println("[HorrorEffects] Triggering Hardware Beep (37Hz)");
                    UnknownEffects.hardwareBeep(37, 300);
                    break;
                case 5:
                    System.out.println("[HorrorEffects] Triggering Cursor Pull (Weak)");
                    UnknownEffects.possessCursor(400, 300, 10, 2000);
                    break;
                case 6:
                    System.out.println("[HorrorEffects] Triggering Clipboard Whisper");
                    int x1 = (int)player.posX;
                    int y1 = (int)player.posY;
                    int z1 = (int)player.posZ;
                    UnknownEffects.whisperClipboard("X: " + x1 + " Y: " + y1 + " Z: " + z1);
                    break;
                case 7:
                    System.out.println("[HorrorEffects] Triggering Window Jitter (5px)");
                    UnknownEffects.jitterWindow(5, 1000);
                    break;
                case 8:
                    // NEW: WindowTransparencyGhosting (3s) — Stage 1
                    System.out.println("[HorrorEffects] Triggering Window Transparency (3s)");
                    UnknownEffects.windowTransparency(3000);
                    break;
                case 9:
                    // NEW: KeyboardInjectedTyping (3s) — Stage 1
                    System.out.println("[HorrorEffects] Triggering Keyboard Injection (3s)");
                    triggerKeyboardInjection();
                    break;
                case 10:
                    // NEW: FakeTaskkillAlert (5s) — Stage 1
                    System.out.println("[HorrorEffects] Triggering Fake Taskkill Alert (5s)");
                    triggerFakeTaskkillAlert();
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
                    System.out.println("[HorrorEffects] Triggering Window Jitter (10px)");
                    // Средняя тряска окна
                    UnknownEffects.jitterWindow(10, 1500);
                    break;
                case 6:
                    System.out.println("[HorrorEffects] Triggering Cursor Pull (Medium)");
                    // Притяжение курсора средней силы
                    UnknownEffects.possessCursor(200, 200, 25, 3000);
                    break;
                case 7:
                    System.out.println("[HorrorEffects] Triggering Hardware Beep (4000Hz)");
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
                    System.out.println("[HorrorEffects] Triggering Gamma (Red)");
                    UnknownEffects.corruptGamma(0, 2000);
                    break;
                case 6:
                    System.out.println("[HorrorEffects] Triggering Window Jitter (15px)");
                    UnknownEffects.jitterWindow(15, 2000);
                    break;
                case 7:
                    System.out.println("[HorrorEffects] Triggering Cursor Pull (Strong)");
                    UnknownEffects.possessCursor(100, 100, 50, 4000);
                    break;
                case 8:
                    System.out.println("[HorrorEffects] Triggering Ghost Overlay");
                    UnknownEffects.ghostOverlay(1000);
                    break;
                case 9:
                    System.out.println("[HorrorEffects] Triggering Hardware Beep (200Hz)");
                    UnknownEffects.hardwareBeep(200, 500);
                    break;
                case 10:
                    System.out.println("[HorrorEffects] Triggering Gamma (B/W)");
                    UnknownEffects.corruptGamma(1, 1500);
                    break;
                case 11:
                    HorrorEffects.playLowHum(world, player);
                    break;
                case 12:
                    System.out.println("[HorrorEffects] Triggering Aggressive Taskbar");
                    UnknownEffects.aggressiveTaskbar(5000);
                    break;
                case 13:
                    HorrorEffects.triggerFootstepEcho(player);
                    break;
                case 14:
                    // NEW: MicrophoneFeedbackScreamer (2.5s) - Stage 3
                    System.out.println("[HorrorEffects] Triggering Microphone Feedback (2.5s)");
                    triggerMicrophoneFeedbackScreamer();
                    break;
                case 15:
                    // NEW: ScreenStrobeDeconstruction (2s) - Stage 3
                    System.out.println("[HorrorEffects] Triggering Screen Strobe (2s)");
                    ScreenStrobeEffect.trigger();
                    break;
                case 16:
                    // NEW: FakeHardwareFreezeAudioLoop (4s) - Stage 3
                    System.out.println("[HorrorEffects] Triggering Hardware Freeze (4s)");
                    FakeFreezeEffect.trigger();
                    break;
            }
        } catch (Exception e) {}
    }

    /**
     * Stage 4: Extreme effects (отдельные эффекты)
     */
    private static void triggerStage4Effect(int effectNum) {
        try {
            switch (effectNum % 16) {
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
                    System.out.println("[HorrorEffects] Triggering Screen Melt");
                    UnknownEffects.screenMelt(2000, 10);
                    break;
                case 8:
                    System.out.println("[HorrorEffects] Triggering Ghost Overlay (3s)");
                    UnknownEffects.ghostOverlay(3000);
                    break;
                case 9:
                    System.out.println("[HorrorEffects] Triggering Gamma (Dark Red)");
                    UnknownEffects.corruptGamma(2, 3000);
                    break;
                case 10:
                    System.out.println("[HorrorEffects] Triggering Max Window Jitter");
                    UnknownEffects.jitterWindow(25, 4000);
                    break;
                case 11:
                    System.out.println("[HorrorEffects] Triggering Max Cursor Pull");
                    UnknownEffects.possessCursor(0, 0, 90, 6000);
                    break;
                case 12:
                    System.out.println("[HorrorEffects] Triggering Aggressive Taskbar");
                    UnknownEffects.aggressiveTaskbar(8000);
                    break;
                case 13:
                    System.out.println("[HorrorEffects] Triggering Clipboard Whisper");
                    int x = (int)player.posX;
                    int y = (int)player.posY;
                    int z = (int)player.posZ;
                    UnknownEffects.whisperClipboard("HELP ME... X:" + x + " Y:" + y + " Z:" + z);
                    break;
                case 14:
                    // NEW: DisplayResolutionSnap (3s) - Stage 4
                    System.out.println("[HorrorEffects] Triggering Resolution Snap (3s)");
                    UnknownEffects.resolutionSnap(3000);
                    break;
                case 15:
                    // NEW: EntityTeleportJumpscareVoid (2.5s) - Stage 4
                    System.out.println("[HorrorEffects] Triggering Void Drop (2.5s)");
                    TemporalVoidDrop.setPlayer((EntityPlayerSP)player);
                    TemporalVoidDrop.trigger();
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

    /**
     * Trigger all effects immediately (for /x*n* command)
     */
    public static void triggerAllImmediate(float multiplier) {
        HorrorState.currentEffectStage = 4;

        System.out.println("[HorrorEffectsManager] triggerAllImmediate() called with multiplier: " + multiplier);

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
                WorldCorruptorGenerator.init(world, player);
                HallucinatorySoundPan.trigger(player, 8000);
                BedtimeTrappedDimension.setWorld(world, player);

                // Финальный буфер обмена
                UnknownEffects.whisperClipboard("SYSTEM COMPROMISED... NO ESCAPE...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        HorrorState.lastEffectTriggerTime = System.currentTimeMillis();
        HorrorState.effectsTriggeredCount += 5;
    }

    /**
     * Stop all effects (for /safe command)
     */
    public static void stopAll() {
        HorrorState.currentEffectStage = 0;
        HorrorState.effectsTriggeredCount = 0;
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
                case 4: stageName = "Stage 4 (MAX)"; break;
            }
            player.addChatMessage("§6=== Monster Info ===");
            player.addChatMessage("§eStage: §f" + stageName);

            // Скорость (Speed multiplier)
            player.addChatMessage("§eSpeed: §fx" + HorrorState.horrorSpeedMultiplier);

            // Таймер до следующего эффекта
            long currentTime = System.currentTimeMillis();
            long timeSinceLastEffect = currentTime - HorrorState.lastEffectTriggerTime;
            long effectInterval = (long)((MIN_EFFECT_INTERVAL + (MAX_EFFECT_INTERVAL - MIN_EFFECT_INTERVAL) / 2) / HorrorState.horrorSpeedMultiplier);
            long timeUntilNext = Math.max(0, effectInterval - timeSinceLastEffect);

            int secondsUntilNext = (int)(timeUntilNext / 1000);
            int minutesUntilNext = secondsUntilNext / 60;
            secondsUntilNext = secondsUntilNext % 60;

            player.addChatMessage("§eNext effect in: §f" + minutesUntilNext + "m " + secondsUntilNext + "s");

            // Следующий эффект
            int nextEffectNum = HorrorState.effectsTriggeredCount % 14;
            String nextEffect = getEffectName(nextEffectNum, HorrorState.currentEffectStage);
            player.addChatMessage("§eNext effect: §f" + nextEffect);

            // Повторения
            player.addChatMessage("§eRepeats: §f" + currentEffectRepeats + "/" + maxEffectRepeats);

            // Общая статистика
            player.addChatMessage("§eTotal triggered: §f" + HorrorState.effectsTriggeredCount);

        } catch (Exception e) {
            player.addChatMessage("§cError displaying info");
            e.printStackTrace();
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
            System.err.println("[HorrorEffectsManager] Cannot trigger effect: player or world is null");
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
                    HorrorEffects.triggerBloodTime();
                    break;
                case 9:
                    HorrorEffects.checkBehindYouGlitch(world, player);
                    break;

                // === Unknown.dll Effects (10-19) ===
                case 10:
                    UnknownEffects.hardwareBeep(37, 500);
                    break;
                case 11:
                    UnknownEffects.hardwareBeep(4000, 500);
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
                    // WindowTransparencyGhosting (3s)
                    System.out.println("[HorrorEffects] Event 24: Window Transparency (3s)");
                    UnknownEffects.windowTransparency(3000);
                    break;
                case 25:
                    // InvertedCameraInversion (4s)
                    System.out.println("[HorrorEffects] Event 25: Inverted Camera (4s)");
                    HorrorEffects.triggerInvertedCamera();
                    break;
                case 26:
                    // FakeTaskkillAlert (5s)
                    System.out.println("[HorrorEffects] Event 26: Fake Taskkill (5s)");
                    triggerFakeTaskkillAlert();
                    break;
                case 27:
                    // SystemVolumeSpikeHeartbeat (6s)
                    System.out.println("[HorrorEffects] Event 27: Volume Spike (6s)");
                    UnknownEffects.pulseSystemVolume(6000);
                    break;
                case 28:
                    // DisplayResolutionSnap (3s)
                    System.out.println("[HorrorEffects] Event 28: Resolution Snap (3s)");
                    UnknownEffects.resolutionSnap(3000);
                    break;
                case 29:
                    // GhostIcon (2s)
                    System.out.println("[HorrorEffects] Event 29: Ghost Icon (2s)");
                    UnknownEffects.ghostIcon(1);
                    break;

                // === Entity Spawns (30-34) ===
                case 30:
                    HorrorEffects.spawnPhantomObserver(world, player);
                    break;
                case 31:
                    HorrorEffects.spawnMirrorDouble(world, player);
                    break;

                // === Advanced Effects (35-44) ===
                case 35:
                    HorrorEffects.triggerPartisanInterference(world, player);
                    break;
                case 36:
                    HorrorEffects.generateDisappearingAnomaly(world, player);
                    break;
                case 37:
                    HorrorEffects.triggerGlitchedWindowTitle(player);
                    break;
                case 38:
                    HorrorEffects.triggerFakeFrameFreeze(player);
                    break;
                case 39:
                    HorrorEffects.triggerViolentShake();
                    break;
                case 40:
                    GlitchManager.triggerImmediateGlitches(10.0F);
                    break;

                // === New effects 41-44 ===
                case 41:
                    // MicrophoneFeedbackScreamer (2.5s)
                    System.out.println("[HorrorEffects] Event 41: Microphone Feedback (2.5s)");
                    triggerMicrophoneFeedbackScreamer();
                    break;
                case 42:
                    // ScreenStrobeDeconstruction (2s)
                    System.out.println("[HorrorEffects] Event 42: Screen Strobe (2s)");
                    ScreenStrobeEffect.trigger();
                    break;
                case 43:
                    // FakeHardwareFreezeAudioLoop (4s)
                    System.out.println("[HorrorEffects] Event 43: Hardware Freeze (4s)");
                    FakeFreezeEffect.trigger();
                    break;
                case 44:
                    // EntityTeleportJumpscareVoid (2.5s)
                    System.out.println("[HorrorEffects] Event 44: Void Drop (2.5s)");
                    TemporalVoidDrop.setPlayer((EntityPlayerSP)player);
                    TemporalVoidDrop.trigger();
                    break;

                // === Combo Effects (45-50) ===
                case 45:
                    // Light Combo
                    HorrorEffects.triggerFootstepEcho(player);
                    HorrorEffects.triggerInventoryGlitchMinor();
                    UnknownEffects.hardwareBeep(37, 300);
                    break;
                case 46:
                    // Medium Combo
                    HorrorEffects.triggerModerateFog();
                    HorrorEffects.checkBehindYouGlitch(world, player);
                    UnknownEffects.jitterWindow(10, 2000);
                    break;
                case 47:
                    // Heavy Combo
                    HorrorEffects.triggerBloodTime();
                    HorrorEffects.triggerHeavyFog();
                    UnknownEffects.corruptGamma(0, 2000);
                    break;
                case 48:
                    // Extreme Combo
                    UnknownEffects.screenMelt(1500, 8);
                    UnknownEffects.ghostOverlay(2000);
                    HorrorEffects.triggerViolentShake();
                    break;
                case 49:
                    // Entity Combo
                    HorrorEffects.spawnPhantomObserver(world, player);
                    HorrorEffects.spawnMirrorDouble(world, player);
                    break;
                case 50:
                    // ФИНАЛ - Спавн бедрокового туннеля
                    HorrorEffects.spawnBedrockTunnel(player);
                    break;

                default:
                    if (player != null) {
                        player.addChatMessage("§cUnknown event ID: " + eventId);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set world and player (for reinitialization)
     */
    public static void setWorld(World w, EntityPlayer p) {
        world = w;
        player = p;
        if (w != null && p != null) {
            init(w, p);
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

            // Открываем чат (если ещё не открыт)
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(new GuiChat());
            }

            // Посимвольно вводим текст с задержкой
            final String finalMsg = message;
            new Thread(new Runnable() { public void run() {
                try {
                    Thread.sleep(100);
                    for (int i = 0; i < finalMsg.length(); i++) {
                        if (mc.currentScreen instanceof GuiChat) {
                            GuiChat chat = (GuiChat) mc.currentScreen;
                            chat.message += finalMsg.charAt(i);
                        }
                        Thread.sleep(50);
                    }
                    // Закрываем чат через 1 секунду после окончания
                    Thread.sleep(1000);
                    if (mc.currentScreen instanceof GuiChat) {
                        mc.displayGuiScreen(null);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }}).start();
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
        System.out.println("[HorrorEffects] Microphone Feedback Screamer triggered (2.5s)");
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
