package net.minecraft.src;

public class EntityEnderPearl extends EntityThrowable {
	public EntityEnderPearl(World var1) {
		super(var1);
	}

	public EntityEnderPearl(World var1, EntityLiving var2) {
		super(var1, var2);
	}

	public EntityEnderPearl(World var1, double var2, double var4, double var6) {
		super(var1, var2, var4, var6);
	}

	protected void onThrowableCollision(MovingObjectPosition var1) {
		if(var1.entityHit != null && var1.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.throwingEntity), 0)) {
		}

		for(int var2 = 0; var2 < 32; ++var2) {
			this.worldObj.spawnParticle("portal", this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
		}

		if(!this.worldObj.multiplayerWorld) {
			if(this.throwingEntity != null) {
				this.throwingEntity.teleportToLocation(this.posX, this.posY, this.posZ);
				this.throwingEntity.fallDistance = 0.0F;
				this.throwingEntity.attackEntityFrom(DamageSource.fall, 5);
			}

			this.setEntityDead();
		}

	}
}
