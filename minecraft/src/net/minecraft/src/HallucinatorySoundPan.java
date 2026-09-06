package net.minecraft.src;

import java.util.Random;

/**
 * HallucinatorySoundPan - Психоакустическое смещение звука
 * Стереопанорама резко "крутится" вокруг головы игрока
 * Звуки воспроизводятся только в одном ухе и перетекают в другое
 */
public class HallucinatorySoundPan {
    private static boolean active = false;
    private static long endTime = 0;
    private static long lastSoundTime = 0;
    private static final long SOUND_INTERVAL = 2000L; // 2 секунды между звуками

    private static EntityPlayer player;
    private static float currentPan = 0.5F;
    private static float panDirection = 0.02F; // Скорость изменения пана

    private static Random rand = new Random();

    /**
     * Активировать эффект
     * @param duration Длительность в миллисекундах
     */
    public static void trigger(EntityPlayer p, long duration) {
        if (p == null) return;

        player = p;
        active = true;
        endTime = System.currentTimeMillis() + duration;
        currentPan = 0.5F;
        panDirection = 0.02F + rand.nextFloat() * 0.03F;

        System.out.println("[HallucinatorySoundPan] Triggering sound pan effect");
    }

    /**
     * Тик эффекта - вызывать каждый кадр
     */
    public static void tick() {
        if (!active || player == null) return;
        if (HorrorState.safeMode) {
            active = false;
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Проверить, не закончился ли эффект
        if (currentTime >= endTime) {
            active = false;
            return;
        }

        // Обновить позицию пана (крутится)
        currentPan += panDirection;
        if (currentPan > 1.0F || currentPan < 0.0F) {
            panDirection = -panDirection;
            currentPan = Math.max(0.0F, Math.min(1.0F, currentPan));
        }

        // Воспроизвести звук с текущим паном
        if (currentTime - lastSoundTime >= SOUND_INTERVAL) {
            playDisorientedSound();
            lastSoundTime = currentTime;
        }
    }

    /**
     * Воспроизвести искажённый звук
     */
    private static void playDisorientedSound() {
        if (player == null || player.worldObj == null) return;

        // Выбрать случайный звук
        String[] sounds = {"ambient.cave.cave", "step.stone", "step.wood", "random.sizzle"};
        String sound = sounds[rand.nextInt(sounds.length)];

        // Ультранизкая частота для дискомфорта (20-30 Гц)
        float basePitch = 0.1F + rand.nextFloat() * 0.05F;

        // Воспроизвести звук с паном
        player.worldObj.playSoundEffect(
            player.posX, player.posY, player.posZ,
            sound,
            0.3F, // Громкость (тихо, чтобы не раздражать слишком сильно)
            basePitch
        );

        // Второй звук в другом ухе (через небольшую задержку)
        if (rand.nextFloat() < 0.3F) {
            final float otherPan = 1.0F - currentPan;
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    if (player != null && player.worldObj != null) {
                        player.worldObj.playSoundEffect(
                            player.posX, player.posY, player.posZ,
                            sound,
                            0.2F,
                            basePitch * 0.9F
                        );
                    }
                } catch (Exception e) {}
            }).start();
        }

        System.out.println("[HallucinatorySoundPan] Playing sound with pan: " + currentPan);
    }

    /**
     * Получить текущий пан (для интеграции с системой звука)
     */
    public static float getCurrentPan() {
        return currentPan;
    }

    /**
     * Проверить, активен ли эффект
     */
    public static boolean isActive() {
        return active && System.currentTimeMillis() < endTime;
    }

    /**
     * Сбросить эффект
     */
    public static void reset() {
        active = false;
        currentPan = 0.5F;
        panDirection = 0.02F;
    }

    /**
     * Установить игрока
     */
    public static void setPlayer(EntityPlayer p) {
        player = p;
    }
}
