package net.minecraft.src;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

/**
 * Temporary render-state effects used by the manual /event command.
 */
public class RenderHorrorEffects {
    private static final Random RANDOM = new Random();
    private static int effect;
    private static long endTime;
    private static int frame;
    private static int repeatsRemaining;
    private static long repeatDuration;
    private static String repeatSound;
    private static long repeatSoundDuration;

    public static void trigger(int id, long duration) {
        trigger(id, duration, 1, null, 0L);
    }

    public static void trigger(int id, long duration, int repeats, String sound, long soundDuration) {
        effect = id;
        endTime = System.currentTimeMillis() + duration;
        frame = 0;
        repeatsRemaining = Math.max(1, repeats);
        repeatDuration = duration;
        repeatSound = sound;
        repeatSoundDuration = soundDuration;
        playSound(sound, soundDuration);
    }

    public static void tick() {
        if (endTime != 0L && System.currentTimeMillis() >= endTime) {
            if (repeatsRemaining > 1) {
                repeatsRemaining--;
                endTime = System.currentTimeMillis() + repeatDuration;
                frame = 0;
                playSound(repeatSound, repeatSoundDuration);
            } else {
                effect = 0;
                endTime = 0L;
                repeatSound = null;
            }
        }
        frame++;
    }

    public static boolean active(int id) {
        return effect == id && endTime > System.currentTimeMillis();
    }

    public static int getFrame() {
        return frame;
    }

    public static int getActiveEffect() {
        return endTime > System.currentTimeMillis() ? effect : 0;
    }

    public static void beforeWorldRender() {
        if (effect == 45) {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace((frame & 1) == 0 ? GL11.GL_FRONT : GL11.GL_BACK);
        } else if (effect == 46) {
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(RANDOM.nextFloat() * 0.35F, RANDOM.nextFloat() * 0.35F, 0.0F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        } else if (effect == 49 && frame % 2 == 0) {
            GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
            GL11.glLogicOp(GL11.GL_XOR);
        } else if (effect == 56) {
            GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
            GL11.glLogicOp(GL11.GL_XOR);
        } else if (effect == 57) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_GREATER);
        } else if (effect == 58) {
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(RANDOM.nextFloat(), RANDOM.nextFloat(), 0.0F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }
    }

    public static void afterWorldRender() {
        if (effect == 46) {
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }
        if (effect == 45) {
            GL11.glCullFace(GL11.GL_BACK);
        }
        if (effect == 49) {
            GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        }
        if (effect == 56) {
            GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        }
        if (effect == 57) {
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }
        if (effect == 58) {
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }
    }

    public static void corruptLightmap(int[] colors) {
        if (effect != 47 || colors == null) return;
        int mode = (frame / 2) % 3;
        for (int i = 0; i < colors.length; i++) {
            if (mode == 0) colors[i] = 0xFFFFFFFF;
            else if (mode == 1) colors[i] = 0xFF000000;
            else colors[i] = 0xFF20FF20;
        }
    }

    public static void overrideClearColor() {
        if (effect == 53 && frame % 3 == 0) {
            int mode = (frame / 3) % 3;
            if (mode == 0) GL11.glClearColor(1.0F, 0.0F, 0.0F, 1.0F);
            else if (mode == 1) GL11.glClearColor(0.6F, 0.0F, 1.0F, 1.0F);
            else GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public static void applyProjectionShear() {
        if (effect != 54 || frame % 3 != 0) return;
        FloatBuffer matrix = ByteBuffer.allocateDirect(16 * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
        matrix.put(new float[] {
            1.0F, 0.0F, 0.0F, 0.0F,
            0.12F, 1.0F, 0.0F, 0.0F,
            0.0F, 0.08F, 1.0F, 0.0F,
            0.0F, 0.0F, 0.0F, 1.0F
        });
        matrix.flip();
        GL11.glMultMatrix(matrix);
    }

    public static void beginHud() {
        if (effect == 55) {
            GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        }
    }

    public static void endHud() {
        if (effect == 55) GL11.glPopAttrib();
    }

    public static void renderSlicedBands(int width, int height) {
        if (effect != 52) return;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = 0; i < 5; i++) {
            int y = i * height / 5;
            int offset = ((frame + i * 17) % 40) - 20;
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.28F);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(offset, y);
            GL11.glVertex2f(width + offset, y);
            GL11.glVertex2f(width + offset, y + height / 5 - 2);
            GL11.glVertex2f(offset, y + height / 5 - 2);
            GL11.glEnd();
        }
        GL11.glPopAttrib();
    }

    public static void renderGuaranteedOverlay(int width, int height) {
        if (effect != 48 && effect != 52 && effect != 55 && effect != 56 && effect != 58) return;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(effect == 55 || effect == 58 ? GL11.GL_ONE : GL11.GL_SRC_ALPHA,
            effect == 55 || effect == 58 ? GL11.GL_ONE : GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, width, height, 0.0D, -1.0D, 1.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        if (effect == 48) {
            int sector = (frame % 6) * width / 6;
            drawOverlayRect(sector, 0, width / 6, height, 0.0F, 0.0F, 0.0F, 0.88F);
            drawOverlayRect((sector + width / 3) % width, height / 3, width / 8,
                height / 3, 0.05F, 0.05F, 0.05F, 0.95F);
        } else if (effect == 52) {
            for (int i = 0; i < 6; i++) {
                int y = i * height / 6;
                int offset = ((frame * 7 + i * 19) % 60) - 30;
                drawOverlayRect(offset, y, width, height / 6 - 3,
                    0.02F, 0.02F, 0.02F, 0.72F);
            }
        } else if (effect == 55) {
            drawOverlayRect(0, 0, width, 5, 1.0F, 1.0F, 1.0F, 0.95F);
            drawOverlayRect(0, height - 5, width, 5, 1.0F, 1.0F, 1.0F, 0.95F);
        } else if (effect == 56) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            int bandHeight = Math.max(1, height / 24);
            for (int i = 0; i < 24; i++) {
                int offset = ((frame * 11 + i * 23) % 80) - 40;
                GL11.glScissor(0, height - (i + 1) * bandHeight, width, bandHeight);
                drawOverlayRect(offset, i * bandHeight, width, bandHeight,
                    1.0F, 1.0F, 1.0F, 0.22F);
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else if (effect == 58) {
            for (int i = 0; i < 50; i++) {
                int x = (frame * 13 + i * 47) % Math.max(1, width);
                int y = (frame * 7 + i * 31) % Math.max(1, height);
                drawOverlayRect(x, y, Math.max(2, width / 8), Math.max(2, height / 12),
                    1.0F, 1.0F, 1.0F, 0.08F);
            }
        }

        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopAttrib();
    }

    private static void drawOverlayRect(int x, int y, int width, int height,
                                        float red, float green, float blue, float alpha) {
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width, y);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x, y + height);
        GL11.glEnd();
    }

    public static void jitterModel() {
        if (effect == 51 && frame % 2 == 0) {
            GL11.glRotatef((RANDOM.nextFloat() - 0.5F) * 35.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef((RANDOM.nextFloat() - 0.5F) * 35.0F, 0.0F, 1.0F, 0.0F);
        }
    }

    public static boolean shatterVertices() {
        return effect == 56 && (frame & 1) == 0;
    }

    public static double vertexNoise() {
        return (RANDOM.nextDouble() - 0.5D) * 3.0D;
    }

    public static void reset() {
        effect = 0;
        endTime = 0L;
        frame = 0;
        repeatsRemaining = 0;
        repeatDuration = 0L;
        repeatSound = null;
        repeatSoundDuration = 0L;
    }

    private static void playSound(String sound, long duration) {
        if (sound == null) {
            return;
        }
        Minecraft var0 = Minecraft.theMinecraft;
        if (var0 != null && var0.sndManager != null) {
            var0.sndManager.playHorrorEffectSound(sound, 1.0F, duration);
        }
    }
}
