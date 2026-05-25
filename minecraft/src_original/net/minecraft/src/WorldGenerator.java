package net.minecraft.src;

import java.util.Random;

public abstract class WorldGenerator {
	private final boolean field_41061_a;

	public WorldGenerator() {
		this.field_41061_a = false;
	}

	public WorldGenerator(boolean var1) {
		this.field_41061_a = var1;
	}

	public abstract boolean generate(World var1, Random var2, int var3, int var4, int var5);

	public void func_517_a(double var1, double var3, double var5) {
	}

	protected void func_41060_a(World var1, int var2, int var3, int var4, int var5, int var6) {
		if(this.field_41061_a) {
			var1.setBlockAndMetadataWithNotify(var2, var3, var4, var5, var6);
		} else {
			var1.setBlockAndMetadata(var2, var3, var4, var5, var6);
		}

	}
}
