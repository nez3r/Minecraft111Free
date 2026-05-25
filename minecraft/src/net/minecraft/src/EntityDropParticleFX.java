package net.minecraft.src;

public class EntityDropParticleFX extends EntityFX {
	private Material field_40103_a;
	private int field_40104_aw;

	public EntityDropParticleFX(World var1, double var2, double var4, double var6, Material var8) {
		super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
		this.motionX = this.motionY = this.motionZ = 0.0D;
		if(var8 == Material.water) {
			this.particleRed = 0.0F;
			this.particleGreen = 0.0F;
			this.particleBlue = 1.0F;
		} else {
			this.particleRed = 1.0F;
			this.particleGreen = 0.0F;
			this.particleBlue = 0.0F;
		}

		this.setParticleTextureIndex(113);
		this.setSize(0.01F, 0.01F);
		this.particleGravity = 0.06F;
		this.field_40103_a = var8;
		this.field_40104_aw = 40;
		this.particleMaxAge = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
		this.motionX = this.motionY = this.motionZ = 0.0D;
	}

	public void renderParticle(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		super.renderParticle(var1, var2, var3, var4, var5, var6, var7);
	}

	public int getEntityBrightnessForRender(float var1) {
		return this.field_40103_a == Material.water ? super.getEntityBrightnessForRender(var1) : 257;
	}

	public float getEntityBrightness(float var1) {
		return this.field_40103_a == Material.water ? super.getEntityBrightness(var1) : 1.0F;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.field_40103_a == Material.water) {
			this.particleRed = 0.2F;
			this.particleGreen = 0.3F;
			this.particleBlue = 1.0F;
		} else {
			this.particleRed = 1.0F;
			this.particleGreen = 16.0F / (float)(40 - this.field_40104_aw + 16);
			this.particleBlue = 4.0F / (float)(40 - this.field_40104_aw + 8);
		}

		this.motionY -= (double)this.particleGravity;
		if(this.field_40104_aw-- > 0) {
			this.motionX *= 0.02D;
			this.motionY *= 0.02D;
			this.motionZ *= 0.02D;
			this.setParticleTextureIndex(113);
		} else {
			this.setParticleTextureIndex(112);
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.98F;
		this.motionY *= (double)0.98F;
		this.motionZ *= (double)0.98F;
		if(this.particleMaxAge-- <= 0) {
			this.setEntityDead();
		}

		if(this.onGround) {
			if(this.field_40103_a == Material.water) {
				this.setEntityDead();
				this.worldObj.spawnParticle("splash", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
			} else {
				this.setParticleTextureIndex(114);
			}

			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

		Material var1 = this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
		if(var1.getIsLiquid() || var1.isSolid()) {
			double var2 = (double)((float)(MathHelper.floor_double(this.posY) + 1) - BlockFluid.getFluidHeightPercent(this.worldObj.getBlockMetadata(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))));
			if(this.posY < var2) {
				this.setEntityDead();
			}
		}

	}
}
