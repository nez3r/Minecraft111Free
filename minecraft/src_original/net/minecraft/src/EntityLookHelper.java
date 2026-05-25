package net.minecraft.src;

public class EntityLookHelper {
	private EntityLiving field_46151_a;
	private float field_46149_b;
	private float field_46150_c;
	private boolean field_46147_d = false;
	private double field_46148_e;
	private double field_46145_f;
	private double field_46146_g;

	public EntityLookHelper(EntityLiving var1) {
		this.field_46151_a = var1;
	}

	public void func_46141_a(Entity var1, float var2, float var3) {
		this.field_46148_e = var1.posX;
		if(var1 instanceof EntityLiving) {
			this.field_46145_f = var1.posY + (double)((EntityLiving)var1).getEyeHeight();
		} else {
			this.field_46145_f = (var1.boundingBox.minY + var1.boundingBox.maxY) / 2.0D;
		}

		this.field_46146_g = var1.posZ;
		this.field_46149_b = var2;
		this.field_46150_c = var3;
		this.field_46147_d = true;
	}

	public void func_46143_a(double var1, double var3, double var5, float var7, float var8) {
		this.field_46148_e = var1;
		this.field_46145_f = var3;
		this.field_46146_g = var5;
		this.field_46149_b = var7;
		this.field_46150_c = var8;
		this.field_46147_d = true;
	}

	public void func_46142_a() {
		this.field_46151_a.rotationPitch = 0.0F;
		if(this.field_46147_d) {
			this.field_46147_d = false;
			double var1 = this.field_46148_e - this.field_46151_a.posX;
			double var3 = this.field_46145_f - (this.field_46151_a.posY + (double)this.field_46151_a.getEyeHeight());
			double var5 = this.field_46146_g - this.field_46151_a.posZ;
			double var7 = (double)MathHelper.sqrt_double(var1 * var1 + var5 * var5);
			float var9 = (float)(Math.atan2(var5, var1) * 180.0D / (double)((float)Math.PI)) - 90.0F;
			float var10 = (float)(-(Math.atan2(var3, var7) * 180.0D / (double)((float)Math.PI)));
			this.field_46151_a.rotationPitch = this.func_46144_a(this.field_46151_a.rotationPitch, var10, this.field_46150_c);
			this.field_46151_a.rotationYaw = this.func_46144_a(this.field_46151_a.rotationYaw, var9, this.field_46149_b);
		}
	}

	private float func_46144_a(float var1, float var2, float var3) {
		float var4;
		for(var4 = var2 - var1; var4 < -180.0F; var4 += 360.0F) {
		}

		while(var4 >= 180.0F) {
			var4 -= 360.0F;
		}

		if(var4 > var3) {
			var4 = var3;
		}

		if(var4 < -var3) {
			var4 = -var3;
		}

		return var1 + var4;
	}
}
