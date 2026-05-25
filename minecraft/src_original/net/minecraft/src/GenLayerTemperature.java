package net.minecraft.src;

public class GenLayerTemperature extends GenLayer {
	public GenLayerTemperature(GenLayer var1) {
		super(0L);
		this.parent = var1;
	}

	public int[] getInts(int var1, int var2, int var3, int var4) {
		int[] var5 = this.parent.getInts(var1, var2, var3, var4);
		int[] var6 = IntCache.getIntCache(var3 * var4);

		for(int var7 = 0; var7 < var3 * var4; ++var7) {
			var6[var7] = BiomeGenBase.biomeList[var5[var7]].getIntTemperature();
		}

		return var6;
	}
}
