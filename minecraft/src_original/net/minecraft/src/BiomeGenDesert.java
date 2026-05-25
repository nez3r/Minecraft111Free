package net.minecraft.src;

public class BiomeGenDesert extends BiomeGenBase {
	public BiomeGenDesert(int var1) {
		super(var1);
		this.spawnableCreatureList.clear();
		this.topBlock = (byte)Block.sand.blockID;
		this.fillerBlock = (byte)Block.sand.blockID;
		this.biomeDecorator.treesPerChunk = -999;
		this.biomeDecorator.deadBushPerChunk = 2;
		this.biomeDecorator.reedsPerChunk = 50;
		this.biomeDecorator.cactiPerChunk = 10;
	}
}
