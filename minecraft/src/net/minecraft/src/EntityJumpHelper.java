package net.minecraft.src;

public class EntityJumpHelper {
	private EntityLiving field_46132_a;
	private boolean field_46131_b = false;

	public EntityJumpHelper(EntityLiving var1) {
		this.field_46132_a = var1;
	}

	public void func_46129_a() {
		this.field_46131_b = true;
	}

	public void func_46130_b() {
		if(this.field_46131_b) {
			this.field_46132_a.func_46003_g(true);
			this.field_46131_b = false;
		}
	}
}
