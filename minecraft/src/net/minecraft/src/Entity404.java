package net.minecraft.src;

import java.util.Random;

/**
 * Black stalker entity that follows player from distance
 */
public class Entity404 extends EntityMob {
    private static final double MIN_DISTANCE = 40.0D;
    private static final double MAX_DISTANCE = 60.0D;
    private static final double TELEPORT_DISTANCE = 100.0D;

    private EntityPlayer targetPlayer;
    private int disappearTimer = 0;
    private boolean isVisible = true;
    private Random rand = new Random();

    public Entity404(World world) {
        super(world);
        this.texture = "/mob/entity404.png";
        this.moveSpeed = 0.3F;
        this.health = 20;
        this.setSize(0.6F, 1.8F);
        this.isImmuneToFire = true;
    }

    public Entity404(World world, EntityPlayer player) {
        this(world);
        this.targetPlayer = player;
    }

    @Override
    public int getMaxHealth() {
        return 20;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!HorrorState.stalkerActive) {
            this.setEntityDead();
            return;
        }

        if (this.targetPlayer == null || this.targetPlayer.isDead) {
            this.setEntityDead();
            return;
        }

        // Check if player is looking at entity
        if (isPlayerLookingAtMe()) {
            if (isVisible) {
                disappearTimer++;
                if (disappearTimer > 5) { // Disappear after 5 ticks
                    isVisible = false;
                    disappearTimer = 0;
                }
            }
        } else {
            if (!isVisible) {
                disappearTimer++;
                if (disappearTimer > 40) { // Reappear after 40 ticks (2 seconds)
                    isVisible = true;
                    disappearTimer = 0;
                }
            }
        }

        // Follow player at distance
        double distanceToPlayer = this.getDistanceToEntity(targetPlayer);

        if (distanceToPlayer > TELEPORT_DISTANCE) {
            // Teleport behind player if too far
            teleportBehindPlayer();
        } else if (distanceToPlayer < MIN_DISTANCE) {
            // Move away if too close
            moveAwayFromPlayer();
        } else if (distanceToPlayer > MAX_DISTANCE) {
            // Move closer if too far
            moveTowardsPlayer();
        }
    }

    private boolean isPlayerLookingAtMe() {
        if (targetPlayer == null) return false;

        // Calculate vector from player to entity
        double dx = this.posX - targetPlayer.posX;
        double dy = (this.posY + this.getEyeHeight()) - (targetPlayer.posY + targetPlayer.getEyeHeight());
        double dz = this.posZ - targetPlayer.posZ;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance == 0) return false;

        // Normalize
        dx /= distance;
        dy /= distance;
        dz /= distance;

        // Get player look vector
        float yaw = targetPlayer.rotationYaw;
        float pitch = targetPlayer.rotationPitch;

        double lookX = -Math.sin(yaw * Math.PI / 180.0) * Math.cos(pitch * Math.PI / 180.0);
        double lookY = -Math.sin(pitch * Math.PI / 180.0);
        double lookZ = Math.cos(yaw * Math.PI / 180.0) * Math.cos(pitch * Math.PI / 180.0);

        // Dot product to check if looking at entity
        double dot = lookX * dx + lookY * dy + lookZ * dz;

        return dot > 0.95; // Within ~18 degree cone
    }

    private void teleportBehindPlayer() {
        if (targetPlayer == null) return;

        double angle = rand.nextDouble() * Math.PI * 2;
        double distance = MIN_DISTANCE + rand.nextDouble() * (MAX_DISTANCE - MIN_DISTANCE);

        double offsetX = Math.cos(angle) * distance;
        double offsetZ = Math.sin(angle) * distance;

        double newX = targetPlayer.posX + offsetX;
        double newZ = targetPlayer.posZ + offsetZ;
        double newY = worldObj.getHeightValue((int)newX, (int)newZ) + 1;

        this.setPosition(newX, newY, newZ);
    }

    private void moveAwayFromPlayer() {
        if (targetPlayer == null) return;

        double dx = this.posX - targetPlayer.posX;
        double dz = this.posZ - targetPlayer.posZ;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0) {
            this.motionX = (dx / distance) * 0.1;
            this.motionZ = (dz / distance) * 0.1;
        }
    }

    private void moveTowardsPlayer() {
        if (targetPlayer == null) return;

        double dx = targetPlayer.posX - this.posX;
        double dz = targetPlayer.posZ - this.posZ;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0) {
            this.motionX = (dx / distance) * 0.08;
            this.motionZ = (dz / distance) * 0.08;
        }
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
    protected Entity findPlayerToAttack() {
        return null; // Never attack
    }

    @Override
    protected void attackEntity(Entity entity, float distance) {
        // Never attack
    }

    @Override
    protected String getLivingSound() {
        return null;
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
        return true;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
