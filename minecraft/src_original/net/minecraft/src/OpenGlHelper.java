package net.minecraft.src;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;

public class OpenGlHelper {
	public static int lightmapDisabled;
	public static int lightmapEnabled;
	private static boolean useMultitextureARB = false;

	public static void initializeTextures() {
		useMultitextureARB = GLContext.getCapabilities().GL_ARB_multitexture && !GLContext.getCapabilities().OpenGL13;
		if(useMultitextureARB) {
			lightmapDisabled = '\u84c0';
			lightmapEnabled = '\u84c1';
		} else {
			lightmapDisabled = '\u84c0';
			lightmapEnabled = '\u84c1';
		}

	}

	public static void setActiveTexture(int var0) {
		if(useMultitextureARB) {
			ARBMultitexture.glActiveTextureARB(var0);
		} else {
			GL13.glActiveTexture(var0);
		}

	}

	public static void setClientActiveTexture(int var0) {
		if(useMultitextureARB) {
			ARBMultitexture.glClientActiveTextureARB(var0);
		} else {
			GL13.glClientActiveTexture(var0);
		}

	}

	public static void setLightmapTextureCoords(int var0, float var1, float var2) {
		if(useMultitextureARB) {
			ARBMultitexture.glMultiTexCoord2fARB(var0, var1, var2);
		} else {
			GL13.glMultiTexCoord2f(var0, var1, var2);
		}

	}
}
