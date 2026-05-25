package net.minecraft.src;

import java.util.Random;

/**
 * Generates abandoned player structures in the world
 * Makes it look like someone was here before
 */
public class WorldGenAbandonedStructures extends WorldGenerator {

    public boolean generate(World world, Random random, int x, int y, int z) {
        // Only generate underground (below y=50)
        if(y > 50) {
            return false;
        }

        int structureType = random.nextInt(6);

        switch(structureType) {
            case 0:
                return generateMinedTunnel(world, random, x, y, z);
            case 1:
                return generateHalfBuiltStructure(world, random, x, y, z);
            case 2:
                return generateRandomChest(world, random, x, y, z);
            case 3:
                return generateCraftingTable(world, random, x, y, z);
            case 4:
                return generateFurnace(world, random, x, y, z);
            case 5:
                return generateSmallFarm(world, random, x, y, z);
        }

        return false;
    }

    private boolean generateMinedTunnel(World world, Random random, int x, int y, int z) {
        // 3-7 block straight tunnel with torches
        int length = 3 + random.nextInt(5);
        int direction = random.nextInt(4); // 0=north, 1=east, 2=south, 3=west

        for(int i = 0; i < length; i++) {
            int dx = 0, dz = 0;
            if(direction == 0) dz = -i;
            else if(direction == 1) dx = i;
            else if(direction == 2) dz = i;
            else dx = -i;

            // Clear 2x2 tunnel
            world.setBlock(x + dx, y, z + dz, 0);
            world.setBlock(x + dx, y + 1, z + dz, 0);

            // Place torch every 3 blocks
            if(i % 3 == 0 && i > 0) {
                world.setBlock(x + dx, y + 1, z + dz, Block.torchWood.blockID);
            }
        }

        return true;
    }

    private boolean generateHalfBuiltStructure(World world, Random random, int x, int y, int z) {
        // Small incomplete cobblestone structure
        // 3x3 base, only 2 walls built
        for(int dx = 0; dx < 3; dx++) {
            for(int dz = 0; dz < 3; dz++) {
                // Floor
                world.setBlock(x + dx, y - 1, z + dz, Block.cobblestone.blockID);

                // Clear inside
                world.setBlock(x + dx, y, z + dz, 0);
                world.setBlock(x + dx, y + 1, z + dz, 0);
            }
        }

        // Two walls only
        for(int dy = 0; dy < 2; dy++) {
            world.setBlock(x, y + dy, z, Block.cobblestone.blockID);
            world.setBlock(x, y + dy, z + 1, Block.cobblestone.blockID);
            world.setBlock(x, y + dy, z + 2, Block.cobblestone.blockID);

            world.setBlock(x + 1, y + dy, z, Block.cobblestone.blockID);
            world.setBlock(x + 2, y + dy, z, Block.cobblestone.blockID);
        }

        return true;
    }

    private boolean generateRandomChest(World world, Random random, int x, int y, int z) {
        // Find solid ground
        while(y > 5 && world.isAirBlock(x, y - 1, z)) {
            y--;
        }

        if(y <= 5) return false;

        // Place chest with random items
        world.setBlock(x, y, z, Block.chest.blockID);
        TileEntityChest chest = (TileEntityChest)world.getBlockTileEntity(x, y, z);

        if(chest != null) {
            // Add some random items
            int itemCount = 1 + random.nextInt(3);
            for(int i = 0; i < itemCount; i++) {
                int slot = random.nextInt(27);
                int itemType = random.nextInt(5);

                ItemStack stack = null;
                switch(itemType) {
                    case 0: stack = new ItemStack(Item.bread, 1 + random.nextInt(3)); break;
                    case 1: stack = new ItemStack(Item.coal, 3 + random.nextInt(5)); break;
                    case 2: stack = new ItemStack(Block.cobblestone, 10 + random.nextInt(20)); break;
                    case 3: stack = new ItemStack(Item.stick, 5 + random.nextInt(10)); break;
                    case 4: stack = new ItemStack(Block.dirt, 5 + random.nextInt(15)); break;
                }

                if(stack != null) {
                    chest.setInventorySlotContents(slot, stack);
                }
            }
        }

        return true;
    }

    private boolean generateCraftingTable(World world, Random random, int x, int y, int z) {
        // Find solid ground
        while(y > 5 && world.isAirBlock(x, y - 1, z)) {
            y--;
        }

        if(y <= 5) return false;

        // Place crafting table
        world.setBlock(x, y, z, Block.workbench.blockID);

        // Maybe add a torch nearby
        if(random.nextBoolean()) {
            world.setBlock(x + 1, y, z, Block.torchWood.blockID);
        }

        return true;
    }

    private boolean generateFurnace(World world, Random random, int x, int y, int z) {
        // Find solid ground
        while(y > 5 && world.isAirBlock(x, y - 1, z)) {
            y--;
        }

        if(y <= 5) return false;

        // Place furnace with some coal
        world.setBlock(x, y, z, Block.stoneOvenIdle.blockID);
        TileEntityFurnace furnace = (TileEntityFurnace)world.getBlockTileEntity(x, y, z);

        if(furnace != null) {
            // Add coal as fuel
            furnace.setInventorySlotContents(1, new ItemStack(Item.coal, 2 + random.nextInt(4)));
        }

        return true;
    }

    private boolean generateSmallFarm(World world, Random random, int x, int y, int z) {
        // Find solid ground
        while(y > 5 && world.isAirBlock(x, y - 1, z)) {
            y--;
        }

        if(y <= 5) return false;

        // 3x3 dirt farm with water in center
        for(int dx = -1; dx <= 1; dx++) {
            for(int dz = -1; dz <= 1; dz++) {
                if(dx == 0 && dz == 0) {
                    // Water in center
                    world.setBlock(x + dx, y - 1, z + dz, Block.waterStill.blockID);
                } else {
                    // Farmland around
                    world.setBlock(x + dx, y - 1, z + dz, Block.tilledField.blockID);

                    // Some wheat growing
                    if(random.nextInt(3) == 0) {
                        world.setBlock(x + dx, y, z + dz, Block.crops.blockID);
                        world.setBlockMetadata(x + dx, y, z + dz, random.nextInt(4)); // Random growth stage
                    }
                }
            }
        }

        return true;
    }
}
