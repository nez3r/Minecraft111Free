package net.minecraft.src;

import java.util.Random;

/**
 * Generates the portal to tunnel dimension in overworld
 * Spawns 500-2000 blocks from spawn
 */
public class WorldGenPortal404 extends WorldGenerator {

    public boolean generate(World world, Random random, int x, int y, int z) {
        // Find suitable ground level
        while(y > 5 && world.isAirBlock(x, y, z)) {
            y--;
        }

        if(y <= 5) {
            return false;
        }

        y++; // Place on top of ground

        // Build 4x5 obsidian frame (like nether portal)
        // Frame structure:
        // OOO
        // O O
        // O O
        // O O
        // OOO

        // Bottom
        for(int dx = 0; dx < 3; dx++) {
            world.setBlock(x + dx, y, z, Block.obsidian.blockID);
        }

        // Sides
        for(int dy = 1; dy < 4; dy++) {
            world.setBlock(x, y + dy, z, Block.obsidian.blockID);
            world.setBlock(x + 2, y + dy, z, Block.obsidian.blockID);
        }

        // Top
        for(int dx = 0; dx < 3; dx++) {
            world.setBlock(x + dx, y + 4, z, Block.obsidian.blockID);
        }

        // Fill portal blocks
        for(int dy = 1; dy < 4; dy++) {
            world.setBlock(x + 1, y + dy, z, Block.portal404.blockID);
        }

        // Place warning sign
        world.setBlock(x + 1, y, z - 1, Block.signPost.blockID);
        TileEntitySign sign = (TileEntitySign)world.getBlockTileEntity(x + 1, y, z - 1);
        if(sign != null) {
            sign.signText[0] = "";
            sign.signText[1] = "DO NOT";
            sign.signText[2] = "ENTER";
            sign.signText[3] = "";
        }

        return true;
    }
}
