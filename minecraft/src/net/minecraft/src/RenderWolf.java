package net.minecraft.src;

public class RenderWolf extends RenderLiving {
	public RenderWolf(ModelBase var1, float var2) {
		super(var1, var2);
	}

	public void a(EntityWolf var1, double var2, double var4, double var6, float var8, float var9) {
		super.a(var1, var2, var4, var6, var8, var9);
	}

	protected float a(EntityWolf var1, float var2) {
		return var1.setTailRotation();
	}

	protected void b(EntityWolf var1, float var2) {
	}

	protected void preRenderCallback(EntityLiving var1, float var2) {
		this.b((EntityWolf)var1, var2);
	}

	protected float handleRotationFloat(EntityLiving var1, float var2) {
		return this.a((EntityWolf)var1, var2);
	}

	public void a(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
		this.a((EntityWolf)var1, var2, var4, var6, var8, var9);
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.a((EntityWolf)var1, var2, var4, var6, var8, var9);
	}
}
