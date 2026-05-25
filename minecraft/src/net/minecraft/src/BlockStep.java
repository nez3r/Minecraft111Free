package net.minecraft.src;

import java.util.Random;

public class BlockStep extends Block {
	public static final String[] blockStepTypes = new String[]{"stone", "sand", "wood", "cobble", "brick", "smoothStoneBrick"};
	private boolean blockType;

	public BlockStep(int var1, boolean var2) {
		super(var1, 6, Material.rock);
		this.blockType = var2;
		if(!var2) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		} else {
			opaqueCubeLookup[var1] = true;
		}

		this.setLightOpacity(255);
	}

	public int getBlockTextureFromSideAndMetadata(int var1, int var2) {
		return var2 == 0 ? (var1 <= 1 ? 6 : 5) : (var2 == 1 ? (var1 == 0 ? 208 : (var1 == 1 ? 176 : 192)) : (var2 == 2 ? 4 : (var2 == 3 ? 16 : (var2 == 4 ? Block.brick.blockIndexInTexture : (var2 == 5 ? Block.stoneBrick.blockIndexInTexture : 6)))));
	}

	public int getBlockTextureFromSide(int var1) {
		return this.getBlockTextureFromSideAndMetadata(var1, 0);
	}

	public boolean isOpaqueCube() {
		return this.blockType;
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
	}

	public int idDropped(int var1, Random var2, int var3) {
		return Block.stairSingle.blockID;
	}

	public int quantityDropped(Random var1) {
		return this.blockType ? 2 : 1;
	}

	protected int damageDropped(int var1) {
		return var1;
	}

	public boolean renderAsNormalBlock() {
		return this.blockType;
	}

	public boolean shouldSideBeRendered(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		if(this != Block.stairSingle) {
			super.shouldSideBeRendered(var1, var2, var3, var4, var5);
		}

		return var5 == 1 ? true : (!super.shouldSideBeRendered(var1, var2, var3, var4, var5) ? false : (var5 == 0 ? true : var1.getBlockId(var2, var3, var4) != this.blockID));
	}

	protected ItemStack createStackedBlock(int var1) {
		return new ItemStack(Block.stairSingle.blockID, 1, var1);
	}
}
