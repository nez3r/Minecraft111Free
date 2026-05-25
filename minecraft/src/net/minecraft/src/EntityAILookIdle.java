package net.minecraft.src;

public class EntityAILookIdle extends EntityAIBase {
	private EntityLiving field_46089_a;
	private double field_46087_b;
	private double field_46088_c;
	private int field_46086_d = 0;

	public EntityAILookIdle(EntityLiving var1) {
		this.field_46089_a = var1;
		this.func_46079_a(3);
	}

	public boolean func_46082_a() {
		return this.field_46089_a.func_46004_aK().nextFloat() < 0.02F;
	}

	public boolean func_46084_g() {
		return this.field_46086_d >= 0;
	}

	public void func_46080_e() {
		double var1 = Math.PI * 2.0D * this.field_46089_a.func_46004_aK().nextDouble();
		this.field_46087_b = Math.cos(var1);
		this.field_46088_c = Math.sin(var1);
		this.field_46086_d = 20 + this.field_46089_a.func_46004_aK().nextInt(20);
	}

	public void func_46081_b() {
		--this.field_46086_d;
		this.field_46089_a.func_46008_aG().func_46143_a(this.field_46089_a.posX + this.field_46087_b, this.field_46089_a.posY + (double)this.field_46089_a.getEyeHeight(), this.field_46089_a.posZ + this.field_46088_c, 10.0F, (float)this.field_46089_a.getVerticalFaceSpeed());
	}

	public int func_46083_c() {
		return super.func_46083_c();
	}

	public void func_46079_a(int var1) {
		super.func_46079_a(var1);
	}

	public void func_46077_d() {
		super.func_46077_d();
	}

	public boolean func_46078_f() {
		return super.func_46078_f();
	}
}
