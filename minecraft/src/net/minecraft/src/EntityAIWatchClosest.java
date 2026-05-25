package net.minecraft.src;

public class EntityAIWatchClosest extends EntityAIBase {
	private EntityLiving field_46105_a;
	private EntityLiving field_46103_b;
	private World field_46104_c;
	private float field_46101_d;
	private int field_46102_e;

	public EntityAIWatchClosest(EntityLiving var1, World var2, float var3) {
		this.field_46105_a = var1;
		this.field_46104_c = var2;
		this.field_46101_d = var3;
		this.func_46079_a(3);
	}

	public boolean func_46082_a() {
		if(this.field_46105_a.func_46004_aK().nextFloat() >= 0.02F) {
			return false;
		} else {
			this.field_46103_b = this.field_46104_c.getClosestPlayerToEntity(this.field_46105_a, (double)this.field_46101_d);
			return this.field_46103_b != null;
		}
	}

	public boolean func_46084_g() {
		return !this.field_46103_b.isEntityAlive() ? false : (this.field_46105_a.getDistanceSqToEntity(this.field_46103_b) > (double)(this.field_46101_d * this.field_46101_d) ? false : this.field_46102_e > 0);
	}

	public void func_46080_e() {
		this.field_46102_e = 40 + this.field_46105_a.func_46004_aK().nextInt(40);
	}

	public void func_46077_d() {
		this.field_46103_b = null;
	}

	public void func_46081_b() {
		this.field_46105_a.func_46008_aG().func_46143_a(this.field_46103_b.posX, this.field_46103_b.posY + (double)this.field_46103_b.getEyeHeight(), this.field_46103_b.posZ, 10.0F, (float)this.field_46105_a.getVerticalFaceSpeed());
		--this.field_46102_e;
	}

	public int func_46083_c() {
		return super.func_46083_c();
	}

	public void func_46079_a(int var1) {
		super.func_46079_a(var1);
	}

	public boolean func_46078_f() {
		return super.func_46078_f();
	}
}
