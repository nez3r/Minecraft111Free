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

        // Pan state is consumed by the sound renderer; do not inject sounds here.
    }

    /**
     * Воспроизвести искажённый звук
     */
    private static void playDisorientedSound() {
        lastSoundTime = System.currentTimeMillis();
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
