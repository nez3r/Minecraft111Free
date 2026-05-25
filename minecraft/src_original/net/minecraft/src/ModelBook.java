package net.minecraft.src;

public class ModelBook extends ModelBase {
	public ModelRenderer field_40330_a = (new ModelRenderer(this)).setTextureOffset(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);
	public ModelRenderer field_40328_b = (new ModelRenderer(this)).setTextureOffset(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);
	public ModelRenderer field_40329_c = (new ModelRenderer(this)).setTextureOffset(0, 10).addBox(0.0F, -4.0F, -0.99F, 5, 8, 1);
	public ModelRenderer field_40326_d = (new ModelRenderer(this)).setTextureOffset(12, 10).addBox(0.0F, -4.0F, -0.01F, 5, 8, 1);
	public ModelRenderer field_40327_e = (new ModelRenderer(this)).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
	public ModelRenderer field_40324_f = (new ModelRenderer(this)).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
	public ModelRenderer field_40325_g = (new ModelRenderer(this)).setTextureOffset(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);

	public ModelBook() {
		this.field_40330_a.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.field_40328_b.setRotationPoint(0.0F, 0.0F, 1.0F);
		this.field_40325_g.rotateAngleY = (float)Math.PI * 0.5F;
	}

	public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		this.setRotationAngles(var2, var3, var4, var5, var6, var7);
		this.field_40330_a.render(var7);
		this.field_40328_b.render(var7);
		this.field_40325_g.render(var7);
		this.field_40329_c.render(var7);
		this.field_40326_d.render(var7);
		this.field_40327_e.render(var7);
		this.field_40324_f.render(var7);
	}

	public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6) {
		float var7 = (MathHelper.sin(var1 * 0.02F) * 0.1F + 1.25F) * var4;
		this.field_40330_a.rotateAngleY = (float)Math.PI + var7;
		this.field_40328_b.rotateAngleY = -var7;
		this.field_40329_c.rotateAngleY = var7;
		this.field_40326_d.rotateAngleY = -var7;
		this.field_40327_e.rotateAngleY = var7 - var7 * 2.0F * var2;
		this.field_40324_f.rotateAngleY = var7 - var7 * 2.0F * var3;
		this.field_40329_c.rotationPointX = MathHelper.sin(var7);
		this.field_40326_d.rotationPointX = MathHelper.sin(var7);
		this.field_40327_e.rotationPointX = MathHelper.sin(var7);
		this.field_40324_f.rotationPointX = MathHelper.sin(var7);
	}
}
