package net.minecraft.src;

/**
 * Invisible tick handler that runs in adjacent chunks to sabotage:
 * breaks torches, opens/closes doors, places signs with text.
 * Runs when player is in another room, so they return to a changed world.
 */
public class PartisanInterference {
    private static final int CHUNK_RADIUS = 2; // REDUCED from 4 to 2 - less lag
    private static final long TICK_INTERVAL = 5000L; // INCREASED from 100ms to 5 seconds - much less frequent

    private static World world;
    private static EntityPlayer player;
    private static long lastTickTime = 0;

    public static void init(World world, EntityPlayer player) {
        PartisanInterference.world = world;
        PartisanInterference.player = player;
    }

    /**
     * Call this from the player's onUpdate or game tick loop.
     */
    public static void tick() {
        if (world == null || player == null) return;

        // Only act when enough time has passed
        if (System.currentTimeMillis() - lastTickTime < TICK_INTERVAL) return;
        lastTickTime = System.currentTimeMillis();

        // Get player's chunk coordinates
        int playerChunkX = (int)player.posX >> 4;
        int playerChunkZ = (int)player.posZ >> 4;

        // Check chunks in radius for potential modifications
        for (int cx = -CHUNK_RADIUS; cx <= CHUNK_RADIUS; cx++) {
            for (int cz = -CHUNK_RADIUS; cz <= CHUNK_RADIUS; cz++) {
                int targetChunkX = playerChunkX + cx;
                int targetChunkZ = playerChunkZ + cz;

                // Skip if this is the player's current chunk
                if (cx == 0 && cz == 0) continue;

                modifyChunkBlocks(targetChunkX, targetChunkZ);
            }
        }
    }

    private static void modifyChunkBlocks(int chunkX, int chunkZ) {
        // OPTIMIZED: Instead of checking ALL blocks (32,768 per chunk),
        // only check a few random blocks per chunk to reduce lag
        int checksPerChunk = 5; // Only check 5 random blocks instead of all

        for (int i = 0; i < checksPerChunk; i++) {
            int x = (int)(Math.random() * 16);
            int y = (int)(Math.random() * 128);
            int z = (int)(Math.random() * 16);

            int worldX = chunkX * 16 + x;
            int worldZ = chunkZ * 16 + z;

            int blockId = world.getBlockId(worldX, y, worldZ);
            if (blockId == 0) continue; // Air, skip

            modifySingleBlock(worldX, y, worldZ, blockId);
        }
    }

    private static void modifySingleBlock(int x, int y, int z, int blockId) {
        // Only modify if player is far enough away (in another room)
        if (player != null) {
            double distance = player.getDistance(x, y, z);
            // If closest player is more than 24 blocks away, we can modify
            if (distance < 24.0D) return;
        }

        // 30% chance to perform an action on this block
        if (Math.random() < 0.3) {
            performBlockAction(x, y, z, blockId);
        }
    }

    private static void performBlockAction(int x, int y, int z, int blockId) {
        // Randomly choose what to do with this block
        double action = Math.random() * 100;

        if (blockId == Block.torchWood.blockID) {
            // Turn off torch (set to air)
            if (action < 60) {
                world.setBlock(x, y, z, 0);
            }
            // Change to red torch
            else if (action < 80) {
                world.setBlock(x, y, z, Block.torchRed.blockID);
            }
            // Leave as is (20%+)
        } else if (blockId == Block.doorWood.blockID) {
            // Toggle door state - re-place to change open/closed
            if (action < 50) {
                // Place the door block again to toggle metadata
                world.setBlock(x, y, z, Block.doorWood.blockID);
            }
        } else if (blockId == Block.doorSteel.blockID) {
            // Toggle iron door
            if (action < 50) {
                world.setBlock(x, y, z, Block.doorSteel.blockID);
            }
        } else if (blockId == Block.signWall.blockID || blockId == Block.signPost.blockID) {
            // Put text on sign - change sign text
            if (action < 70) {
                setSignText(x, y, z);
            }
        } else if (blockId == Block.stoneBrick.blockID) {
            // Occasionally change to mossy stone brick variant
            if (action < 80 && Math.random() < 0.1) {
                // Place air to remove (placeholder for mossy variant)
                world.setBlock(x, y, z, 0);
            }
        } else if (blockId == Block.leaves.blockID) {
            // Make leaves decay faster / disappear
            if (action < 40) {
                world.setBlock(x, y, z, 0);
            }
        }
        // Other block types can be added here
    }

    private static void setSignText(int x, int y, int z) {
        // Get the tile entity for the sign
        TileEntity tileentity = world.getBlockTileEntity(x, y, z);
        if (tileentity instanceof TileEntitySign) {
            TileEntitySign sign = (TileEntitySign) tileentity;
            String[] messages = new String[4];
            messages[0] = "";
            messages[1] = getRandomSignText();
            messages[2] = "";
            messages[3] = "";
            sign.signText = messages;
            sign.onInventoryChanged();
        }
    }

    private static String getRandomSignText() {
        String[] texts = {
            "you can't hide",
            "look behind you",
            "it's watching",
            "don't sleep",
            "they are here",
            "welcome home",
            "stop looking",
            "gone forever",
            "trapped",
            "no escape",
            "follow me",
            "stay",
            "leave",
            "help",
            "run"
        };
        return texts[(int)(Math.random() * texts.length)];
    }

    public static void setWorld(World w, EntityPlayer p) {
        world = w;
        player = p;
    }

    public static void triggerInterference() {
        // Trigger immediate interference
        if (world != null && player != null) {
            tick();
        }
    }
}
