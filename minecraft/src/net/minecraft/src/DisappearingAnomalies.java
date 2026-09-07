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
    private static final long TICK_INTERVAL = 5000L; // INCREASED from 100ms to 5 seconds - much less frequent

    private static World world;
    private static EntityPlayer player;
    private static long lastTickTime = 0;
    private static Map<String, AnomalyData> activeAnomalies = new HashMap<String, AnomalyData>();

    private static class AnomalyData {
        int x, y, z;
        int blockType;
        int originalBlockType;
        long spawnTime;
        boolean isTracked;

        AnomalyData(int x, int y, int z, int blockType, int originalBlockType) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockType = blockType;
            this.originalBlockType = originalBlockType;
            this.spawnTime = System.currentTimeMillis();
            this.isTracked = true;
        }
    }

    public static void init(World world, EntityPlayer player) {
        setWorld(world, player);
    }

    /**
     * Call this from world tick to generate and manage anomalies.
     */
    public static void tick() {
        if (world == null || player == null || player.worldObj != world) {
            reset();
            return;
        }
        if (HorrorState.safeMode) {
            despawnAll();
            return;
        }

        // Apply speed multiplier
        long effectiveInterval = (long)(TICK_INTERVAL / HorrorState.horrorSpeedMultiplier);
        if (System.currentTimeMillis() - lastTickTime < effectiveInterval) return;
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

                        if (hasAnomalyInChunk(targetChunkX, targetChunkZ)) continue;

                // Random chance to place anomaly
                if (Math.random() * 100 < SPAWN_CHANCE) {
                    spawnAnomalyInChunk(targetChunkX, targetChunkZ);
                }
            }
        }
    }

    private static void spawnAnomalyInChunk(int chunkX, int chunkZ) {
        Random rand = new Random(chunkX * 31337L + chunkZ * 7919L);

        // Random position within chunk
        int x = chunkX * 16 + rand.nextInt(16);
        int z = chunkZ * 16 + rand.nextInt(16);
        int y = world.getTopSolidOrLiquidBlock(x, z) - 1;

        // Only spawn underground or in appropriate places
        if (y > 60) return; // Too high
        if (world.getBlockId(x, y, z) != 0) return;

        int blockType = Block.cobblestone.blockID; // Default
        int originalBlockType = world.getBlockId(x, y, z);

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
        activeAnomalies.put(key, new AnomalyData(x, y, z, blockType, originalBlockType));
    }

    private static void spawnPillar(int x, int baseY, int z, int height, int blockType) {
        for (int y = 0; y < height; y++) {
            if (world.getBlockId(x, baseY + y, z) == 0) {
                world.setBlock(x, baseY + y, z, blockType);
            }
        }
    }

    private static void checkDespawn() {
        if (player == null) return;

        java.util.ArrayList<String> toRemove = new java.util.ArrayList<String>();

        for (Map.Entry<String, AnomalyData> entry : activeAnomalies.entrySet()) {
            AnomalyData anomaly = entry.getValue();

            // Calculate distance to player
            double dx = player.posX - anomaly.x;
            double dy = player.posY - anomaly.y;
            double dz = player.posZ - anomaly.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // Despawn if player is far enough away
            if (distance > DESPAWN_DISTANCE) {
                // Restore only blocks still owned by the anomaly; never erase
                // a player's replacement block or an unrelated tile entity.
                if (world.getBlockId(anomaly.x, anomaly.y, anomaly.z) == anomaly.blockType) {
                    world.setBlock(anomaly.x, anomaly.y, anomaly.z, anomaly.originalBlockType);
                }
                for (int yy = anomaly.y + 1; yy < anomaly.y + 8; yy++) {
                    if (world.getBlockId(anomaly.x, yy, anomaly.z) == anomaly.blockType) {
                        world.setBlock(anomaly.x, yy, anomaly.z, 0);
                    }
                }
                toRemove.add(entry.getKey());
            }
        }

        for (int i = 0; i < toRemove.size(); i++) {
            activeAnomalies.remove(toRemove.get(i));
        }
    }

    /**
     * Force despawn of all anomalies (for safe mode).
     */
    public static void despawnAll() {
        if (world == null) {
            activeAnomalies.clear();
            return;
        }
        for (AnomalyData anomaly : activeAnomalies.values()) {
            if (world.getBlockId(anomaly.x, anomaly.y, anomaly.z) == anomaly.blockType) {
                world.setBlock(anomaly.x, anomaly.y, anomaly.z, anomaly.originalBlockType);
            }
            for (int yy = anomaly.y + 1; yy < anomaly.y + 8; yy++) {
                if (world.getBlockId(anomaly.x, yy, anomaly.z) == anomaly.blockType) {
                    world.setBlock(anomaly.x, yy, anomaly.z, 0);
                }
            }
        }
        activeAnomalies.clear();
    }

    public static void setWorld(World w, EntityPlayer p) {
        if (world != w) {
            activeAnomalies.clear();
            lastTickTime = 0;
        }
        world = w;
        player = p;
    }

    public static void generateAnomaly() {
        if (world != null && player != null && player.worldObj == world && !HorrorState.safeMode) {
            generateAnomalies();
        }
    }

    private static boolean hasAnomalyInChunk(int chunkX, int chunkZ) {
        for (AnomalyData anomaly : activeAnomalies.values()) {
            if ((anomaly.x >> 4) == chunkX && (anomaly.z >> 4) == chunkZ) return true;
        }
        return false;
    }

    public static void reset() {
        despawnAll();
        world = null;
        player = null;
        lastTickTime = 0;
    }
}
