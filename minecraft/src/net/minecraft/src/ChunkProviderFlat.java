package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ChunkProviderFlat implements IChunkProvider {
	private World field_46055_a;
	private Random field_46053_b;
	private final boolean field_46054_c;
	private MapGenVillage field_46052_d = new MapGenVillage(1);

	public ChunkProviderFlat(World var1, long var2, boolean var4) {
		this.field_46055_a = var1;
		this.field_46054_c = var4;
		this.field_46053_b = new Random(var2);
	}

	private void func_46051_a(byte[] var1) {
		int var2 = var1.length / 256;

		for(int var3 = 0; var3 < 16; ++var3) {
			for(int var4 = 0; var4 < 16; ++var4) {
				for(int var5 = 0; var5 < var2; ++var5) {
					int var6 = 0;
					if(var5 == 0) {
						var6 = Block.bedrock.blockID;
					} else if(var5 <= 2) {
						var6 = Block.dirt.blockID;
					} else if(var5 == 3) {
						var6 = Block.grass.blockID;
					}

					var1[var3 << 11 | var4 << 7 | var5] = (byte)var6;
				}
			}
		}

	}

	public Chunk loadChunk(int var1, int var2) {
		return this.provideChunk(var1, var2);
	}

	public Chunk provideChunk(int var1, int var2) {
		byte[] var3 = new byte[16 * this.field_46055_a.worldHeight * 16];
		Chunk var4 = new Chunk(this.field_46055_a, var3, var1, var2);
		this.func_46051_a(var3);
		if(this.field_46054_c) {
			this.field_46052_d.generate(this, this.field_46055_a, var1, var2, var3);
		}

		var4.generateSkylightMap();
		return var4;
	}

	public boolean chunkExists(int var1, int var2) {
		return true;
	}

	public void populate(IChunkProvider var1, int var2, int var3) {
		this.field_46053_b.setSeed(this.field_46055_a.getWorldSeed());
		long var4 = this.field_46053_b.nextLong() / 2L * 2L + 1L;
		long var6 = this.field_46053_b.nextLong() / 2L * 2L + 1L;
		this.field_46053_b.setSeed((long)var2 * var4 + (long)var3 * var6 ^ this.field_46055_a.getWorldSeed());
		if(this.field_46054_c) {
			this.field_46052_d.generateStructuresInChunk(this.field_46055_a, this.field_46053_b, var2, var3);
		}

	}

	public boolean saveChunks(boolean var1, IProgressUpdate var2) {
		return true;
	}

	public boolean unload100OldestChunks() {
		return false;
	}

	public boolean canSave() {
		return true;
	}

	public String makeString() {
		return "FlatLevelSource";
	}

	public List func_40377_a(EnumCreatureType var1, int var2, int var3, int var4) {
		WorldChunkManager var5 = this.field_46055_a.getWorldChunkManager();
		if(var5 == null) {
			return null;
		} else {
			BiomeGenBase var6 = var5.getBiomeGenAtChunkCoord(new ChunkCoordIntPair(var2 >> 4, var4 >> 4));
			return var6 == null ? null : var6.getSpawnableList(var1);
		}
	}

	public ChunkPosition func_40376_a(World var1, String var2, int var3, int var4, int var5) {
		return null;
	}
}
