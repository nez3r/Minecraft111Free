package net.minecraft.src;

public class EntityAIAttackOnCollide extends EntityAIBase {
	World field_46095_a;
	EntityMob field_46093_b;
	EntityLiving field_46094_c;
	int field_46091_d = 0;
	float field_46092_e;

	public EntityAIAttackOnCollide(EntityMob var1, World var2, float var3) {
		this.field_46093_b = var1;
		this.field_46095_a = var2;
		this.field_46092_e = var3;
		this.func_46079_a(3);
	}

	public boolean func_46082_a() {
		this.field_46094_c = this.func_46090_h();
		return this.field_46094_c != null;
	}

	public void func_46081_b() {
		this.field_46093_b.func_46012_aJ().func_46070_a(this.field_46094_c, this.field_46093_b.func_46013_aQ());
		this.field_46093_b.func_46008_aG().func_46141_a(this.field_46094_c, 30.0F, 30.0F);
		this.field_46091_d = Math.max(this.field_46091_d - 1, 0);
		double var1 = 4.0D;
		if(this.field_46093_b.getDistanceSqToEntity(this.field_46094_c) <= var1) {
			if(this.field_46091_d <= 0) {
				this.field_46091_d = 20;
				this.field_46093_b.attackEntityAsMob(this.field_46094_c);
			}
		}
	}

	private EntityLiving func_46090_h() {
		Object var1 = this.field_46093_b.func_46007_aL();
		if(var1 == null) {
			var1 = this.field_46095_a.getClosestVulnerablePlayerToEntity(this.field_46093_b, (double)this.field_46092_e);
		}

		return (EntityLiving)(var1 == null ? null : (((EntityLiving)var1).boundingBox.maxY > this.field_46093_b.boundingBox.minY && ((EntityLiving)var1).boundingBox.minY < this.field_46093_b.boundingBox.maxY ? (!this.field_46093_b.canEntityBeSeen((Entity)var1) ? null : var1) : null));
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

	public void func_46080_e() {
		super.func_46080_e();
	}

	public boolean func_46078_f() {
		return super.func_46078_f();
	}

	public boolean func_46084_g() {
		return super.func_46084_g();
	}
}
