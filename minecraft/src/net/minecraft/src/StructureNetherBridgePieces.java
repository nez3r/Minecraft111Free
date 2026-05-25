package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class StructureNetherBridgePieces {
	private static final StructureNetherBridgePieceWeight[] field_40692_a = new StructureNetherBridgePieceWeight[]{new StructureNetherBridgePieceWeight(ComponentNetherBridgeStraight.class, 30, 0, true), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCrossing3.class, 10, 4), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCrossing.class, 10, 4), new StructureNetherBridgePieceWeight(ComponentNetherBridgeStairs.class, 10, 3), new StructureNetherBridgePieceWeight(ComponentNetherBridgeThrone.class, 5, 2), new StructureNetherBridgePieceWeight(ComponentNetherBridgeEntrance.class, 5, 1)};
	private static final StructureNetherBridgePieceWeight[] field_40691_b = new StructureNetherBridgePieceWeight[]{new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor5.class, 25, 0, true), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCrossing2.class, 15, 5), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor2.class, 5, 10), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor.class, 5, 10), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor3.class, 10, 3, true), new StructureNetherBridgePieceWeight(ComponentNetherBridgeCorridor4.class, 7, 2), new StructureNetherBridgePieceWeight(ComponentNetherBridgeNetherStalkRoom.class, 5, 2)};

	private static ComponentNetherBridgePiece func_40690_b(StructureNetherBridgePieceWeight var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7) {
		Class var8 = var0.field_40699_a;
		Object var9 = null;
		if(var8 == ComponentNetherBridgeStraight.class) {
			var9 = ComponentNetherBridgeStraight.func_40029_a(var1, var2, var3, var4, var5, var6, var7);
		} else if(var8 == ComponentNetherBridgeCrossing3.class) {
			var9 = ComponentNetherBridgeCrossing3.func_40033_a(var1, var2, var3, var4, var5, var6, var7);
		} else if(var8 == ComponentNetherBridgeCrossing.class) {
			var9 = ComponentNetherBridgeCrossing.func_40028_a(var1, var2, var3, var4, var5, var6, var7);
		} else if(var8 == ComponentNetherBridgeStairs.class) {
			var9 = ComponentNetherBridgeStairs.func_40031_a(var1, var2, var3, var4, var5, var6, var7);
		} else if(var8 == ComponentNetherBridgeThrone.class) {
			var9 = ComponentNetherBridgeThrone.func_40026_a(var1, var2, var3, var4, var5, var6, var7);
		} else if(var8 == ComponentNetherBridgeEntrance.class) {
			var9 = ComponentNetherBridgeEntrance.func_40030_a(var1, var2, var3, var4, var5, var6, var7);
		} else if(var8 == ComponentNetherBridgeCorridor5.class) {
			var9 = ComponentNetherBridgeCorridor5.func_40032_a(var1, var2, var3, var4, var5, var6, var7);
		} else if(var8 == ComponentNetherBridgeCorridor2.class) {
			var9 = ComponentNetherBridgeCorridor2.func_40041_a(var1, var2, var3, var4, var5, var6, var7);
		} else if(var8 == ComponentNetherBridgeCorridor.class) {
			var9 = ComponentNetherBridgeCorridor.func_40038_a(var1, var2, var3, var4, var5, var6, var7);
		} else if(var8 == ComponentNetherBridgeCorridor3.class) {
			var9 = ComponentNetherBridgeCorridor3.func_40042_a(var1, var2, var3, var4, var5, var6, var7);
		} else if(var8 == ComponentNetherBridgeCorridor4.class) {
			var9 = ComponentNetherBridgeCorridor4.func_40039_a(var1, var2, var3, var4, var5, var6, var7);
		} else if(var8 == ComponentNetherBridgeCrossing2.class) {
			var9 = ComponentNetherBridgeCrossing2.func_40025_a(var1, var2, var3, var4, var5, var6, var7);
		} else if(var8 == ComponentNetherBridgeNetherStalkRoom.class) {
			var9 = ComponentNetherBridgeNetherStalkRoom.func_40040_a(var1, var2, var3, var4, var5, var6, var7);
		}

		return (ComponentNetherBridgePiece)var9;
	}

	static ComponentNetherBridgePiece func_40688_a(StructureNetherBridgePieceWeight var0, List var1, Random var2, int var3, int var4, int var5, int var6, int var7) {
		return func_40690_b(var0, var1, var2, var3, var4, var5, var6, var7);
	}

	static StructureNetherBridgePieceWeight[] func_40689_a() {
		return field_40692_a;
	}

	static StructureNetherBridgePieceWeight[] func_40687_b() {
		return field_40691_b;
	}
}
