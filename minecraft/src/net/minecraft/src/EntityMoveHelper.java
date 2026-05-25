package net.minecraft.src;

public class EntityMoveHelper {
	private EntityLiving field_46041_a;
	private double field_46039_b;
	private double field_46040_c;
	private double field_46037_d;
	private float field_46038_e;
	private boolean field_46036_f = false;

	public EntityMoveHelper(EntityLiving var1, float var2) {
		this.field_46041_a = var1;
		this.field_46039_b = var1.posX;
		this.field_46040_c = var1.posY;
		this.field_46037_d = var1.posZ;
		this.field_46038_e = var2;
	}

	public void func_46035_a(double var1, double var3, double var5) {
		this.field_46039_b = var1;
		this.field_46040_c = var3;
		this.field_46037_d = var5;
		this.field_46036_f = true;
	}

	public void func_46033_a(float var1) {
		this.field_46038_e = var1;
	}

	public void func_46034_a() {
		this.field_46041_a.func_46010_f(0.0F);
		if(this.field_46036_f) {
			this.field_46036_f = false;
			int var1 = MathHelper.floor_double(this.field_46041_a.boundingBox.minY + 0.5D);
			double var2 = this.field_46039_b - this.field_46041_a.posX;
			double var4 = this.field_46037_d - this.field_46041_a.posZ;
			double var6 = this.field_46040_c - (double)var1;
			float var8 = (float)(Math.atan2(var4, var2) * 180.0D / (double)((float)Math.PI)) - 90.0F;

			float var9;
			for(var9 = var8 - this.field_46041_a.rotationYaw; var9 < -180.0F; var9 += 360.0F) {
			}

			while(var9 >= 180.0F) {
				var9 -= 360.0F;
			}

			if(var9 > 30.0F) {
				var9 = 30.0F;
			}

			if(var9 < -30.0F) {
				var9 = -30.0F;
			}

			this.field_46041_a.rotationYaw += var9;
			this.field_46041_a.func_46010_f(this.field_46038_e);
			this.field_46041_a.func_46003_g(var6 > 0.0D);
		}
	}
}
