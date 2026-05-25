package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

/**
 * Renderer for EntityBlackDemon
 */
public class RenderBlackDemon extends RenderLiving {
    private ModelBiped modelBipedMain;

    public RenderBlackDemon(ModelBiped model, float shadowSize) {
        super(model, shadowSize);
        this.modelBipedMain = model;
    }

    public void renderBlackDemon(EntityBlackDemon entity, double x, double y, double z, float yaw, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Scale to 3x size
        GL11.glScalef(3.0F, 3.0F, 3.0F);

        // Completely black with slight transparency
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.9F);

        // Glitch effect - random jitter
        if (entity.worldObj.rand.nextInt(3) == 0) {
            x += (entity.worldObj.rand.nextDouble() - 0.5) * 0.5;
            y += (entity.worldObj.rand.nextDouble() - 0.5) * 0.5;
            z += (entity.worldObj.rand.nextDouble() - 0.5) * 0.5;
        }

        // Render with glowing white eyes effect
        super.a(entity, x / 3.0, y / 3.0, z / 3.0, yaw, partialTicks);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void a(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        this.renderBlackDemon((EntityBlackDemon)entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        this.renderBlackDemon((EntityBlackDemon)entity, x, y, z, yaw, partialTicks);
    }
}
