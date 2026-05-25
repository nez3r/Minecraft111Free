package net.minecraft.src;

import java.util.Random;

public class EntityAIWander extends EntityAIBase {
	private EntityCreature field_46100_a;
	private double field_46098_b;
	private double field_46099_c;
	private double field_46097_d;

	public EntityAIWander(EntityCreature var1) {
		this.field_46100_a = var1;
		this.func_46079_a(3);
	}

	public boolean func_46082_a() {
		if(this.field_46100_a.func_46011_aM() >= 100) {
			return false;
		} else if(this.field_46100_a.func_46004_aK().nextInt(120) != 0) {
			return false;
		} else {
			Vec3D var1 = this.func_46096_h();
			if(var1 == null) {
				return false;
			} else {
				this.field_46098_b = var1.xCoord;
				this.field_46099_c = var1.yCoord;
				this.field_46097_d = var1.zCoord;
				return true;
			}
		}
	}

	public boolean func_46084_g() {
		return !this.field_46100_a.func_46012_aJ().func_46072_b();
	}

	public void func_46080_e() {
		this.field_46100_a.func_46012_aJ().func_46071_a(this.field_46098_b, this.field_46099_c, this.field_46097_d, this.field_46100_a.func_46013_aQ());
	}

	private Vec3D func_46096_h() {
		Random var1 = this.field_46100_a.func_46004_aK();
		boolean var2 = false;
		int var3 = -1;
		int var4 = -1;
		int var5 = -1;
		float var6 = -99999.0F;

		for(int var7 = 0; var7 < 10; ++var7) {
			int var8 = MathHelper.floor_double(this.field_46100_a.posX + (double)var1.nextInt(13) - 6.0D);
			int var9 = MathHelper.floor_double(this.field_46100_a.posY + (double)var1.nextInt(7) - 3.0D);
			int var10 = MathHelper.floor_double(this.field_46100_a.posZ + (double)var1.nextInt(13) - 6.0D);
			float var11 = this.field_46100_a.getBlockPathWeight(var8, var9, var10);
			if(var11 > var6) {
				var6 = var11;
				var3 = var8;
				var4 = var9;
				var5 = var10;
				var2 = true;
			}
		}

		if(var2) {
			return Vec3D.createVector((double)var3, (double)var4, (double)var5);
		} else {
			return null;
		}
	}

	public int func_46083_c() {
		return super.func_46083_c();
	}

	public void func_46079_a(int var1) {
		super.func_46079_a(var1);
	}

	public void func_46081_b() {
		super.func_46081_b();
	}

	public void func_46077_d() {
		super.func_46077_d();
	}

	public boolean func_46078_f() {
		return super.func_46078_f();
	}
}
