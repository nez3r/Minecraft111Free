package net.minecraft.src;

public class PathNavigate implements INavigate {
	private EntityLiving field_46076_a;
	private World field_46074_b;
	private PathEntity field_46075_c;
	private float field_46073_d;

	public PathNavigate(EntityLiving var1, World var2) {
		this.field_46076_a = var1;
		this.field_46074_b = var2;
	}

	public void func_46071_a(double var1, double var3, double var5, float var7) {
		this.field_46075_c = this.field_46074_b.getEntityPathToXYZ(this.field_46076_a, (int)var1, (int)var3, (int)var5, 10.0F);
		this.field_46073_d = var7;
	}

	public void func_46070_a(EntityLiving var1, float var2) {
		this.field_46075_c = this.field_46074_b.getPathToEntity(this.field_46076_a, var1, 16.0F);
		this.field_46073_d = var2;
	}

	public void func_46069_a() {
		if(this.field_46075_c != null) {
			float var1 = this.field_46076_a.width;
			Vec3D var2 = this.field_46075_c.getPosition(this.field_46076_a);

			while(var2 != null && var2.squareDistanceTo(this.field_46076_a.posX, var2.yCoord, this.field_46076_a.posZ) < (double)(var1 * var1)) {
				this.field_46075_c.incrementPathIndex();
				if(this.field_46075_c.isFinished()) {
					var2 = null;
					this.field_46075_c = null;
				} else {
					var2 = this.field_46075_c.getPosition(this.field_46076_a);
				}
			}

			if(var2 != null) {
				this.field_46076_a.func_46009_aH().func_46033_a(this.field_46073_d);
				this.field_46076_a.func_46009_aH().func_46035_a(var2.xCoord, var2.yCoord, var2.zCoord);
			}
		}
	}

	public boolean func_46072_b() {
		return this.field_46075_c == null || this.field_46075_c.isFinished();
	}
}
