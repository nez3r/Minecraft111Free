package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

public class ComponentStrongholdStairs2 extends ComponentStrongholdStairs {
	public StructureStrongholdPieceWeight field_35038_a;
	public ComponentStrongholdPortalRoom field_40009_b;
	public ArrayList field_35037_b = new ArrayList();

	public ComponentStrongholdStairs2(int var1, Random var2, int var3, int var4) {
		super(0, var2, var3, var4);
	}

	public ChunkPosition func_40008_a_() {
		return this.field_40009_b != null ? this.field_40009_b.func_40008_a_() : super.func_40008_a_();
	}
}
