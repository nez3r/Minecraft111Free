package net.minecraft.src;

import java.util.Random;

/**
 * WorldCorruptorGenerator - Структурное гниение чанков
 * Вырезает квадратные дыры до бедрока, превращает воду в лаву, спавнит перевёрнутые деревья
 */
public class WorldCorruptorGenerator {
    private static final int RADIUS = 20;
    private static final long CORRUPT_INTERVAL = 5000L; // 5 секунд между изменениями

    private static World world;
    private static EntityPlayer player;
    private static long lastCorruptTime = 0;
    private static Random rand = new Random();

    /**
     * Инициализация
     */
    public static void init(World w, EntityPlayer p) {
        world = w;
        player = p;
    }

    /**
     * Тик генератора - вызывать каждый кадр
     */
    public static void tick() {
        if (world == null || player == null) return;
        if (HorrorState.safeMode) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCorruptTime < CORRUPT_INTERVAL) return;
        lastCorruptTime = currentTime;

        // Случайный тип искажения
        int corruptionType = rand.nextInt(3);

        switch (corruptionType) {
            case 0:
                generateHoleToBedrock();
                break;
            case 1:
                corruptWaterToLava();
                break;
            case 2:
                spawnInvertedTree();
                break;
        }
    }

    /**
     * Вырезать квадратную дыру до бедрока
     */
    private static void generateHoleToBedrock() {
        // Случайная позиция в радиусе 20 блоков от игрока
        int x = (int)player.posX + rand.nextInt(RADIUS * 2) - RADIUS;
        int z = (int)player.posZ + rand.nextInt(RADIUS * 2) - RADIUS;

        // Не слишком близко к игроку
        double dist = Math.sqrt(Math.pow(x - player.posX, 2) + Math.pow(z - player.posZ, 2));
        if (dist < 5) return;

        int size = 3 + rand.nextInt(3); // 3-5 блоков в ширину

        System.out.println("[WorldCorruptor] Generating hole to bedrock at " + x + ", " + z);

        // Вырезать дыру сверху донизу
        for (int y = (int)player.posY + 10; y > 0; y--) {
            for (int dx = 0; dx < size; dx++) {
                for (int dz = 0; dz < size; dz++) {
                    world.setBlock(x + dx, y, z + dz, 0);
                }
            }
        }
    }

    /**
     * Превратить воду в лаву (или чёрную текстуру)
     */
    private static void corruptWaterToLava() {
        int x = (int)player.posX + rand.nextInt(RADIUS * 2) - RADIUS;
        int y = (int)player.posY + rand.nextInt(10) - 5;
        int z = (int)player.posZ + rand.nextInt(RADIUS * 2) - RADIUS;

        int blockId = world.getBlockId(x, y, z);

        if (blockId == Block.waterStill.blockID || blockId == Block.waterMoving.blockID) {
            System.out.println("[WorldCorruptor] Corrupting water to lava at " + x + ", " + y + ", " + z);
            world.setBlock(x, y, z, Block.lavaStill.blockID);
        }
    }

    /**
     * Спавнить перевёрнутое дерево кроной вниз
     */
    private static void spawnInvertedTree() {
        int x = (int)player.posX + rand.nextInt(RADIUS * 2) - RADIUS;
        int z = (int)player.posZ + rand.nextInt(RADIUS * 2) - RADIUS;
        int y = world.getTopSolidOrLiquidBlock(x, z);

        // Не слишком близко к игроку
        double dist = Math.sqrt(Math.pow(x - player.posX, 2) + Math.pow(z - player.posZ, 2));
        if (dist < 8) return;

        System.out.println("[WorldCorruptor] Spawning inverted tree at " + x + ", " + y + ", " + z);

        // Ствол (короткий, 3-4 блока)
        int trunkHeight = 3 + rand.nextInt(2);
        for (int i = 0; i < trunkHeight; i++) {
            world.setBlock(x, y + i, z, Block.wood.blockID);
        }

        // Крона вниз (перевёрнутая)
        int leavesStart = y + trunkHeight;
        for (int dy = 0; dy < 4; dy++) {
            int radius = dy < 2 ? 2 : 1;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx == 0 && dz == 0 && dy < 2) continue; // Не заменять ствол
                    world.setBlock(x + dx, leavesStart - dy * 2, z + dz, Block.leaves.blockID);
                }
            }
        }
    }

    /**
     * Установить мир и игрока
     */
    public static void setWorld(World w, EntityPlayer p) {
        world = w;
        player = p;
    }

    /**
     * Сбросить генератор
     */
    public static void reset() {
        lastCorruptTime = 0;
    }
}
