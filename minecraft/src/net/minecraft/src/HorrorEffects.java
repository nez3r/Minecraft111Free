package net.minecraft.src;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

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

        System.out.println("[HorrorEffects] Triggering Footstep Echo");

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
            e.printStackTrace();
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
        System.out.println("[HorrorEffects] Triggering Inventory Glitch (Minor)");
        inventoryGlitchActive = true;
        inventoryGlitchEnd = System.currentTimeMillis() + 3000; // 3 секунды
    }

    public static void triggerInventoryGlitchMajor() {
        System.out.println("[HorrorEffects] Triggering Inventory Glitch (Major)");
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

        System.out.println("[HorrorEffects] Playing Ambient Sound");

        try {
            world.playSoundEffect(
                player.posX, player.posY, player.posZ,
                "ambient.cave.cave",
                0.8F,
                0.6F + rand.nextFloat() * 0.2F // Немного понизить высоту
            );
            lastAmbientSound = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playLowHum(World world, EntityPlayer player) {
        if (world == null || player == null) return;

        System.out.println("[HorrorEffects] Playing Low Hum");

        try {
            world.playSoundEffect(
                player.posX, player.posY, player.posZ,
                "ambient.cave.cave",
                0.5F,
                0.3F // Очень низкая высота = гул
            );
            lastLowHum = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================================================================
    // 4. DYNAMIC FOG - Динамический туман
    // =============================================================================
    private static float currentFogMultiplier = 1.0F;
    private static long fogEffectEnd = 0;

    public static void increaseFogSlightly() {
        System.out.println("[HorrorEffects] Increasing Fog Slightly (0.7x)");
        currentFogMultiplier = 0.7F;
        fogEffectEnd = System.currentTimeMillis() + 5000;
    }

    public static void triggerModerateFog() {
        System.out.println("[HorrorEffects] Triggering Moderate Fog (0.4x)");
        currentFogMultiplier = 0.4F;
        fogEffectEnd = System.currentTimeMillis() + 8000;
    }

    public static void triggerHeavyFog() {
        System.out.println("[HorrorEffects] Triggering Heavy Fog (0.15x)");
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
        System.out.println("[HorrorEffects] Triggering Blood Time (15s)");
        bloodTimeActive = true;
        bloodTimeEnd = System.currentTimeMillis() + 15000; // 15 секунд
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
        System.out.println("[HorrorEffects] Resetting Blood Time");
        bloodTimeActive = false;
        bloodTimeEnd = 0;
    }

    // =============================================================================
    // 6. VISUAL GLITCH "Behind You" - Глитч "За тобой"
    // =============================================================================
    public static void checkBehindYouGlitch(World world, EntityPlayer player) {
        if (world == null || player == null) return;

        System.out.println("[HorrorEffects] Triggering Behind You Glitch");

        try {
            // Изменить случайные блоки позади игрока
            int range = 8;
            for (int i = 0; i < 5; i++) {
                int x = (int)player.posX + rand.nextInt(range) - range/2;
                int y = (int)player.posY + rand.nextInt(range) - range/2;
                int z = (int)player.posZ + rand.nextInt(range) - range/2;

                int blockId = world.getBlockId(x, y, z);

                // Факелы гаснут или краснеют
                if (blockId == Block.torchWood.blockID) {
                    if (rand.nextBoolean()) {
                        world.setBlockWithNotify(x, y, z, 0); // Убрать факел
                    }
                }

                // Листья опадают
                if (blockId == Block.leaves.blockID) {
                    world.setBlockWithNotify(x, y, z, 0);
                }

                // Каменные кирпичи становятся мшистыми
                if (blockId == Block.stoneBrick.blockID) {
                    world.setBlockMetadataWithNotify(x, y, z, 1); // Mossy
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================================================================
    // 7. PHANTOM OBSERVER - Спавн наблюдателя
    // =============================================================================
    public static void spawnPhantomObserver(World world, EntityPlayer player) {
        if (world == null || player == null) return;

        System.out.println("[HorrorEffects] Spawning Phantom Observer");

        try {
            PhantomObserver.spawnNearPlayer(world, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================================================================
    // 8. MIRROR DOUBLE - Спавн двойника
    // =============================================================================
    public static void spawnMirrorDouble(World world, EntityPlayer player) {
        if (world == null || player == null) return;

        System.out.println("[HorrorEffects] Spawning Mirror Double");

        try {
            MirrorDouble.spawnDouble(world, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================================================================
    // 9. PARTISAN INTERFERENCE - Партизанская интерференция
    // =============================================================================
    public static void triggerPartisanInterference(World world, EntityPlayer player) {
        if (world == null || player == null) return;

        System.out.println("[HorrorEffects] Triggering Partisan Interference");

        try {
            PartisanInterference.triggerInterference();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================================================================
    // 10. DISAPPEARING ANOMALIES - Исчезающие аномалии
    // =============================================================================
    public static void generateDisappearingAnomaly(World world, EntityPlayer player) {
        if (world == null || player == null) return;

        System.out.println("[HorrorEffects] Generating Disappearing Anomaly");

        try {
            DisappearingAnomalies.generateAnomaly();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================================================================
    // 11. DYNAMIC WINDOW TITLE - Динамический заголовок окна
    // =============================================================================
    public static void triggerGlitchedWindowTitle(EntityPlayer player) {
        if (player == null) return;

        System.out.println("[HorrorEffects] Triggering Glitched Window Title");

        try {
            DynamicWindowTitle.triggerGlitchedTitle(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================================================================
    // 12. FAKE FRAME FREEZE - Ложное зависание кадра
    // =============================================================================
    public static void triggerFakeFrameFreeze(EntityPlayer player) {
        if (player == null) return;

        System.out.println("[HorrorEffects] Triggering Fake Frame Freeze");

        try {
            FakeFrameFreeze.triggerFreeze(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================================================================
    // 13. VIOLENT SHAKE - Сильная тряска экрана
    // =============================================================================
    public static void triggerViolentShake() {
        System.out.println("[HorrorEffects] Triggering Violent Shake");

        try {
            GlitchManager.triggerViolentShake();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================================================================
    // 14. BEDROCK TUNNEL - Спавн бедрокового туннеля
    // =============================================================================
    public static void spawnBedrockTunnel(EntityPlayer player) {
        if (player == null) return;

        System.out.println("[HorrorEffects] Spawning Bedrock Tunnel");

        try {
            WorldGenBedrockTunnel.generateNearPlayer(player);
        } catch (Exception e) {
            e.printStackTrace();
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
