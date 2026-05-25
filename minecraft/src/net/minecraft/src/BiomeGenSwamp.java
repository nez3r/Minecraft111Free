package net.minecraft.src;

import java.util.Random;

public class BiomeGenSwamp extends BiomeGenBase {
	protected BiomeGenSwamp(int var1) {
		super(var1);
		this.biomeDecorator.treesPerChunk = 2;
		this.biomeDecorator.flowersPerChunk = -999;
		this.biomeDecorator.deadBushPerChunk = 1;
		this.biomeDecorator.mushroomsPerChunk = 8;
		this.biomeDecorator.reedsPerChunk = 10;
		this.biomeDecorator.clayPerChunk = 1;
		this.biomeDecorator.waterlilyPerChunk = 4;
		this.waterColorMultiplier = 14745518;
	}

	public WorldGenerator getRandomWorldGenForTrees(Random var1) {
		return this.worldGenSwamp;
	}

	public int getGrassColorAtCoords(IBlockAccess var1, int var2, int var3, int var4) {
		double var5 = (double)var1.getWorldChunkManager().getTemperature(var2, var3, var4);
		double var7 = (double)var1.getWorldChunkManager().getRainfall(var2, var4);
		return ((ColorizerGrass.getGrassColor(var5, var7) & 16711422) + 5115470) / 2;
	}

	public int getFoliageColorAtCoords(IBlockAccess var1, int var2, int var3, int var4) {
		double var5 = (double)var1.getWorldChunkManager().getTemperature(var2, var3, var4);
		double var7 = (double)var1.getWorldChunkManager().getRainfall(var2, var4);
		return ((ColorizerFoliage.getFoliageColor(var5, var7) & 16711422) + 5115470) / 2;
	}
}
