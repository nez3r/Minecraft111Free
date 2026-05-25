package net.minecraft.src;

public abstract class GenLayer {
	private long worldGenSeed;
	protected GenLayer parent;
	private long chunkSeed;
	private long baseSeed;

	public static GenLayer[] func_35497_a(long var0) {
		LayerIsland var2 = new LayerIsland(1L);
		GenLayerZoomFuzzy var10 = new GenLayerZoomFuzzy(2000L, var2);
		GenLayerIsland var11 = new GenLayerIsland(1L, var10);
		GenLayerZoom var12 = new GenLayerZoom(2001L, var11);
		var11 = new GenLayerIsland(2L, var12);
		GenLayerSnow var13 = new GenLayerSnow(2L, var11);
		var12 = new GenLayerZoom(2002L, var13);
		var11 = new GenLayerIsland(3L, var12);
		var12 = new GenLayerZoom(2003L, var11);
		var11 = new GenLayerIsland(4L, var12);
		GenLayerMushroomIsland var16 = new GenLayerMushroomIsland(5L, var11);
		byte var3 = 4;
		GenLayer var4 = GenLayerZoom.func_35515_a(1000L, var16, 0);
		GenLayerRiverInit var14 = new GenLayerRiverInit(100L, var4);
		var4 = GenLayerZoom.func_35515_a(1000L, var14, var3 + 2);
		GenLayerRiver var15 = new GenLayerRiver(1L, var4);
		GenLayerSmooth var17 = new GenLayerSmooth(1000L, var15);
		GenLayer var5 = GenLayerZoom.func_35515_a(1000L, var16, 0);
		GenLayerVillageLandscape var18 = new GenLayerVillageLandscape(200L, var5);
		var5 = GenLayerZoom.func_35515_a(1000L, var18, 2);
		Object var19 = new GenLayerHills(1000L, var5);
		Object var6 = new GenLayerTemperature((GenLayer)var19);
		Object var7 = new GenLayerDownfall((GenLayer)var19);

		for(int var8 = 0; var8 < var3; ++var8) {
			var19 = new GenLayerZoom((long)(1000 + var8), (GenLayer)var19);
			if(var8 == 0) {
				var19 = new GenLayerIsland(3L, (GenLayer)var19);
			}

			if(var8 == 1) {
				var19 = new GenLayerShore(1000L, (GenLayer)var19);
			}

			if(var8 == 1) {
				var19 = new GenLayerSwampRivers(1000L, (GenLayer)var19);
			}

			GenLayerSmoothZoom var20 = new GenLayerSmoothZoom((long)(1000 + var8), (GenLayer)var6);
			var6 = new GenLayerTemperatureMix(var20, (GenLayer)var19, var8);
			GenLayerSmoothZoom var22 = new GenLayerSmoothZoom((long)(1000 + var8), (GenLayer)var7);
			var7 = new GenLayerDownfallMix(var22, (GenLayer)var19, var8);
		}

		GenLayerSmooth var24 = new GenLayerSmooth(1000L, (GenLayer)var19);
		GenLayerRiverMix var25 = new GenLayerRiverMix(100L, var24, var17);
		GenLayer var21 = GenLayerSmoothZoom.func_35517_a(1000L, (GenLayer)var6, 2);
		GenLayer var23 = GenLayerSmoothZoom.func_35517_a(1000L, (GenLayer)var7, 2);
		GenLayerZoomVoronoi var9 = new GenLayerZoomVoronoi(10L, var25);
		var25.initWorldGenSeed(var0);
		var21.initWorldGenSeed(var0);
		var23.initWorldGenSeed(var0);
		var9.initWorldGenSeed(var0);
		return new GenLayer[]{var25, var9, var21, var23, var25};
	}

	public GenLayer(long var1) {
		this.baseSeed = var1;
		this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
		this.baseSeed += var1;
		this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
		this.baseSeed += var1;
		this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
		this.baseSeed += var1;
	}

	public void initWorldGenSeed(long var1) {
		this.worldGenSeed = var1;
		if(this.parent != null) {
			this.parent.initWorldGenSeed(var1);
		}

		this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
		this.worldGenSeed += this.baseSeed;
		this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
		this.worldGenSeed += this.baseSeed;
		this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
		this.worldGenSeed += this.baseSeed;
	}

	public void initChunkSeed(long var1, long var3) {
		this.chunkSeed = this.worldGenSeed;
		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += var1;
		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += var3;
		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += var1;
		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += var3;
	}

	protected int nextInt(int var1) {
		int var2 = (int)((this.chunkSeed >> 24) % (long)var1);
		if(var2 < 0) {
			var2 += var1;
		}

		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += this.worldGenSeed;
		return var2;
	}

	public abstract int[] getInts(int var1, int var2, int var3, int var4);
}
