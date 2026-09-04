package net.minecraft.src;

/**
 * Dynamic fog that compresses render distance when player descends below Y=16
 * or when hidden entities approach the player.
 */
public class DynamicFogHandler {
    private static float baseFogDensity = 0.08F; // Default fog density
    private static float compressedFogDensity = 0.5F; // Fog for close visibility (2-3 blocks)
    private static final int MIN_Y = 16;
    private static boolean fogCompressed = false;

    private static World world;
    private static EntityPlayer player;

    public static void init(World world, EntityPlayer player) {
        DynamicFogHandler.world = world;
        DynamicFogHandler.player = player;
    }

    /**
     * Updates fog density based on player position and nearby hidden entities.
     */
    public static void updateFog() {
        if (player == null || world == null) return;

        boolean shouldCompress = false;

        // Compress fog when descending below Y=16
        if (player.posY < MIN_Y) {
            shouldCompress = true;
        }

        // Also check for hidden entities nearby (stalker, phantom observer)
        // Check all entities in world
        for (int i = 0; i < world.loadedEntityList.size(); i++) {
            Object obj = world.loadedEntityList.get(i);
            if (obj instanceof Entity) {
                Entity entity = (Entity) obj;

                // Check phantom observer or stalker entities
                if (entity instanceof Entity404 || entity instanceof PhantomObserver) {
                    double distance = entity.getDistanceToEntity(player);
                    if (distance < 30.0D) { // Within 30 blocks
                        shouldCompress = true;
                    }
                }
            }
        }

        // Apply fog compression
        if (shouldCompress && !fogCompressed) {
            fogCompressed = true;
            // The fog is updated via render settings
            // We'll modify this via the render pipeline
        } else if (!shouldCompress && fogCompressed) {
            fogCompressed = false;
        }
    }

    public static float getFogDensity() {
        return fogCompressed ? compressedFogDensity : baseFogDensity;
    }

    public static boolean isFogCompressed() {
        return fogCompressed;
    }
}