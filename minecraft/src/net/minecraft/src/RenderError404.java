package net.minecraft.src;

import org.lwjgl.opengl.GL11;

/**
 * Renderer for Error404 entity
 * Renders with corrupted/glitchy texture effect
 */
public class RenderError404 extends RenderLiving {
    private ModelBiped modelBipedMain;

    public RenderError404(ModelBiped model, float shadowSize) {
        super(model, shadowSize);
        this.modelBipedMain = model;
    }

    public void renderEntityError404(EntityError404 entity, double x, double y, double z, float yaw, float partialTicks) {
        GL11.glPushMatrix();

        // Corrupted texture effect - flicker and distort
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Random color tint for corruption effect
        float r = 0.5F + entity.worldObj.rand.nextFloat() * 0.5F;
        float g = 0.5F + entity.worldObj.rand.nextFloat() * 0.5F;
        float b = 0.5F + entity.worldObj.rand.nextFloat() * 0.5F;
        GL11.glColor4f(r, g, b, 0.8F);

        // Random position offset for glitch
        x += (entity.worldObj.rand.nextDouble() - 0.5) * 0.5;
        y += (entity.worldObj.rand.nextDouble() - 0.5) * 0.5;
        z += (entity.worldObj.rand.nextDouble() - 0.5) * 0.5;

        super.a(entity, x, y, z, yaw, partialTicks);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public void a(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        this.renderEntityError404((EntityError404)entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        this.renderEntityError404((EntityError404)entity, x, y, z, yaw, partialTicks);
    }
}
