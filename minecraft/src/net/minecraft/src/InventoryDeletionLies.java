package net.minecraft.src;

import java.util.Random;

/**
 * InventoryDeletionLies - Фальшивое уничтожение инвентаря
 * Слоты хотбара очищаются со звуком сгорания, но предметы не удаляются на самом деле
 */
public class InventoryDeletionLies {
    private static boolean active = false;
    private static long endTime = 0;
    private static int currentSlot = 0;
    private static long lastDeleteTime = 0;
    private static final long DELETE_INTERVAL = 800L; // 800мс между слотами

    private static EntityPlayer player;
    private static Random rand = new Random();
    private static boolean[] hiddenSlots = new boolean[9];

    /**
     * Активировать эффект фальшивого удаления
     * @param duration Длительность эффекта в миллисекундах
     */
    public static void trigger(EntityPlayer p, long duration) {
        if (p == null) return;

        player = p;
        active = true;
        endTime = System.currentTimeMillis() + duration;
        currentSlot = 0;
        lastDeleteTime = 0;

        System.out.println("[InventoryDeletionLies] Triggering fake inventory deletion");
    }

    /**
     * Тик эффекта - вызывать каждый кадр
     */
    public static void tick() {
        if (!active || player == null) return;
        if (player.worldObj == null || player.isDead) {
            reset();
            return;
        }
        if (HorrorState.safeMode) {
            active = false;
            clearHiddenSlots();
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Проверить, не закончился ли эффект
        if (currentTime >= endTime) {
            active = false;
            return;
        }

        // Удалять слоты с интервалом
        if (currentTime - lastDeleteTime >= DELETE_INTERVAL && currentSlot < 9) {
            fakeDeleteSlot(currentSlot);
            currentSlot++;
            lastDeleteTime = currentTime;

            // Звук сгорания
            if (player.worldObj != null) {
                player.worldObj.playSoundEffect(
                    player.posX, player.posY, player.posZ,
                    "random.fizz",
                    0.8F,
                    0.5F + rand.nextFloat() * 0.3F
                );
            }
        }
    }

    /**
     * Фальшивое удаление слота
     */
    private static void fakeDeleteSlot(int slot) {
        // Визуально очищаем слот, но не удаляем предмет
        // Это делает слот "невидимым", но предмет остаётся

        // В реальности мы не можем изменить рендеринг инвентаря отсюда,
        // поэтому просто записываем в лог и воспроизводим звук
        System.out.println("[InventoryDeletionLies] Faking deletion of slot " + slot);
        if (slot >= 0 && slot < hiddenSlots.length) hiddenSlots[slot] = true;
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
        currentSlot = 0;
        lastDeleteTime = 0;
        clearHiddenSlots();
        endTime = 0;
    }

    /**
     * Установить игрока
     */
    public static void setPlayer(EntityPlayer p) {
        if (player != p) clearHiddenSlots();
        player = p;
    }

    public static boolean isSlotHidden(Slot slot) {
        if (!isActive() || player == null || slot == null || slot.inventory != player.inventory) return false;
        if (slot.slotNumber < 36 || slot.slotNumber >= 45) return false;
        return hiddenSlots[slot.slotNumber - 36];
    }

    private static void clearHiddenSlots() {
        for (int i = 0; i < hiddenSlots.length; i++) hiddenSlots[i] = false;
    }
}
