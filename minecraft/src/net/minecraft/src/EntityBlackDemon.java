package net.minecraft.src;

/**
 * Huge black demon entity for attack sequence
 */
public class EntityBlackDemon extends EntityMob {
    private EntityPlayer targetPlayer;
    private long spawnTime;

    public EntityBlackDemon(World world) {
        super(world);
        this.texture = "/mob/blackdemon.png";
        this.setSize(1.8F, 5.4F); // 3x normal player size
        this.health = 1000;
        this.isImmuneToFire = true;
        this.spawnTime = System.currentTimeMillis();
    }

    public EntityBlackDemon(World world, EntityPlayer player) {
        this(world);
        this.targetPlayer = player;
    }

    @Override
    public int getMaxHealth() {
        return 1000;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Remove after 10 seconds
        if (System.currentTimeMillis() - spawnTime > 10000) {
            this.setEntityDead();
            HorrorState.attackSequenceActive = false;
            HorrorState.attackEntity = null;
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
        return null;
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

    public EntityPlayer getTargetPlayer() {
        return targetPlayer;
    }
}
