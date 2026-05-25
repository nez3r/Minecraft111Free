package net.minecraft.src;

import java.util.List;
import java.util.Random;

/**
 * Chunk provider for bedrock tunnel dimension
 * Generates a 5x5 bedrock tunnel with torches
 */
public class ChunkProviderTunnel implements IChunkProvider {
    private Random rand;
    private World worldObj;

    public ChunkProviderTunnel(World world, long seed) {
        this.worldObj = world;
        this.rand = new Random(seed);
    }

    public Chunk provideChunk(int chunkX, int chunkZ) {
        Chunk chunk = new Chunk(this.worldObj, chunkX, chunkZ);
        byte[] blocks = chunk.blocks;

        // Fill entire chunk with air initially
        for(int i = 0; i < blocks.length; i++) {
            blocks[i] = 0;
        }

        // Generate tunnel along X axis (Z=0, centered at Y=64)
        if(chunkZ == 0) {
            generateTunnel(chunk, chunkX);
        }

        // Generate end room at X=19 (chunk 19, 304 blocks from spawn)
        if(chunkX == 19 && chunkZ >= -1 && chunkZ <= 1) {
            generateEndRoom(chunk, chunkX, chunkZ);
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    private void generateTunnel(Chunk chunk, int chunkX) {
        int centerY = 64;
        int centerZ = 0;

        // 5x5 tunnel
        for(int x = 0; x < 16; x++) {
            for(int y = centerY - 2; y <= centerY + 2; y++) {
                for(int z = centerZ - 2; z <= centerZ + 2; z++) {
                    int localZ = z + 8; // Offset to chunk coordinates

                    if(localZ >= 0 && localZ < 16) {
                        // Bedrock walls, floor, ceiling
                        if(y == centerY - 2 || y == centerY + 2 ||
                           z == centerZ - 2 || z == centerZ + 2) {
                            chunk.setBlockID(x, y, localZ, Block.bedrock.blockID);
                        } else {
                            // Air inside tunnel
                            chunk.setBlockID(x, y, localZ, 0);
                        }
                    }
                }
            }

            // Place torches every 8 blocks on the walls
            int globalX = chunkX * 16 + x;
            if(globalX % 8 == 0 && globalX > 0) {
                int localZ = centerZ + 8;
                if(localZ >= 0 && localZ < 16) {
                    // Torch on north wall
                    chunk.setBlockID(x, centerY, centerZ - 1 + 8, Block.torchWood.blockID);
                    chunk.setBlockMetadata(x, centerY, centerZ - 1 + 8, 4); // South facing

                    // Torch on south wall
                    chunk.setBlockID(x, centerY, centerZ + 1 + 8, Block.torchWood.blockID);
                    chunk.setBlockMetadata(x, centerY, centerZ + 1 + 8, 3); // North facing
                }
            }
        }
    }

    private void generateEndRoom(Chunk chunk, int chunkX, int chunkZ) {
        int centerY = 64;

        // 10x10x6 room at the end of tunnel
        int roomStartX = 19 * 16; // Global X - start at chunk 19
        int roomEndX = 19 * 16 + 10; // 10 blocks wide
        int roomStartZ = -5;
        int roomEndZ = 5;
        int roomFloorY = centerY - 3;
        int roomCeilY = centerY + 3;

        int chunkStartX = chunkX * 16;
        int chunkStartZ = chunkZ * 16;

        for(int x = 0; x < 16; x++) {
            int globalX = chunkStartX + x;
            if(globalX < roomStartX || globalX >= roomEndX) continue;

            for(int z = 0; z < 16; z++) {
                int globalZ = chunkStartZ + z;
                if(globalZ < roomStartZ || globalZ >= roomEndZ) continue;

                for(int y = roomFloorY; y <= roomCeilY; y++) {
                    // Bedrock walls (floor, ceiling, all 4 walls including back wall)
                    if(y == roomFloorY || y == roomCeilY ||
                       globalZ == roomStartZ || globalZ == roomEndZ - 1 ||
                       globalX == roomStartX || globalX == roomEndX - 1) {
                        chunk.setBlockID(x, y, z, Block.bedrock.blockID);
                    } else {
                        // Air inside
                        chunk.setBlockID(x, y, z, 0);
                    }
                }
            }
        }

        // Place chest at center of back wall (not in the middle of room)
        int chestX = roomEndX - 2; // 2 blocks from back wall
        int chestZ = (roomStartZ + roomEndZ) / 2;
        int chestY = roomFloorY + 1;

        int localChestX = chestX - chunkStartX;
        int localChestZ = chestZ - chunkStartZ;

        if(localChestX >= 0 && localChestX < 16 && localChestZ >= 0 && localChestZ < 16) {
            chunk.setBlockID(localChestX, chestY, localChestZ, Block.chest.blockID);
        }
    }

    public boolean chunkExists(int chunkX, int chunkZ) {
        return true;
    }

    public Chunk loadChunk(int chunkX, int chunkZ) {
        return this.provideChunk(chunkX, chunkZ);
    }

    public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ) {
        // Place chest contents after chunk generation
        int chestX = 19 * 16 + 8; // 2 blocks from back wall
        int chestZ = 0;
        int chestY = 62; // roomFloorY + 1 = 61 + 1

        if(chunkX == 19 && chunkZ == 0) {
            // Wait a tick for tile entity to be created
            final int finalX = chestX;
            final int finalY = chestY;
            final int finalZ = chestZ;

            // Schedule chest filling for next tick
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(100); // Wait 100ms for tile entity creation
                        TileEntityChest chest = (TileEntityChest)worldObj.getBlockTileEntity(finalX, finalY, finalZ);
                        if(chest != null) {
                            // Fill with 64 diamonds in center slot
                            chest.setInventorySlotContents(13, new ItemStack(Item.diamond, 64));
                            // Mark this as the tunnel chest
                            chest.isTunnelChest = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public boolean saveChunks(boolean saveAll, IProgressUpdate progressUpdate) {
        return true;
    }

    public boolean unload100OldestChunks() {
        return false;
    }

    public boolean canSave() {
        return true;
    }

    public String makeString() {
        return "TunnelLevelSource";
    }

    public List getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
        return null;
    }

    public List func_40377_a(EnumCreatureType creatureType, int x, int y, int z) {
        return null;
    }

    public ChunkPosition findClosestStructure(World world, String structureName, int x, int y, int z) {
        return null;
    }

    public ChunkPosition func_40376_a(World world, String structureName, int x, int y, int z) {
        return null;
    }
}
