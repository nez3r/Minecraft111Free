package net.minecraft.src;

/**
 * Phantom observer mob that spawns at the edge of render distance.
 * Always faces player, makes no sound, and instantly despawns when
 * approached closer than 12-15 blocks or when directly looked at.
 */
public class PhantomObserver extends EntityMob {
    private static World activeWorld;
    private static EntityPlayer activePlayer;
    private static final double SPAWN_DISTANCE_MIN = 64.0D; // Edge of render distance
    private static final double SPAWN_DISTANCE_MAX = 80.0D;
    private static final double DESPAWN_DISTANCE_CLOSE = 12.0D;
    private static final double DESPAWN_DISTANCE_LOOK = 15.0D;
    private static final long SPAWN_INTERVAL = 30000L; // 30 seconds between spawns

    private EntityPlayer targetPlayer;
    private long lastSpawnTime = 0;
    private boolean isFacingPlayer = true;

    public PhantomObserver(World world) {
        super(world);
        this.texture = "/mob/phantom.png"; // Use existing or create transparent texture
        this.setSize(0.9F, 1.8F);
        this.moveSpeed = 0.0F; // Doesn't move
        this.health = 1;
        this.isImmuneToFire = true;
        this.preventEntitySpawning = true; // Prevent normal AI
        this.noClip = true; // Can go through walls
    }

    @Override
    public int getMaxHealth() {
        return 1;
    }

    public PhantomObserver(World world, EntityPlayer player) {
        this(world);
        this.targetPlayer = player;
        this.lastSpawnTime = System.currentTimeMillis();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!HorrorState.stalkerActive) {
            this.setEntityDead();
            return;
        }

        if (targetPlayer == null || targetPlayer.isDead || targetPlayer.worldObj != worldObj) {
            this.setEntityDead();
            return;
        }

        // Update position to always face player
        updateFacingPlayer();

        // Check for despawning conditions
        double distanceToPlayer = getDistanceToEntity(targetPlayer);

        // Despawn if too close
        if (distanceToPlayer < DESPAWN_DISTANCE_CLOSE) {
            this.setEntityDead();
            return;
        }

        // Despawn if player is looking directly at it
        if (isPlayerLookingAtMe() && distanceToPlayer < DESPAWN_DISTANCE_LOOK) {
            this.setEntityDead();
            return;
        }

        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    private void updateFacingPlayer() {
        if (targetPlayer == null) return;

        double dx = targetPlayer.posX - this.posX;
        double dy = (targetPlayer.posY + targetPlayer.getEyeHeight()) - (this.posY + this.getEyeHeight());
        double dz = targetPlayer.posZ - this.posZ;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 0.1) return;

        dx /= distance;
        dy /= distance;
        dz /= distance;

        // Calculate yaw and pitch to face player
        double yaw = Math.atan2(dx, dz) * 180.0 / Math.PI;
        double pitch = -Math.asin(dy) * 180.0 / Math.PI;

        this.rotationYaw = (float) yaw;
        this.rotationPitch = (float) pitch;
    }

    private boolean isPlayerLookingAtMe() {
        if (targetPlayer == null) return false;

        double dx = this.posX - targetPlayer.posX;
        double dy = (this.posY + this.getEyeHeight()) - (targetPlayer.posY + targetPlayer.getEyeHeight());
        double dz = this.posZ - targetPlayer.posZ;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance == 0) return false;

        dx /= distance;
        dy /= distance;
        dz /= distance;

        // Get player look vector
        float yaw = targetPlayer.rotationYaw;
        float pitch = targetPlayer.rotationPitch;

        double lookX = -Math.sin(yaw * Math.PI / 180.0) * Math.cos(pitch * Math.PI / 180.0);
        double lookY = -Math.sin(pitch * Math.PI / 180.0);
        double lookZ = Math.cos(yaw * Math.PI / 180.0) * Math.cos(pitch * Math.PI / 180.0);

        double dot = lookX * dx + lookY * dy + lookZ * dz;
        return dot > 0.98; // Very narrow cone - direct look
    }

    private void spawnNewObserver() {
        if (targetPlayer == null || worldObj == null) return;

        // Spawn at edge of render distance in random direction
        double angle = Math.random() * Math.PI * 2;
        double distance = SPAWN_DISTANCE_MIN + Math.random() * (SPAWN_DISTANCE_MAX - SPAWN_DISTANCE_MIN);

        double offsetX = Math.cos(angle) * distance;
        double offsetZ = Math.sin(angle) * distance;

        double newX = targetPlayer.posX + offsetX;
        double newZ = targetPlayer.posZ + offsetZ;
        double newY = worldObj.getHeightValue((int)newX, (int)newZ) + 1;

        // Ensure it's not inside solid blocks
        if (worldObj.getBlockId((int)newX, (int)newY, (int)newZ) != 0) {
            newY = worldObj.getTopSolidOrLiquidBlock((int)newX, (int)newZ) + 1;
        }

        PhantomObserver observer = new PhantomObserver(worldObj, targetPlayer);
        observer.setPosition(newX, newY, newZ);
        worldObj.spawnEntityInWorld(observer);
    }

    @Override
    protected boolean canDespawn() {
        return false; // We handle despawning manually
    }

    @Override
    protected Entity findPlayerToAttack() {
        return null;
    }

    @Override
    protected void attackEntity(Entity entity, float distance) {
        // Never attack
    }

    @Override
    protected String getLivingSound() {
        return null; // No sound
    }

    @Override
    protected String getHurtSound() {
        return null;
    }

    @Override
    protected String getDeathSound() {
        return null;
    }

    @Override
    public boolean getCanSpawnHere() {
        return false; // We control spawning manually
    }

    @Override
    public float getShadowSize() {
        return 0.0F; // No shadow
    }

    public static void setWorld(World w, EntityPlayer p) {
        if (activeWorld != w || activePlayer != p) removeAll();
        activeWorld = w;
        activePlayer = p;
    }

    public static void spawnNearPlayer(World w, EntityPlayer p) {
        // Spawn phantom near player - simplified
        if (w != null && p != null) {
            try {
                PhantomObserver phantom = new PhantomObserver(w, p);
                setWorld(w, p);
                double angle = Math.random() * Math.PI * 2.0D;
                double distance = SPAWN_DISTANCE_MIN;
                double x = p.posX + Math.cos(angle) * distance;
                double z = p.posZ + Math.sin(angle) * distance;
                double y = w.getHeightValue((int)x, (int)z) + 1;
                phantom.setPosition(x, y, z);
                w.spawnEntityInWorld(phantom);
            } catch (Exception e) {}
        }
    }

    public static void updateAll(World w, EntityPlayer p) {
        if (w == null || p == null) {
            removeAll();
            return;
        }
        setWorld(w, p);
        java.util.ArrayList entities = new java.util.ArrayList(w.loadedEntityList);
        for (int i = 0; i < entities.size(); i++) {
            Object entity = entities.get(i);
            if (entity instanceof PhantomObserver) {
                ((PhantomObserver)entity).targetPlayer = p;
            }
        }
    }

    public static void removeAll() {
        if (activeWorld != null) {
            java.util.ArrayList entities = new java.util.ArrayList(activeWorld.loadedEntityList);
            for (int i = 0; i < entities.size(); i++) {
                Object entity = entities.get(i);
                if (entity instanceof PhantomObserver) {
                    ((Entity)entity).setEntityDead();
                }
            }
        }
        activeWorld = null;
        activePlayer = null;
    }
}
