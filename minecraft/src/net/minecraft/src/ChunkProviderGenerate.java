package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ChunkProviderGenerate implements IChunkProvider {
	private Random rand;
	private NoiseGeneratorOctaves noiseGen1;
	private NoiseGeneratorOctaves noiseGen2;
	private NoiseGeneratorOctaves noiseGen3;
	private NoiseGeneratorOctaves noiseGen4;
	public NoiseGeneratorOctaves noiseGen5;
	public NoiseGeneratorOctaves noiseGen6;
	public NoiseGeneratorOctaves mobSpawnerNoise;
	private World worldObj;
	private final boolean mapFeaturesEnabled;
	private double[] field_4180_q;
	private double[] stoneNoise = new double[256];
	private MapGenBase caveGenerator = new MapGenCaves();
	private MapGenStronghold strongholdGenerator = new MapGenStronghold();
	private MapGenVillage villageGenerator = new MapGenVillage(0);
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
	private MapGenBase ravineGenerator = new MapGenRavine();
	private BiomeGenBase[] biomesForGeneration;
	double[] noise3;
	double[] noise1;
	double[] noise2;
	double[] noise5;
	double[] noise6;
	float[] field_35388_l;
	int[][] unusedIntArray32x32 = new int[32][32];

	public ChunkProviderGenerate(World var1, long var2, boolean var4) {
		this.worldObj = var1;
		this.mapFeaturesEnabled = var4;
		this.rand = new Random(var2);
		this.noiseGen1 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseGen2 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseGen3 = new NoiseGeneratorOctaves(this.rand, 8);
		this.noiseGen4 = new NoiseGeneratorOctaves(this.rand, 4);
		this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
		this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
		this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
	}

	public void generateTerrain(int var1, int var2, byte[] var3) {
		byte var4 = 4;
		int var5 = this.worldObj.worldHeight / 8;
		int var6 = this.worldObj.seaLevel;
		int var7 = var4 + 1;
		int var8 = this.worldObj.worldHeight / 8 + 1;
		int var9 = var4 + 1;
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().func_35557_b(this.biomesForGeneration, var1 * 4 - 2, var2 * 4 - 2, var7 + 5, var9 + 5);
		this.field_4180_q = this.initializeNoiseField(this.field_4180_q, var1 * var4, 0, var2 * var4, var7, var8, var9);

		for(int var10 = 0; var10 < var4; ++var10) {
			for(int var11 = 0; var11 < var4; ++var11) {
				for(int var12 = 0; var12 < var5; ++var12) {
					double var13 = 0.125D;
					double var15 = this.field_4180_q[((var10 + 0) * var9 + var11 + 0) * var8 + var12 + 0];
					double var17 = this.field_4180_q[((var10 + 0) * var9 + var11 + 1) * var8 + var12 + 0];
					double var19 = this.field_4180_q[((var10 + 1) * var9 + var11 + 0) * var8 + var12 + 0];
					double var21 = this.field_4180_q[((var10 + 1) * var9 + var11 + 1) * var8 + var12 + 0];
					double var23 = (this.field_4180_q[((var10 + 0) * var9 + var11 + 0) * var8 + var12 + 1] - var15) * var13;
					double var25 = (this.field_4180_q[((var10 + 0) * var9 + var11 + 1) * var8 + var12 + 1] - var17) * var13;
					double var27 = (this.field_4180_q[((var10 + 1) * var9 + var11 + 0) * var8 + var12 + 1] - var19) * var13;
					double var29 = (this.field_4180_q[((var10 + 1) * var9 + var11 + 1) * var8 + var12 + 1] - var21) * var13;

					for(int var31 = 0; var31 < 8; ++var31) {
						double var32 = 0.25D;
						double var34 = var15;
						double var36 = var17;
						double var38 = (var19 - var15) * var32;
						double var40 = (var21 - var17) * var32;

						for(int var42 = 0; var42 < 4; ++var42) {
							int var43 = var42 + var10 * 4 << this.worldObj.xShift | 0 + var11 * 4 << this.worldObj.heightShift | var12 * 8 + var31;
							int var44 = 1 << this.worldObj.heightShift;
							var43 -= var44;
							double var45 = 0.25D;
							double var49 = (var36 - var34) * var45;
							double var47 = var34 - var49;

							for(int var51 = 0; var51 < 4; ++var51) {
								var47 += var49;
								if(var47 > 0.0D) {
									var43 += var44;
									var3[var43] = (byte)Block.stone.blockID;
								} else if(var12 * 8 + var31 < var6) {
									var43 += var44;
									var3[var43] = (byte)Block.waterStill.blockID;
								} else {
									var43 += var44;
									var3[var43] = 0;
								}
							}

							var34 += var38;
							var36 += var40;
						}

						var15 += var23;
						var17 += var25;
						var19 += var27;
						var21 += var29;
					}
				}
			}
		}

	}

	public void replaceBlocksForBiome(int var1, int var2, byte[] var3, BiomeGenBase[] var4) {
		int var5 = this.worldObj.seaLevel;
		double var6 = 1.0D / 32.0D;
		this.stoneNoise = this.noiseGen4.generateNoiseOctaves(this.stoneNoise, var1 * 16, var2 * 16, 0, 16, 16, 1, var6 * 2.0D, var6 * 2.0D, var6 * 2.0D);
		float[] var8 = this.worldObj.getWorldChunkManager().initTemperatureCache(var1 * 16, var2 * 16, 16, 16);

		for(int var9 = 0; var9 < 16; ++var9) {
			for(int var10 = 0; var10 < 16; ++var10) {
				float var11 = var8[var10 + var9 * 16];
				BiomeGenBase var12 = var4[var10 + var9 * 16];
				int var13 = (int)(this.stoneNoise[var9 + var10 * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
				int var14 = -1;
				byte var15 = var12.topBlock;
				byte var16 = var12.fillerBlock;

				for(int var17 = this.worldObj.worldMaxY; var17 >= 0; --var17) {
					int var18 = (var10 * 16 + var9) * this.worldObj.worldHeight + var17;
					if(var17 <= 0 + this.rand.nextInt(5)) {
						var3[var18] = (byte)Block.bedrock.blockID;
					} else {
						byte var19 = var3[var18];
						if(var19 == 0) {
							var14 = -1;
						} else if(var19 == Block.stone.blockID) {
							if(var14 == -1) {
								if(var13 <= 0) {
									var15 = 0;
									var16 = (byte)Block.stone.blockID;
								} else if(var17 >= var5 - 4 && var17 <= var5 + 1) {
									var15 = var12.topBlock;
									var16 = var12.fillerBlock;
								}

								if(var17 < var5 && var15 == 0) {
									if(var11 < 0.15F) {
										var15 = (byte)Block.ice.blockID;
									} else {
										var15 = (byte)Block.waterStill.blockID;
									}
								}

								var14 = var13;
								if(var17 >= var5 - 1) {
									var3[var18] = var15;
								} else {
									var3[var18] = var16;
								}
							} else if(var14 > 0) {
								--var14;
								var3[var18] = var16;
								if(var14 == 0 && var16 == Block.sand.blockID) {
									var14 = this.rand.nextInt(4);
									var16 = (byte)Block.sandStone.blockID;
								}
							}
						}
					}
				}
			}
		}

	}

	public Chunk loadChunk(int var1, int var2) {
		return this.provideChunk(var1, var2);
	}

	public Chunk provideChunk(int var1, int var2) {
		this.rand.setSeed((long)var1 * 341873128712L + (long)var2 * 132897987541L);
		byte[] var3 = new byte[16 * this.worldObj.worldHeight * 16];
		Chunk var4 = new Chunk(this.worldObj, var3, var1, var2);
		this.generateTerrain(var1, var2, var3);
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, var1 * 16, var2 * 16, 16, 16);
		this.replaceBlocksForBiome(var1, var2, var3, this.biomesForGeneration);
		this.caveGenerator.generate(this, this.worldObj, var1, var2, var3);
		this.ravineGenerator.generate(this, this.worldObj, var1, var2, var3);
		if(this.mapFeaturesEnabled) {
			this.mineshaftGenerator.generate(this, this.worldObj, var1, var2, var3);
			this.villageGenerator.generate(this, this.worldObj, var1, var2, var3);
			this.strongholdGenerator.generate(this, this.worldObj, var1, var2, var3);
		}

		var4.generateSkylightMap();
		return var4;
	}

	private double[] initializeNoiseField(double[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
		if(var1 == null) {
			var1 = new double[var5 * var6 * var7];
		}

		if(this.field_35388_l == null) {
			this.field_35388_l = new float[25];

			for(int var8 = -2; var8 <= 2; ++var8) {
				for(int var9 = -2; var9 <= 2; ++var9) {
					float var10 = 10.0F / MathHelper.sqrt_float((float)(var8 * var8 + var9 * var9) + 0.2F);
					this.field_35388_l[var8 + 2 + (var9 + 2) * 5] = var10;
				}
			}
		}

		double var43 = 684.412D;
		double var44 = 684.412D;
		this.noise5 = this.noiseGen5.func_4109_a(this.noise5, var2, var4, var5, var7, 1.121D, 1.121D, 0.5D);
		this.noise6 = this.noiseGen6.func_4109_a(this.noise6, var2, var4, var5, var7, 200.0D, 200.0D, 0.5D);
		this.noise3 = this.noiseGen3.generateNoiseOctaves(this.noise3, var2, var3, var4, var5, var6, var7, var43 / 80.0D, var44 / 160.0D, var43 / 80.0D);
		this.noise1 = this.noiseGen1.generateNoiseOctaves(this.noise1, var2, var3, var4, var5, var6, var7, var43, var44, var43);
		this.noise2 = this.noiseGen2.generateNoiseOctaves(this.noise2, var2, var3, var4, var5, var6, var7, var43, var44, var43);
		boolean var42 = false;
		int var12 = 0;
		int var13 = 0;

		for(int var14 = 0; var14 < var5; ++var14) {
			for(int var15 = 0; var15 < var7; ++var15) {
				float var16 = 0.0F;
				float var17 = 0.0F;
				float var18 = 0.0F;
				byte var19 = 2;
				BiomeGenBase var20 = this.biomesForGeneration[var14 + 2 + (var15 + 2) * (var5 + 5)];

				for(int var21 = -var19; var21 <= var19; ++var21) {
					for(int var22 = -var19; var22 <= var19; ++var22) {
						BiomeGenBase var23 = this.biomesForGeneration[var14 + var21 + 2 + (var15 + var22 + 2) * (var5 + 5)];
						float var24 = this.field_35388_l[var21 + 2 + (var22 + 2) * 5] / (var23.minHeight + 2.0F);
						if(var23.minHeight > var20.minHeight) {
							var24 /= 2.0F;
						}

						var16 += var23.maxHeight * var24;
						var17 += var23.minHeight * var24;
						var18 += var24;
					}
				}

				var16 /= var18;
				var17 /= var18;
				var16 = var16 * 0.9F + 0.1F;
				var17 = (var17 * 4.0F - 1.0F) / 8.0F;
				double var45 = this.noise6[var13] / 8000.0D;
				if(var45 < 0.0D) {
					var45 = -var45 * 0.3D;
				}

				var45 = var45 * 3.0D - 2.0D;
				if(var45 < 0.0D) {
					var45 /= 2.0D;
					if(var45 < -1.0D) {
						var45 = -1.0D;
					}

					var45 /= 1.4D;
					var45 /= 2.0D;
				} else {
					if(var45 > 1.0D) {
						var45 = 1.0D;
					}

					var45 /= 8.0D;
				}

				++var13;

				for(int var46 = 0; var46 < var6; ++var46) {
					double var47 = (double)var17;
					double var26 = (double)var16;
					var47 += var45 * 0.2D;
					var47 = var47 * (double)var6 / 16.0D;
					double var28 = (double)var6 / 2.0D + var47 * 4.0D;
					double var30 = 0.0D;
					double var32 = ((double)var46 - var28) * 12.0D * 128.0D / (double)this.worldObj.worldHeight / var26;
					if(var32 < 0.0D) {
						var32 *= 4.0D;
					}

					double var34 = this.noise1[var12] / 512.0D;
					double var36 = this.noise2[var12] / 512.0D;
					double var38 = (this.noise3[var12] / 10.0D + 1.0D) / 2.0D;
					if(var38 < 0.0D) {
						var30 = var34;
					} else if(var38 > 1.0D) {
						var30 = var36;
					} else {
						var30 = var34 + (var36 - var34) * var38;
					}

					var30 -= var32;
					if(var46 > var6 - 4) {
						double var40 = (double)((float)(var46 - (var6 - 4)) / 3.0F);
						var30 = var30 * (1.0D - var40) + -10.0D * var40;
					}

					var1[var12] = var30;
					++var12;
				}
			}
		}

		return var1;
	}

	public boolean chunkExists(int var1, int var2) {
		return true;
	}

	public void populate(IChunkProvider var1, int var2, int var3) {
		BlockSand.fallInstantly = true;
		int var4 = var2 * 16;
		int var5 = var3 * 16;
		BiomeGenBase var6 = this.worldObj.getWorldChunkManager().getBiomeGenAt(var4 + 16, var5 + 16);
		this.rand.setSeed(this.worldObj.getWorldSeed());
		long var7 = this.rand.nextLong() / 2L * 2L + 1L;
		long var9 = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed((long)var2 * var7 + (long)var3 * var9 ^ this.worldObj.getWorldSeed());
		boolean var11 = false;
		if(this.mapFeaturesEnabled) {
			this.mineshaftGenerator.generateStructuresInChunk(this.worldObj, this.rand, var2, var3);
			var11 = this.villageGenerator.generateStructuresInChunk(this.worldObj, this.rand, var2, var3);
			this.strongholdGenerator.generateStructuresInChunk(this.worldObj, this.rand, var2, var3);
		}

		int var12;
		int var13;
		int var14;
		if(!var11 && this.rand.nextInt(4) == 0) {
			var12 = var4 + this.rand.nextInt(16) + 8;
			var13 = this.rand.nextInt(this.worldObj.worldHeight);
			var14 = var5 + this.rand.nextInt(16) + 8;
			(new WorldGenLakes(Block.waterStill.blockID)).generate(this.worldObj, this.rand, var12, var13, var14);
		}

		if(!var11 && this.rand.nextInt(8) == 0) {
			var12 = var4 + this.rand.nextInt(16) + 8;
			var13 = this.rand.nextInt(this.rand.nextInt(this.worldObj.worldHeight - 8) + 8);
			var14 = var5 + this.rand.nextInt(16) + 8;
			if(var13 < this.worldObj.seaLevel || this.rand.nextInt(10) == 0) {
				(new WorldGenLakes(Block.lavaStill.blockID)).generate(this.worldObj, this.rand, var12, var13, var14);
			}
		}

		for(var12 = 0; var12 < 8; ++var12) {
			var13 = var4 + this.rand.nextInt(16) + 8;
			var14 = this.rand.nextInt(this.worldObj.worldHeight);
			int var15 = var5 + this.rand.nextInt(16) + 8;
			if((new WorldGenDungeons()).generate(this.worldObj, this.rand, var13, var14, var15)) {
			}
		}

		// Horror mod: Generate abandoned structures (player presence signs)
		if(this.rand.nextInt(20) == 0) {
			int structX = var4 + this.rand.nextInt(16) + 8;
			int structY = this.rand.nextInt(50); // Only below y=50
			int structZ = var5 + this.rand.nextInt(16) + 8;
			(new WorldGenAbandonedStructures()).generate(this.worldObj, this.rand, structX, structY, structZ);
		}

		// Horror mod: Generate Error404 house (very rare - 1 per 500 chunks)
		if(this.rand.nextInt(500) == 0) {
			int houseX = var4 + this.rand.nextInt(16) + 8;
			int houseY = 64; // Surface level
			int houseZ = var5 + this.rand.nextInt(16) + 8;
			(new WorldGenError404House()).generate(this.worldObj, this.rand, houseX, houseY, houseZ);
		}

		var6.func_35477_a(this.worldObj, this.rand, var4, var5);
		SpawnerAnimals.func_35957_a(this.worldObj, var6, var4 + 8, var5 + 8, 16, 16, this.rand);
		var4 += 8;
		var5 += 8;

		for(var12 = 0; var12 < 16; ++var12) {
			for(var13 = 0; var13 < 16; ++var13) {
				var14 = this.worldObj.getPrecipitationHeight(var4 + var12, var5 + var13);
				if(this.worldObj.func_40471_p(var12 + var4, var14 - 1, var13 + var5)) {
					this.worldObj.setBlockWithNotify(var12 + var4, var14 - 1, var13 + var5, Block.ice.blockID);
				}

				if(this.worldObj.canSnowAt(var12 + var4, var14, var13 + var5)) {
					this.worldObj.setBlockWithNotify(var12 + var4, var14, var13 + var5, Block.snow.blockID);
				}
			}
		}

		BlockSand.fallInstantly = false;
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
		return "RandomLevelSource";
	}

	public List func_40377_a(EnumCreatureType var1, int var2, int var3, int var4) {
		WorldChunkManager var5 = this.worldObj.getWorldChunkManager();
		if(var5 == null) {
			return null;
		} else {
			BiomeGenBase var6 = var5.getBiomeGenAtChunkCoord(new ChunkCoordIntPair(var2 >> 4, var4 >> 4));
			return var6 == null ? null : var6.getSpawnableList(var1);
		}
	}

	public ChunkPosition func_40376_a(World var1, String var2, int var3, int var4, int var5) {
		return "Stronghold".equals(var2) && this.strongholdGenerator != null ? this.strongholdGenerator.func_40484_a(var1, var3, var4, var5) : null;
	}
}
