package net.minecraft.src;

import java.util.List;

public class EntitySnowman extends EntitySnowmanBase {
	public EntitySnowman(World var1) {
		super(var1);
		this.texture = "/mob/snowman.png";
		this.setSize(0.4F, 1.8F);
	}

	public int getMaxHealth() {
		return 4;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(this.entityToAttack == null && !this.hasPath() && this.worldObj.rand.nextInt(100) == 0) {
			List var1 = this.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBoxFromPool(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
			if(!var1.isEmpty()) {
				this.setEntityToAttack((Entity)var1.get(this.worldObj.rand.nextInt(var1.size())));
			}
		}

		int var5 = MathHelper.floor_double(this.posX);
		int var2 = MathHelper.floor_double(this.posY);
		int var3 = MathHelper.floor_double(this.posZ);
		if(this.worldObj.getWorldChunkManager().getTemperature(var5, var2, var3) > 1.0F) {
			this.attackEntityFrom(DamageSource.onFire, 1);
		}

		for(var5 = 0; var5 < 4; ++var5) {
			var2 = MathHelper.floor_double(this.posX + (double)((float)(var5 % 2 * 2 - 1) * 0.25F));
			var3 = MathHelper.floor_double(this.posY);
			int var4 = MathHelper.floor_double(this.posZ + (double)((float)(var5 / 2 % 2 * 2 - 1) * 0.25F));
			if(this.worldObj.getBlockId(var2, var3, var4) == 0 && this.worldObj.getWorldChunkManager().getTemperature(var2, var3, var4) < 0.8F && Block.snow.canPlaceBlockAt(this.worldObj, var2, var3, var4)) {
				this.worldObj.setBlockWithNotify(var2, var3, var4, Block.snow.blockID);
			}
		}

	}

	protected void attackEntity(Entity var1, float var2) {
		if(var2 < 10.0F) {
			double var3 = var1.posX - this.posX;
			double var5 = var1.posZ - this.posZ;
			if(this.attackTime == 0) {
				EntitySnowball var7 = new EntitySnowball(this.worldObj, this);
				double var8 = var1.posY + (double)var1.getEyeHeight() - (double)1.1F - var7.posY;
				float var10 = MathHelper.sqrt_double(var3 * var3 + var5 * var5) * 0.2F;
				this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
				this.worldObj.spawnEntityInWorld(var7);
				var7.setThrowableHeading(var3, var8 + (double)var10, var5, 1.6F, 12.0F);
				this.attackTime = 10;
			}

			this.rotationYaw = (float)(Math.atan2(var5, var3) * 180.0D / (double)((float)Math.PI)) - 90.0F;
			this.hasAttacked = true;
		}

	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
	}

	protected int getDropItemId() {
		return Item.snowball.shiftedIndex;
	}

	protected void dropFewItems(boolean var1, int var2) {
		int var3 = this.rand.nextInt(16);

		for(int var4 = 0; var4 < var3; ++var4) {
			this.dropItem(Item.snowball.shiftedIndex, 1);
		}

	}
}
