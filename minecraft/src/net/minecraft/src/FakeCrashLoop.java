package net.minecraft.src;

/**
 * FakeCrashLoop - Фальшивый вылет с восстановлением
 * Закрывает окно игры, показывает Java error dialog, затем возвращает игрока
 */
public class FakeCrashLoop {
    private static boolean crashTriggered = false;
    private static EntityPlayer player;
    private static World world;
    private static boolean waitingForPlayer = false;

    /**
     * Вызвать фальшивый краш
     */
    public static void trigger(EntityPlayer p, World w) {
        if (crashTriggered || waitingForPlayer) return;

        player = p;
        world = w;
        crashTriggered = true;

        System.out.println("[FakeCrashLoop] Triggering fake crash");

        // Запустить в отдельном потоке
        new Thread(() -> {
            try {
                // Небольшая задержка
                Thread.sleep(500);

                // Здесь мы должны закрыть окно, но это делается в Java коде
                // Поскольку у нас нет прямого доступа к Display, просто покажем диалог
                waitingForPlayer = true;

                // Показать фальшивый Java error
                // Это не будет работать в headless режиме, но создаст видимость краша
                System.err.println("Java Runtime Environment: Java HotSpot(TM) Client VM");
                System.err.println("Exception in thread \"Client thread\" java.lang.RuntimeException");
                System.err.println("  at net.minecraft.client.Minecraft.run(Minecraft.java:???");
                System.err.println("  at java.lang.Thread.run(Thread.java:745)");
                System.err.println("");
                System.err.println("# A fatal error has been detected by the Java Runtime Environment");
                System.err.println("# EXCEPTION_ACCESS_VIOLATION (0xc0000005)");

                Thread.sleep(3000);

                // Симулировать "восстановление"
                waitingForPlayer = false;
                crashTriggered = false;

                // Спавнить сущность рядом с игроком
                if (player != null && world != null) {
                    spawnScaryEntity();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Спавнить пугающую сущность рядом с игроком
     */
    private static void spawnScaryEntity() {
        if (player == null || world == null) return;

        try {
            // Спавн Entity404 или крипера рядом
            double angle = Math.toRadians(player.rotationYaw + 180);
            double offsetX = -Math.sin(angle) * 3;
            double offsetZ = Math.cos(angle) * 3;

            Entity404 stalker = new Entity404(world);
            stalker.setPosition(player.posX + offsetX, player.posY, player.posZ + offsetZ);
            world.spawnEntityInWorld(stalker);

            // Установить как сталкера
            HorrorState.stalkerEntity = stalker;
            HorrorState.stalkerActive = true;

            System.out.println("[FakeCrashLoop] Spawned scary entity");

        } catch (Exception e) {
            System.err.println("[FakeCrashLoop] Failed to spawn entity: " + e.getMessage());
        }
    }

    /**
     * Проверить, активен ли краш
     */
    public static boolean isActive() {
        return crashTriggered || waitingForPlayer;
    }

    /**
     * Сбросить состояние
     */
    public static void reset() {
        crashTriggered = false;
        waitingForPlayer = false;
    }

    /**
     * Установить мир и игрока
     */
    public static void setWorld(World w, EntityPlayer p) {
        world = w;
        player = p;
    }
}
