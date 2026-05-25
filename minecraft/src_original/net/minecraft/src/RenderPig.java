package net.minecraft.src;

public class RenderPig extends RenderLiving {
	public RenderPig(ModelBase var1, ModelBase var2, float var3) {
		super(var1, var3);
		this.setRenderPassModel(var2);
	}

	protected int a(EntityPig var1, int var2, float var3) {
		this.loadTexture("/mob/saddle.png");
		return var2 == 0 && var1.getSaddled() ? 1 : -1;
	}

	public void a(EntityPig var1, double var2, double var4, double var6, float var8, float var9) {
		super.a(var1, var2, var4, var6, var8, var9);
	}

	protected int shouldRenderPass(EntityLiving var1, int var2, float var3) {
		return this.a((EntityPig)var1, var2, var3);
	}

	public void a(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
		this.a((EntityPig)var1, var2, var4, var6, var8, var9);
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.a((EntityPig)var1, var2, var4, var6, var8, var9);
	}
}
