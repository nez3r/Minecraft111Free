package net.minecraft.src;

import java.util.Random;

/**
 * Red torch block variant for horror mod
 */
public class BlockRedTorch extends BlockTorch {

    public BlockRedTorch(int id, int textureIndex) {
        super(id, textureIndex);
        this.setLightValue(0.75F); // Slightly dimmer than normal torch (12 instead of 14)
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        int metadata = world.getBlockMetadata(x, y, z);
        double xPos = (double)x + 0.5D;
        double yPos = (double)y + 0.7D;
        double zPos = (double)z + 0.5D;
        double offset = 0.2199999988079071D;
        double horizontalOffset = 0.27000000476837158D;

        if (metadata == 1) {
            world.spawnParticle("reddust", xPos - horizontalOffset, yPos + offset, zPos, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("smoke", xPos - horizontalOffset, yPos + offset, zPos, 0.0D, 0.0D, 0.0D);
        } else if (metadata == 2) {
            world.spawnParticle("reddust", xPos + horizontalOffset, yPos + offset, zPos, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("smoke", xPos + horizontalOffset, yPos + offset, zPos, 0.0D, 0.0D, 0.0D);
        } else if (metadata == 3) {
            world.spawnParticle("reddust", xPos, yPos + offset, zPos - horizontalOffset, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("smoke", xPos, yPos + offset, zPos - horizontalOffset, 0.0D, 0.0D, 0.0D);
        } else if (metadata == 4) {
            world.spawnParticle("reddust", xPos, yPos + offset, zPos + horizontalOffset, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("smoke", xPos, yPos + offset, zPos + horizontalOffset, 0.0D, 0.0D, 0.0D);
        } else {
            world.spawnParticle("reddust", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("smoke", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
        }
    }
}
