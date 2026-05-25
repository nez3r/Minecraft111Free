package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldChunkManager {
	private GenLayer field_34903_b;
	private GenLayer biomeIndexLayer;
	private GenLayer temperatureLayer;
	private GenLayer rainfallLayer;
	private BiomeCache biomeCache;
	private List biomesToSpawnIn;
	public BiomeGenBase[] rendererBiomeGenCache;
	public float[] temperatureCache;

	protected WorldChunkManager() {
		this.biomeCache = new BiomeCache(this);
		this.biomesToSpawnIn = new ArrayList();
		this.biomesToSpawnIn.add(BiomeGenBase.forest);
		this.biomesToSpawnIn.add(BiomeGenBase.plains);
		this.biomesToSpawnIn.add(BiomeGenBase.taiga);
		this.biomesToSpawnIn.add(BiomeGenBase.field_46047_u);
		this.biomesToSpawnIn.add(BiomeGenBase.field_46048_t);
	}

	public WorldChunkManager(World var1) {
		this();
		GenLayer[] var2 = GenLayer.func_35497_a(var1.getWorldSeed());
		this.field_34903_b = var2[0];
		this.biomeIndexLayer = var2[1];
		this.temperatureLayer = var2[2];
		this.rainfallLayer = var2[3];
	}

	public List getBiomesToSpawnIn() {
		return this.biomesToSpawnIn;
	}

	public BiomeGenBase getBiomeGenAtChunkCoord(ChunkCoordIntPair var1) {
		return this.getBiomeGenAt(var1.chunkXPos << 4, var1.chunkZPos << 4);
	}

	public BiomeGenBase getBiomeGenAt(int var1, int var2) {
		return this.biomeCache.getBiomeGenAt(var1, var2);
	}

	public float getRainfall(int var1, int var2) {
		return this.biomeCache.getRainfall(var1, var2);
	}

	public float[] getRainfall(float[] var1, int var2, int var3, int var4, int var5) {
		IntCache.resetIntCache();
		if(var1 == null || var1.length < var4 * var5) {
			var1 = new float[var4 * var5];
		}

		int[] var6 = this.rainfallLayer.getInts(var2, var3, var4, var5);

		for(int var7 = 0; var7 < var4 * var5; ++var7) {
			float var8 = (float)var6[var7] / 65536.0F;
			if(var8 > 1.0F) {
				var8 = 1.0F;
			}

			var1[var7] = var8;
		}

		return var1;
	}

	public float getTemperature(int var1, int var2, int var3) {
		return this.getTemperatureAtHeight(this.biomeCache.getTemperature(var1, var3), var2);
	}

	public float getTemperatureAtHeight(float var1, int var2) {
		return var1;
	}

	public float[] initTemperatureCache(int var1, int var2, int var3, int var4) {
		this.temperatureCache = this.getTemperatures(this.temperatureCache, var1, var2, var3, var4);
		return this.temperatureCache;
	}

	public float[] getTemperatures(float[] var1, int var2, int var3, int var4, int var5) {
		IntCache.resetIntCache();
		if(var1 == null || var1.length < var4 * var5) {
			var1 = new float[var4 * var5];
		}

		int[] var6 = this.temperatureLayer.getInts(var2, var3, var4, var5);

		for(int var7 = 0; var7 < var4 * var5; ++var7) {
			float var8 = (float)var6[var7] / 65536.0F;
			if(var8 > 1.0F) {
				var8 = 1.0F;
			}

			var1[var7] = var8;
		}

		return var1;
	}

	public BiomeGenBase[] func_35557_b(BiomeGenBase[] var1, int var2, int var3, int var4, int var5) {
		IntCache.resetIntCache();
		if(var1 == null || var1.length < var4 * var5) {
			var1 = new BiomeGenBase[var4 * var5];
		}

		int[] var6 = this.field_34903_b.getInts(var2, var3, var4, var5);

		for(int var7 = 0; var7 < var4 * var5; ++var7) {
			var1[var7] = BiomeGenBase.biomeList[var6[var7]];
		}

		return var1;
	}

	public BiomeGenBase[] loadRendererData(int var1, int var2, int var3, int var4) {
		if(var3 == 16 && var4 == 16 && (var1 & 15) == 0 && (var2 & 15) == 0) {
			return this.biomeCache.getCachedBiomes(var1, var2);
		} else {
			this.rendererBiomeGenCache = this.loadBlockGeneratorData(this.rendererBiomeGenCache, var1, var2, var3, var4);
			return this.rendererBiomeGenCache;
		}
	}

	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] var1, int var2, int var3, int var4, int var5) {
		return this.getBiomeGenAt(var1, var2, var3, var4, var5, true);
	}

	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] var1, int var2, int var3, int var4, int var5, boolean var6) {
		IntCache.resetIntCache();
		if(var1 == null || var1.length < var4 * var5) {
			var1 = new BiomeGenBase[var4 * var5];
		}

		if(var6 && var4 == 16 && var5 == 16 && (var2 & 15) == 0 && (var3 & 15) == 0) {
			BiomeGenBase[] var9 = this.biomeCache.getCachedBiomes(var2, var3);
			System.arraycopy(var9, 0, var1, 0, var4 * var5);
			return var1;
		} else {
			int[] var7 = this.biomeIndexLayer.getInts(var2, var3, var4, var5);

			for(int var8 = 0; var8 < var4 * var5; ++var8) {
				var1[var8] = BiomeGenBase.biomeList[var7[var8]];
			}

			return var1;
		}
	}

	public boolean areBiomesViable(int var1, int var2, int var3, List var4) {
		int var5 = var1 - var3 >> 2;
		int var6 = var2 - var3 >> 2;
		int var7 = var1 + var3 >> 2;
		int var8 = var2 + var3 >> 2;
		int var9 = var7 - var5 + 1;
		int var10 = var8 - var6 + 1;
		int[] var11 = this.field_34903_b.getInts(var5, var6, var9, var10);

		for(int var12 = 0; var12 < var9 * var10; ++var12) {
			BiomeGenBase var13 = BiomeGenBase.biomeList[var11[var12]];
			if(!var4.contains(var13)) {
				return false;
			}
		}

		return true;
	}

	public ChunkPosition func_35556_a(int var1, int var2, int var3, List var4, Random var5) {
		int var6 = var1 - var3 >> 2;
		int var7 = var2 - var3 >> 2;
		int var8 = var1 + var3 >> 2;
		int var9 = var2 + var3 >> 2;
		int var10 = var8 - var6 + 1;
		int var11 = var9 - var7 + 1;
		int[] var12 = this.field_34903_b.getInts(var6, var7, var10, var11);
		ChunkPosition var13 = null;
		int var14 = 0;

		for(int var15 = 0; var15 < var12.length; ++var15) {
			int var16 = var6 + var15 % var10 << 2;
			int var17 = var7 + var15 / var10 << 2;
			BiomeGenBase var18 = BiomeGenBase.biomeList[var12[var15]];
			if(var4.contains(var18) && (var13 == null || var5.nextInt(var14 + 1) == 0)) {
				var13 = new ChunkPosition(var16, 0, var17);
				++var14;
			}
		}

		return var13;
	}

	public void cleanupCache() {
		this.biomeCache.cleanupCache();
	}
}
