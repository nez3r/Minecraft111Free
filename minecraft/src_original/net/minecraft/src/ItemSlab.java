package net.minecraft.src;

public class ItemSlab extends ItemBlock {
	public ItemSlab(int var1) {
		super(var1);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public int getIconFromDamage(int var1) {
		return Block.stairSingle.getBlockTextureFromSideAndMetadata(2, var1);
	}

	public int getMetadata(int var1) {
		return var1;
	}

	public String getItemNameIS(ItemStack var1) {
		int var2 = var1.getItemDamage();
		if(var2 < 0 || var2 >= BlockStep.blockStepTypes.length) {
			var2 = 0;
		}

		return super.getItemName() + "." + BlockStep.blockStepTypes[var2];
	}

	public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7) {
		if(var7 != 1) {
		}

		if(var1.stackSize == 0) {
			return false;
		} else if(!var2.canPlayerEdit(var4, var5, var6)) {
			return false;
		} else {
			int var8 = var3.getBlockId(var4, var5, var6);
			int var9 = var3.getBlockMetadata(var4, var5, var6);
			if(var7 == 1 && var8 == Block.stairSingle.blockID && var9 == var1.getItemDamage()) {
				if(var3.setBlockAndMetadataWithNotify(var4, var5, var6, Block.stairDouble.blockID, var9)) {
					var3.playSoundEffect((double)((float)var4 + 0.5F), (double)((float)var5 + 0.5F), (double)((float)var6 + 0.5F), Block.stairDouble.stepSound.stepSoundDir2(), (Block.stairDouble.stepSound.getVolume() + 1.0F) / 2.0F, Block.stairDouble.stepSound.getPitch() * 0.8F);
					--var1.stackSize;
				}

				return true;
			} else {
				return super.onItemUse(var1, var2, var3, var4, var5, var6, var7);
			}
		}
	}
}
