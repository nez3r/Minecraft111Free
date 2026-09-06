package net.minecraft.src;

/**
 * ScreenStrobeEffect - Стробоскоп с подменой модели (2 сек)
 * Чередование чёрного кадра и кадра с битыми текстурами
 */
public class ScreenStrobeEffect {
    private static boolean strobeActive = false;
    private static long strobeEnd = 0;
    private static boolean blackFrame = false;

    public static void trigger() {
        System.out.println("[ScreenStrobeEffect] Triggering (2s)");
        strobeActive = true;
        strobeEnd = System.currentTimeMillis() + 2000;
        blackFrame = false;
    }

    public static boolean isActive() {
        if (System.currentTimeMillis() > strobeEnd) {
            strobeActive = false;
        }
        return strobeActive;
    }

    public static boolean shouldRenderBlack() {
        if (!isActive()) return false;
        // Мигание каждые 50мс
        long elapsed = System.currentTimeMillis() % 100;
        return elapsed < 50;
    }

    public static void reset() {
        strobeActive = false;
        strobeEnd = 0;
    }
}
