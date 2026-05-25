package net.minecraft.src;

/**
 * Error404 entity for jump scare in house
 * Appears for 0.5 seconds then disappears
 */
public class EntityError404 extends EntityMob {
    private long spawnTime;
    private EntityPlayer targetPlayer;

    public EntityError404(World world) {
        super(world);
        this.texture = "/mob/error404.png";
        this.setSize(0.6F, 1.8F);
        this.health = 1;
        this.spawnTime = System.currentTimeMillis();
    }

    public EntityError404(World world, EntityPlayer player) {
        this(world);
        this.targetPlayer = player;
    }

    @Override
    public int getMaxHealth() {
        return 1;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Disappear after 0.5 seconds
        if(System.currentTimeMillis() - spawnTime > 500) {
            this.setEntityDead();
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
        return "damage.fallbig"; // Loud sound
    }

    @Override
    protected String getDeathSound() {
        return null;
    }

    @Override
    public boolean getCanSpawnHere() {
        return true;
    }
}
