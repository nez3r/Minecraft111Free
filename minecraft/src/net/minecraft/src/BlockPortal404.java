package net.minecraft.src;

import java.util.Random;

/**
 * Custom portal block for tunnel dimension
 * Uses obsidian frame like nether portal
 */
public class BlockPortal404 extends BlockBreakable {
    public BlockPortal404(int id, int textureIndex) {
        super(id, textureIndex, Material.portal, false);
        this.setTickOnLoad(true);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        float var5;
        float var6;
        if(blockAccess.getBlockId(x - 1, y, z) != this.blockID && blockAccess.getBlockId(x + 1, y, z) != this.blockID) {
            var5 = 2.0F / 16.0F;
            var6 = 0.5F;
            this.setBlockBounds(0.5F - var5, 0.0F, 0.5F - var6, 0.5F + var5, 1.0F, 0.5F + var6);
        } else {
            var5 = 0.5F;
            var6 = 2.0F / 16.0F;
            this.setBlockBounds(0.5F - var5, 0.0F, 0.5F - var6, 0.5F + var5, 1.0F, 0.5F + var6);
        }
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if(entity.ridingEntity == null && entity.riddenByEntity == null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;

            if(!world.multiplayerWorld) {
                // Set portal cooldown
                player.timeUntilPortal = 10;

                // Simple teleport for single player
                if(player.dimension == 0) {
                    // Teleport to tunnel dimension entrance
                    player.setPosition(0.5D, 65.0D, 0.5D);
                    player.motionX = player.motionY = player.motionZ = 0.0D;
                } else if(player.dimension == 66) {
                    // Return to overworld at portal location
                    // Portal coordinates should be stored in HorrorState
                    if(HorrorState.portalX != 0 || HorrorState.portalZ != 0) {
                        player.setPosition(HorrorState.portalX + 0.5D, HorrorState.portalY + 1.0D, HorrorState.portalZ + 0.5D);
                        player.motionX = player.motionY = player.motionZ = 0.0D;
                    }
                }
            }
        }
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);

        // Check if portal frame is still valid
        if(!this.isPortalValid(world, x, y, z)) {
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    private boolean isPortalValid(World world, int x, int y, int z) {
        byte orientation = 0;

        if(world.getBlockId(x - 1, y, z) == this.blockID || world.getBlockId(x + 1, y, z) == this.blockID) {
            orientation = 1;
        }

        if(world.getBlockId(x, y, z - 1) == this.blockID || world.getBlockId(x, y, z + 1) == this.blockID) {
            orientation = 2;
        }

        // Find bottom of portal
        int bottomY = y;
        while(world.getBlockId(x, bottomY - 1, z) == this.blockID) {
            bottomY--;
        }

        // Check if obsidian frame exists
        if(orientation == 1) {
            return world.getBlockId(x, bottomY - 1, z) == Block.obsidian.blockID;
        } else if(orientation == 2) {
            return world.getBlockId(x, bottomY - 1, z) == Block.obsidian.blockID;
        }

        return false;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        if(!this.isPortalValid(world, x, y, z)) {
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public int getRenderBlockPass() {
        return 1;
    }

    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        // Dark purple particles
        if(random.nextInt(100) == 0) {
            world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "portal.portal", 0.5F, random.nextFloat() * 0.4F + 0.8F);
        }

        for(int i = 0; i < 4; ++i) {
            double xPos = (double)x + random.nextDouble();
            double yPos = (double)y + random.nextDouble();
            double zPos = (double)z + random.nextDouble();
            double xVel = (random.nextDouble() - 0.5D) * 0.5D;
            double yVel = (random.nextDouble() - 0.5D) * 0.5D;
            double zVel = (random.nextDouble() - 0.5D) * 0.5D;

            world.spawnParticle("portal", xPos, yPos, zPos, xVel, yVel, zVel);
        }
    }
}
