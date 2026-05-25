package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

/**
 * Renderer for Entity404 stalker
 */
public class Render404 extends RenderLiving {
    private ModelBiped modelBipedMain;

    public Render404(ModelBiped model, float shadowSize) {
        super(model, shadowSize);
        this.modelBipedMain = model;
    }

    public void renderEntity404(Entity404 entity, double x, double y, double z, float yaw, float partialTicks) {
        if (!entity.isVisible()) {
            return; // Don't render when invisible
        }

        // Apply transparency and dark color
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Dark color tint
        GL11.glColor4f(0.1F, 0.1F, 0.1F, 0.7F);

        // Glitch effect - random offset
        if (entity.worldObj.rand.nextInt(10) == 0) {
            x += (entity.worldObj.rand.nextDouble() - 0.5) * 0.2;
            y += (entity.worldObj.rand.nextDouble() - 0.5) * 0.2;
            z += (entity.worldObj.rand.nextDouble() - 0.5) * 0.2;
        }

        super.a(entity, x, y, z, yaw, partialTicks);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public void a(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        this.renderEntity404((Entity404)entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        this.renderEntity404((Entity404)entity, x, y, z, yaw, partialTicks);
    }
}
