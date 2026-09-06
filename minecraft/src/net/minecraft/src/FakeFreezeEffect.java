package net.minecraft.src;

import java.util.Random;

/**
 * FakeHardwareFreezeAudioLoop - Фальшивое зависание ПК (4 сек)
 * Картинка застывает, звук замыкается в цикл 50мс
 */
public class FakeFreezeEffect {
    private static boolean freezeActive = false;
    private static long freezeEnd = 0;
    private static Random rand = new Random();

    public static void trigger() {
        System.out.println("[FakeFreezeEffect] Triggering (4s)");
        freezeActive = true;
        freezeEnd = System.currentTimeMillis() + 4000;
        // Звук замыкается в буфере через OpenAL
    }

    public static boolean isActive() {
        if (freezeActive && System.currentTimeMillis() > freezeEnd) {
            freezeActive = false;
        }
        return freezeActive;
    }

    public static void reset() {
        freezeActive = false;
        freezeEnd = 0;
    }
}
