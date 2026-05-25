package net.minecraft.src;

import java.util.Random;

/**
 * Generates rare Error404 house with jump scare
 * 1 house per 500 chunks
 */
public class WorldGenError404House extends WorldGenerator {

    public boolean generate(World world, Random random, int x, int y, int z) {
        // Find ground level
        y = world.getHeightValue(x, z);

        // Build 5x5 wooden house
        // Floor
        for(int dx = 0; dx < 5; dx++) {
            for(int dz = 0; dz < 5; dz++) {
                world.setBlock(x + dx, y - 1, z + dz, Block.planks.blockID);
            }
        }

        // Walls (3 blocks high)
        for(int dy = 0; dy < 3; dy++) {
            // North wall
            for(int dx = 0; dx < 5; dx++) {
                world.setBlock(x + dx, y + dy, z, Block.planks.blockID);
            }
            // South wall
            for(int dx = 0; dx < 5; dx++) {
                world.setBlock(x + dx, y + dy, z + 4, Block.planks.blockID);
            }
            // West wall
            for(int dz = 1; dz < 4; dz++) {
                world.setBlock(x, y + dy, z + dz, Block.planks.blockID);
            }
            // East wall
            for(int dz = 1; dz < 4; dz++) {
                world.setBlock(x + 4, y + dy, z + dz, Block.planks.blockID);
            }
        }

        // Door on south wall
        world.setBlock(x + 2, y, z + 4, 0);
        world.setBlock(x + 2, y + 1, z + 4, 0);

        // Roof (flat)
        for(int dx = 0; dx < 5; dx++) {
            for(int dz = 0; dz < 5; dz++) {
                world.setBlock(x + dx, y + 3, z + dz, Block.planks.blockID);
            }
        }

        // Clear inside
        for(int dx = 1; dx < 4; dx++) {
            for(int dz = 1; dz < 4; dz++) {
                for(int dy = 0; dy < 3; dy++) {
                    world.setBlock(x + dx, y + dy, z + dz, 0);
                }
            }
        }

        // Place torch inside
        world.setBlock(x + 2, y + 1, z + 2, Block.torchWood.blockID);

        // Place sign outside
        world.setBlock(x + 2, y, z + 5, Block.signPost.blockID);
        TileEntitySign sign = (TileEntitySign)world.getBlockTileEntity(x + 2, y, z + 5);
        if(sign != null) {
            sign.signText[0] = "";
            sign.signText[1] = "ERROR";
            sign.signText[2] = "404";
            sign.signText[3] = "";
        }

        // Store house location for entity spawn
        HorrorState.error404HouseX = x + 2;
        HorrorState.error404HouseY = y + 1;
        HorrorState.error404HouseZ = z + 2;

        return true;
    }
}
