package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Disappearing anomalies - generates lone pillars, empty chests, wells
 * that silently disappear when player moves 30-40 blocks away.
 */
public class DisappearingAnomalies {
    private static final int SPAWN_CHANCE = 5; // 5% chance per chunk
    private static final int DESPAWN_DISTANCE = 40;
    private static final long TICK_INTERVAL = 100L; // Check every 5 ticks

    private static World world;
    private static EntityPlayer player;
    private static long lastTickTime = 0;
    private static Map<String, AnomalyData> activeAnomalies = new HashMap<>();

    private static class AnomalyData {
        int x, y, z;
        int blockType;
        long spawnTime;
        boolean isTracked;

        AnomalyData(int x, int y, int z, int blockType) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockType = blockType;
            this.spawnTime = System.currentTimeMillis();
            this.isTracked = true;
        }
    }

    public static void init(World world, EntityPlayer player) {
        DisappearingAnomalies.world = world;
        DisappearingAnomalies.player = player;
    }

    /**
     * Call this from world tick to generate and manage anomalies.
     */
    public static void tick() {
        if (world == null || player == null) return;
        if (HorrorState.safeMode) return;

        // Only check periodically
        if (System.currentTimeMillis() - lastTickTime < TICK_INTERVAL) return;
        lastTickTime = System.currentTimeMillis();

        // Generate anomalies in loaded chunks
        generateAnomalies();

        // Check for despawning anomalies
        checkDespawn();
    }

    private static void generateAnomalies() {
        // Only generate in new chunks occasionally
        if (Math.random() > 0.001) return; // 0.1% chance per tick

        // Get player's chunk
        int playerChunkX = (int)player.posX >> 4;
        int playerChunkZ = (int)player.posZ >> 4;

        // Generate anomalies in nearby chunks
        for (int cx = -3; cx <= 3; cx++) {
            for (int cz = -3; cz <= 3; cz++) {
                int targetChunkX = playerChunkX + cx;
                int targetChunkZ = playerChunkZ + cz;

                // Check if this chunk already has an anomaly
                String chunkKey = targetChunkX + "," + targetChunkZ;
                if (activeAnomalies.containsKey(chunkKey)) continue;

                // Random chance to place anomaly
                if (Math.random() * 100 < SPAWN_CHANCE) {
                    spawnAnomalyInChunk(targetChunkX, targetChunkZ);
                }
            }
        }
    }

    private static void spawnAnomalyInChunk(int chunkX, int chunkZ) {
        Random rand = new Random(chunkX * 31337 + chunkZ * 7919);

        // Random position within chunk
        int x = chunkX * 16 + rand.nextInt(16);
        int z = chunkZ * 16 + rand.nextInt(16);
        int y = world.getTopSolidOrLiquidBlock(x, z) - 1;

        // Only spawn underground or in appropriate places
        if (y > 60) return; // Too high

        int blockType = Block.cobblestone.blockID; // Default

        // Choose anomaly type
        int anomalyType = rand.nextInt(3);

        switch (anomalyType) {
            case 0: // Lone pillar
                blockType = Block.cobblestone.blockID;
                spawnPillar(x, y, z, 3 + rand.nextInt(5), blockType);
                break;
            case 1: // Empty chest (placed but empty)
                blockType = Block.chest.blockID;
                world.setBlock(x, y, z, blockType);
                break;
            case 2: // Random structure
                blockType = Block.stoneBrick.blockID;
                world.setBlock(x, y, z, blockType);
                break;
        }

        // Track this anomaly
        String key = x + "," + y + "," + z;
        activeAnomalies.put(key, new AnomalyData(x, y, z, blockType));
    }

    private static void spawnPillar(int x, int baseY, int z, int height, int blockType) {
        for (int y = 0; y < height; y++) {
            world.setBlock(x, baseY + y, z, blockType);
        }
    }

    private static void checkDespawn() {
        if (player == null) return;

        String toRemove = null;

        for (Map.Entry<String, AnomalyData> entry : activeAnomalies.entrySet()) {
            AnomalyData anomaly = entry.getValue();

            // Calculate distance to player
            double dx = player.posX - anomaly.x;
            double dy = player.posY - anomaly.y;
            double dz = player.posZ - anomaly.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // Despawn if player is far enough away
            if (distance > DESPAWN_DISTANCE) {
                // Silently remove the anomaly
                if (anomaly.blockType == Block.chest.blockID) {
                    // For chests, also remove the tile entity
                    TileEntity tileentity = world.getBlockTileEntity(anomaly.x, anomaly.y, anomaly.z);
                    if (tileentity != null) {
                        tileentity.invalidate();
                    }
                }

                world.setBlock(anomaly.x, anomaly.y, anomaly.z, 0);
                toRemove = entry.getKey();
                break;
            }
        }

        if (toRemove != null) {
            activeAnomalies.remove(toRemove);
        }
    }

    /**
     * Force despawn of all anomalies (for safe mode).
     */
    public static void despawnAll() {
        for (AnomalyData anomaly : activeAnomalies.values()) {
            world.setBlock(anomaly.x, anomaly.y, anomaly.z, 0);
        }
        activeAnomalies.clear();
    }
}