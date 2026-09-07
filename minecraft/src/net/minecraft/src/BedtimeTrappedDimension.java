package net.minecraft.src;

/**
 * BedtimeTrappedDimension - Сон в чужом измерении
 * При попытке уснуть игрок не просыпается утром, а попадает в бесконечный мир из бедрока
 */
public class BedtimeTrappedDimension {
    private static boolean trapped = false;
    private static int trapTime = 0;
    private static final int TRAP_DURATION = 30000; // 30 секунд в ловушке

    private static World world;
    private static EntityPlayer player;

    /**
     * Проверить, попал ли игрок в ловушку при попытке сна
     */
    public static boolean onSleepAttempt(EntityPlayer p, World w) {
        if (p == null || w == null) return false;

        // 30% шанс попасть в ловушку
        if (Math.random() < 0.3) {
            trapped = true;
            player = p;
            world = w;
            trapTime = 0;

            return true; // Вернуть true, чтобы перехватить сон
        }

        return false; // Разрешить нормальный сон
    }

    /**
     * Тик ловушки - вызывать каждый кадр
     */
    public static void tick() {
        if (!trapped || player == null || world == null) return;
        if (HorrorState.safeMode) {
            trapped = false;
            return;
        }

        trapTime++;

        // Плавное темнение экрана
        if (trapTime < 60) {
            // Постепенно темнеем
            float darkness = trapTime / 60.0F;
            // В реальном рендеринге это будет обработано через EntityRenderer
        }

        // Через 5 секунд (100 тиков) спавнить в измерении
        if (trapTime == 100) {
            teleportToBedrockDimension();
        }

        // Через 30 секунд (600 тиков) освободить игрока
        if (trapTime >= TRAP_DURATION) {
            releasePlayer();
        }
    }

    /**
     * Телепортировать игрока в бедроковое измерение
     */
    private static void teleportToBedrockDimension() {
        // В Minecraft 1.1 нет системы измерений, поэтому имитируем:
        // 1. Заполнить окружение бедроком
        // 2. Убрать небо и звёзды
        // 3. Установить позицию игрока

        int radius = 16;
        int centerX = (int)player.posX;
        int centerY = (int)player.posY;
        int centerZ = (int)player.posZ;

        // Заполнить бокс бедроком
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    // Только на границах и на полу
                    if (Math.abs(x) == radius || Math.abs(z) == radius || Math.abs(y) == radius) {
                        world.setBlock(centerX + x, centerY + y, centerZ + z, Block.bedrock.blockID);
                    }
                }
            }
        }

        // Заполнить пол бедроком
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                world.setBlock(centerX + x, centerY - radius - 1, centerZ + z, Block.bedrock.blockID);
            }
        }

        // Поставить игрока в центр
        player.setPosition(centerX, centerY, centerZ);

    }

    /**
     * Освободить игрока из ловушки
     */
    private static void releasePlayer() {
        // Восстановить нормальную погоду и время суток
        if (world != null) {
            world.worldInfo.setWorldTime(6000); // Утро
        }

        trapped = false;
        trapTime = 0;

    }

    /**
     * Проверить, находится ли игрок в ловушке
     */
    public static boolean isTrapped() {
        return trapped;
    }

    /**
     * Принудительно освободить игрока
     */
    public static void release() {
        trapped = false;
        trapTime = 0;
    }

    /**
     * Установить мир и игрока
     */
    public static void setWorld(World w, EntityPlayer p) {
        world = w;
        player = p;
    }
}