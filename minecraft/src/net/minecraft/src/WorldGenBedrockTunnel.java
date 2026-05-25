package net.minecraft.src;

import java.util.Random;

/**
 * Generates a bedrock tunnel structure near the player
 * Appears after certain horror events
 */
public class WorldGenBedrockTunnel extends WorldGenerator {

    public boolean generate(World world, Random random, int x, int y, int z) {
        // Find suitable ground level
        while(y > 5 && world.isAirBlock(x, y, z)) {
            y--;
        }

        if(y <= 5) {
            return false;
        }

        y++; // Place on top of ground

        // Generate small initial tunnel entrance (only 5 blocks long initially)
        int centerY = y + 2;
        int initialLength = 5; // Small initial tunnel

        // Build small tunnel entrance along X axis
        for(int dx = 0; dx < initialLength; dx++) {
            for(int dy = -2; dy <= 2; dy++) {
                for(int dz = -2; dz <= 2; dz++) {
                    int blockX = x + dx;
                    int blockY = centerY + dy;
                    int blockZ = z + dz;

                    // Bedrock walls, floor, ceiling - FORCE replace all blocks
                    if(dy == -2 || dy == 2 || dz == -2 || dz == 2) {
                        world.setBlock(blockX, blockY, blockZ, Block.bedrock.blockID);
                    } else {
                        // Air inside tunnel - destroy everything
                        world.setBlock(blockX, blockY, blockZ, 0);
                    }
                }
            }

            // Small delay to prevent freezing
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Place one torch at entrance
        world.setBlockWithNotify(x + 2, centerY, z - 1, Block.torchWood.blockID);
        world.setBlockMetadataWithNotify(x + 2, centerY, z - 1, 4); // South facing

        // Place chest with diamonds at the end (5 blocks away initially)
        int chestX = x + 4;
        int chestY = centerY - 1;
        int chestZ = z;

        world.setBlockWithNotify(chestX, chestY, chestZ, Block.chest.blockID);

        // Fill chest with diamonds and mark as tunnel chest
        TileEntityChest chest = (TileEntityChest)world.getBlockTileEntity(chestX, chestY, chestZ);
        if(chest != null) {
            chest.isTunnelChest = true;
            chest.setInventorySlotContents(13, new ItemStack(Item.diamond, 64));
        }

        // Place warning sign at entrance
        world.setBlockWithNotify(x - 1, y, z, Block.signPost.blockID);
        TileEntitySign sign = (TileEntitySign)world.getBlockTileEntity(x - 1, y, z);
        if(sign != null) {
            sign.signText[0] = "";
            sign.signText[1] = "\u00a7cDO NOT";
            sign.signText[2] = "\u00a7cENTER";
            sign.signText[3] = "";
        }

        HorrorState.tunnelGenerated = true;
        HorrorState.tunnelX = x;
        HorrorState.tunnelY = y;
        HorrorState.tunnelZ = z;

        return true;
    }

    /**
     * Generate tunnel near player position
     */
    public static void generateNearPlayer(final EntityPlayer player) {
        if(player == null || player.worldObj == null) {
            return;
        }

        if(HorrorState.tunnelGenerated) {
            return;
        }

        final Random random = new Random();

        // Generate 20-50 blocks away from player
        double angle = random.nextDouble() * Math.PI * 2;
        int distance = 20 + random.nextInt(31); // 20-50 blocks

        final int tunnelX = (int)(player.posX + Math.cos(angle) * distance);
        final int tunnelZ = (int)(player.posZ + Math.sin(angle) * distance);
        int tunnelY = player.worldObj.getHeightValue(tunnelX, tunnelZ);

        // Find ground level
        while(tunnelY > 5 && player.worldObj.isAirBlock(tunnelX, tunnelY, tunnelZ)) {
            tunnelY--;
        }
        tunnelY++; // Place on top of ground

        // Mark as generated immediately
        HorrorState.tunnelGenerated = true;
        HorrorState.tunnelX = tunnelX;
        HorrorState.tunnelY = tunnelY + 2; // Center Y
        HorrorState.tunnelZ = tunnelZ;

        // Generate initial entrance (5 blocks) immediately
        int centerY = tunnelY + 2;
        for(int dx = 0; dx < 5; dx++) {
            for(int dy = -2; dy <= 2; dy++) {
                for(int dz = -2; dz <= 2; dz++) {
                    int blockX = tunnelX + dx;
                    int blockY = centerY + dy;
                    int blockZ = tunnelZ + dz;

                    // Bedrock walls, floor, ceiling - FORCE replace all blocks
                    if(dy == -2 || dy == 2 || dz == -2 || dz == 2) {
                        player.worldObj.setBlock(blockX, blockY, blockZ, Block.bedrock.blockID);
                    } else {
                        // Air inside tunnel - destroy everything
                        player.worldObj.setBlock(blockX, blockY, blockZ, 0);
                    }
                }
            }
        }

        // Place warning sign at entrance
        player.worldObj.setBlock(tunnelX - 1, tunnelY, tunnelZ, Block.signPost.blockID);
        TileEntitySign sign = (TileEntitySign)player.worldObj.getBlockTileEntity(tunnelX - 1, tunnelY, tunnelZ);
        if(sign != null) {
            sign.signText[0] = "";
            sign.signText[1] = "\u00a7cDO NOT";
            sign.signText[2] = "\u00a7cENTER";
            sign.signText[3] = "";
        }

        // Place one torch at entrance on wall (not floating)
        player.worldObj.setBlock(tunnelX + 2, centerY, tunnelZ + 1, Block.torchWood.blockID);
        player.worldObj.setBlockMetadataWithNotify(tunnelX + 2, centerY, tunnelZ + 1, 3); // North facing (attached to south wall)

        // Reset segment counter
        lastGeneratedSegment = 4; // Already generated 0-4

        player.addChatMessage("\u00a78Something appeared nearby...");
    }

    /**
     * Check if player is inside tunnel and close entrance if they go too far
     */
    public static void checkTunnelProgress(EntityPlayer player) {
        if(!HorrorState.tunnelGenerated) {
            return;
        }

        if(player == null || player.worldObj == null) {
            return;
        }

        // Get tunnel coordinates
        int tunnelX = HorrorState.tunnelX;
        int centerY = HorrorState.tunnelY;
        int tunnelZ = HorrorState.tunnelZ;

        // Check if player is near tunnel area
        double distX = player.posX - tunnelX;
        double distZ = player.posZ - tunnelZ;
        double distY = player.posY - centerY;
        double distance = Math.sqrt(distX * distX + distZ * distZ);

        // If player is within 100 blocks, generate tunnel progressively
        if(distance < 100 && Math.abs(distY) <= 10) {
            // Calculate how far into tunnel player is (along X axis)
            double tunnelProgress = distX;

            // Check if player REALLY entered tunnel:
            // - Must be at least 5 blocks into tunnel (past entrance)
            // - Must be within tunnel bounds (Y and Z)
            // - Must be inside the tunnel corridor
            boolean insideTunnel = tunnelProgress >= 5.0 &&
                                   Math.abs(distY) <= 2 &&
                                   Math.abs(distZ) <= 2;

            if(insideTunnel && !HorrorState.tunnelEntranceClosed) {
                // Play tunnel entrance sound
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.theMinecraft;
                if(mc != null && mc.sndManager != null) {
                    mc.sndManager.playSoundFX("error.tunnel", 1.0F, 1.0F);
                }

                // Close entrance behind player with bedrock wall
                closeEntranceWithBedrock(player.worldObj, tunnelX, centerY, tunnelZ);
                HorrorState.tunnelEntranceClosed = true;
                player.addChatMessage("\u00a74You hear a loud rumbling behind you...");

                // Force render distance to Tiny and save settings
                if(mc != null && mc.gameSettings != null) {
                    mc.gameSettings.renderDistance = 0; // 0 = Tiny
                    mc.gameSettings.saveOptions(); // Save to persist the change
                }
            }

            // Keep render distance locked to Tiny while in tunnel
            if(insideTunnel && HorrorState.tunnelEntranceClosed) {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.theMinecraft;
                if(mc != null && mc.gameSettings != null && mc.gameSettings.renderDistance != 0) {
                    mc.gameSettings.renderDistance = 0; // Force back to Tiny
                }
            }

            // Generate tunnel far ahead (80 blocks ahead of player)
            if(tunnelProgress >= -5 && tunnelProgress < 200) {
                int targetLength = (int)Math.max(5, tunnelProgress + 80);
                extendTunnelSafe(player.worldObj, tunnelX, centerY, tunnelZ, targetLength);
            }
        }
    }

    /**
     * Extend tunnel safely - only 1 block per tick to avoid lag
     */
    private static int lastGeneratedSegment = -1;

    private static void extendTunnelSafe(World world, int startX, int centerY, int centerZ, int targetLength) {
        // Limit to 200 blocks max (for infinite feeling)
        targetLength = Math.min(targetLength, 200);

        // Only generate 1 segment per call to avoid lag
        for(int dx = Math.max(0, lastGeneratedSegment + 1); dx < targetLength; dx++) {
            // Check if this segment already exists
            if(world.getBlockId(startX + dx, centerY, centerZ) != 0) {
                lastGeneratedSegment = dx;
                continue;
            }

            // Generate only this one segment
            for(int dy = -2; dy <= 2; dy++) {
                for(int dz = -2; dz <= 2; dz++) {
                    int blockX = startX + dx;
                    int blockY = centerY + dy;
                    int blockZ = centerZ + dz;

                    // Bedrock walls, floor, ceiling
                    if(dy == -2 || dy == 2 || dz == -2 || dz == 2) {
                        world.setBlockWithNotify(blockX, blockY, blockZ, Block.bedrock.blockID);
                    } else {
                        // Air inside tunnel
                        world.setBlockWithNotify(blockX, blockY, blockZ, 0);
                    }
                }
            }

            // Place torch on wall every 5 blocks (on south wall, not floating)
            if(dx % 5 == 0 && dx > 0) {
                world.setBlock(startX + dx, centerY, centerZ + 1, Block.torchWood.blockID);
                world.setBlockMetadataWithNotify(startX + dx, centerY, centerZ + 1, 3); // North facing (attached to south wall)
            }

            // Place chest at position 80 with back wall
            if(dx == 80) {
                world.setBlock(startX + dx, centerY - 1, centerZ, Block.chest.blockID);

                // Wait for tile entity to be created
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Mark chest as tunnel chest and fill with diamonds
                TileEntityChest chest = (TileEntityChest)world.getBlockTileEntity(startX + dx, centerY - 1, centerZ);
                if(chest != null) {
                    chest.isTunnelChest = true;
                    chest.setInventorySlotContents(13, new ItemStack(Item.diamond, 64));
                }

                // Place back wall (bedrock wall behind chest at dx+1)
                for(int dy = -2; dy <= 2; dy++) {
                    for(int dz = -2; dz <= 2; dz++) {
                        world.setBlock(startX + dx + 1, centerY + dy, centerZ + dz, Block.bedrock.blockID);
                    }
                }
            }

            lastGeneratedSegment = dx;
            return; // Only generate 1 segment per tick
        }
    }

    /**
     * Close tunnel entrance with bedrock wall
     */
    private static void closeEntranceWithBedrock(World world, int x, int y, int z) {
        // Fill entrance (first 2 blocks) with bedrock wall
        for(int dx = 0; dx < 2; dx++) {
            for(int dy = -2; dy <= 2; dy++) {
                for(int dz = -2; dz <= 2; dz++) {
                    int blockX = x + dx;
                    int blockY = y + dy;
                    int blockZ = z + dz;

                    // Fill everything with bedrock
                    world.setBlock(blockX, blockY, blockZ, Block.bedrock.blockID);
                }
            }
        }
    }
}
