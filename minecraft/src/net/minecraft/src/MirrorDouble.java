package net.minecraft.src;

/**
 * Mirror double entity - a copy of the player that reproduces movements
 * with a 2-3 second delay, positioned on the opposite side of dark corridors.
 */
public class MirrorDouble extends EntityLiving {
    private static World activeWorld;
    private static EntityPlayer activePlayer;
    private EntityPlayer targetPlayer;
    private long spawnTime;
    private float[] movementHistory = new float[200]; // Store positions for ~3 seconds at 20 ticks/sec
    private int historyIndex = 0;
    private boolean isRecording = false;

    // Delay before mirror starts mimicking (2-3 seconds = 40-60 ticks at 20 tps)
    private static final int MIN_DELAY_TICKS = 40;
    private static final int MAX_DELAY_TICKS = 60;
    private int delayTicks = 0;
    private int tickCounter = 0;

    private double mirrorX;
    private double mirrorY;
    private double mirrorZ;
    private boolean anchorSet;

    public MirrorDouble(World world, EntityPlayer player) {
        super(world);
        this.targetPlayer = player;
        this.spawnTime = System.currentTimeMillis();
        this.delayTicks = MIN_DELAY_TICKS + (int)(Math.random() * (MAX_DELAY_TICKS - MIN_DELAY_TICKS));
        this.mirrorX = 0;
        this.mirrorY = 0;
        this.mirrorZ = 0;
        this.setSize(0.6F, 1.8F); // Same size as player
        this.noClip = true; // Can pass through blocks
        this.isImmuneToFire = true;
    }

    @Override
    public int getMaxHealth() {
        return 1;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (targetPlayer == null || targetPlayer.isDead || !HorrorState.stalkerActive) {
            this.setEntityDead();
            return;
        }

        // Position mirror double on opposite side of corridor from player
        if (!anchorSet) positionMirror();

        // Record player movements for delayed playback
        recordMovement();

        // Play back recorded movement after delay
        tickCounter++;
        if (tickCounter > delayTicks) {
            playbackMovement();
        }

        // Remove after 10 minutes or if too far
        if (System.currentTimeMillis() - spawnTime > 600000 ||
            targetPlayer.getDistance(mirrorX, mirrorY, mirrorZ) > 100.0D) {
            this.setEntityDead();
        }
    }

    private void positionMirror() {
        if (targetPlayer == null) return;

        // Position mirror on the opposite side of the corridor
        // Calculate perpendicular direction to player's facing
        float yawRad = (float) Math.toRadians(targetPlayer.rotationYaw);
        double perpX = Math.sin(yawRad);
        double perpZ = Math.cos(yawRad);

        // Place mirror at a stable distance (15-25 blocks) on the perpendicular axis
        double distance = 15.0D + Math.random() * 10.0D;

        mirrorX = targetPlayer.posX + perpX * distance;
        mirrorY = targetPlayer.posY;
        mirrorZ = targetPlayer.posZ + perpZ * distance;

        // Snap to ground
        mirrorY = worldObj.getHeightValue((int)mirrorX, (int)mirrorZ) + 1;

        this.setPosition(mirrorX, mirrorY, mirrorZ);
        anchorSet = true;
    }

    private void recordMovement() {
        // Record current player position and rotation
        int posIndex = historyIndex * 3; // Store x, y, z for each tick
        movementHistory[posIndex] = (float) targetPlayer.posX;
        movementHistory[posIndex + 1] = (float) targetPlayer.posY;
        movementHistory[posIndex + 2] = (float) targetPlayer.posZ;

        historyIndex++;
        if (historyIndex >= movementHistory.length / 3) {
            historyIndex = 0; // Wrap around
        }
        isRecording = true;
    }

    private void playbackMovement() {
        if (!isRecording) return;

        // Calculate the index to play back (delayed by delayTicks)
        if (tickCounter <= delayTicks) return;
        int playbackIndex = historyIndex - delayTicks - 1;
        int historySize = movementHistory.length / 3;
        while (playbackIndex < 0) playbackIndex += historySize;
        while (playbackIndex >= historySize) playbackIndex -= historySize;
        if (playbackIndex < 0 || playbackIndex >= movementHistory.length / 3) return;

        float px = movementHistory[playbackIndex * 3];
        float py = movementHistory[playbackIndex * 3 + 1];
        float pz = movementHistory[playbackIndex * 3 + 2];

        // Only move mirror if player is far enough (not right next to it)
        double currentDistance = Math.sqrt(
            Math.pow(mirrorX - px, 2) +
            Math.pow(mirrorY - py, 2) +
            Math.pow(mirrorZ - pz, 2)
        );

        if (currentDistance > 2.0D) {
            // Apply the delayed movement as an offset from the fixed anchor.
            double dx = px - (float)targetPlayer.posX;
            double dy = py - (float)targetPlayer.posY;
            double dz = pz - (float)targetPlayer.posZ;
            mirrorX += dx * 0.1D;
            mirrorY += dy * 0.1D;
            mirrorZ += dz * 0.1D;

            this.setPosition(mirrorX, mirrorY, mirrorZ);
        }
    }

    /**
     * Get the mirror's X position for rendering.
     */
    public double getMirrorX() {
        return mirrorX;
    }

    /**
     * Get the mirror's Y position for rendering.
     */
    public double getMirrorY() {
        return mirrorY;
    }

    /**
     * Get the mirror's Z position for rendering.
     */
    public double getMirrorZ() {
        return mirrorZ;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected boolean canDespawn() {
        return false;
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
    public float getShadowSize() {
        return 0.0F; // No shadow
    }

    public static void setWorld(World w, EntityPlayer p) {
        if (activeWorld != w || activePlayer != p) {
            removeDouble();
        }
        activeWorld = w;
        activePlayer = p;
    }

    public static void spawnDouble(World w, EntityPlayer p) {
        // Spawn mirror double near player
        if (w != null && p != null) {
            try {
                setWorld(w, p);
                MirrorDouble mirror = new MirrorDouble(w, p);
                w.spawnEntityInWorld(mirror);
            } catch (Exception e) {}
        }
    }

    public static void update() {
        // Entities are updated by World.tick; no second update loop is needed.
    }

    public static void removeDouble() {
        if (activeWorld != null) {
            java.util.ArrayList entities = new java.util.ArrayList(activeWorld.loadedEntityList);
            for (int i = 0; i < entities.size(); i++) {
                Object entity = entities.get(i);
                if (entity instanceof MirrorDouble) {
                    ((Entity)entity).setEntityDead();
                }
            }
        }
        activeWorld = null;
        activePlayer = null;
    }
}
