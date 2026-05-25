package net.minecraft.src;

import java.util.Random;

/**
 * Teleporter for tunnel dimension
 * Handles portal creation and player positioning
 */
public class Teleporter404 extends Teleporter {
    private Random random;

    public Teleporter404() {
        this.random = new Random();
    }

    public void placeInPortal(Entity entity, double x, double y, double z, float yaw) {
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if(player.dimension == 66) {
                // Entering tunnel dimension - place at start of tunnel
                entity.setLocationAndAngles(0.5D, 65.0D, 0.5D, 90.0F, 0.0F);
                entity.motionX = entity.motionY = entity.motionZ = 0.0D;
            } else {
                // Returning to overworld - place at portal location
                entity.setLocationAndAngles(x, y, z, yaw, 0.0F);
                entity.motionX = entity.motionY = entity.motionZ = 0.0D;
            }
        }
    }

    public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float yaw) {
        // Always create new portal position
        this.placeInPortal(entity, x, y, z, yaw);
        return true;
    }

    public boolean makePortal(Entity entity) {
        // Portal is created by world generation, not by teleporter
        return true;
    }

    public void removeStalePortalLocations(long worldTime) {
        // No portal caching needed
    }
}
