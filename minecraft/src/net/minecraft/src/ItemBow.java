package net.minecraft.src;

public class ItemBow extends Item {
	public ItemBow(int var1) {
		super(var1);
		this.maxStackSize = 1;
		this.setMaxDamage(384);
	}

	public void onPlayerStoppedUsing(ItemStack var1, World var2, EntityPlayer var3, int var4) {
		boolean var5 = var3.capabilities.depleteBuckets || EnchantmentHelper.getEnchantmentLevel(Enchantment.field_46042_v.effectId, var1) > 0;
		if(var5 || var3.inventory.hasItem(Item.arrow.shiftedIndex)) {
			int var6 = this.getMaxItemUseDuration(var1) - var4;
			float var7 = (float)var6 / 20.0F;
			var7 = (var7 * var7 + var7 * 2.0F) / 3.0F;
			if((double)var7 < 0.1D) {
				return;
			}

			if(var7 > 1.0F) {
				var7 = 1.0F;
			}

			EntityArrow var8 = new EntityArrow(var2, var3, var7 * 2.0F);
			if(var7 == 1.0F) {
				var8.arrowCritical = true;
			}

			int var9 = EnchantmentHelper.getEnchantmentLevel(Enchantment.field_46045_s.effectId, var1);
			if(var9 > 0) {
				var8.func_46024_b(var8.func_46025_l() + (double)var9 * 0.5D + 0.5D);
			}

			int var10 = EnchantmentHelper.getEnchantmentLevel(Enchantment.field_46044_t.effectId, var1);
			if(var10 > 0) {
				var8.func_46023_b(var10);
			}

			if(EnchantmentHelper.getEnchantmentLevel(Enchantment.field_46043_u.effectId, var1) > 0) {
				var8.setFire(100);
			}

			var1.damageItem(1, var3);
			var2.playSoundAtEntity(var3, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + var7 * 0.5F);
			if(!var5) {
				var3.inventory.consumeInventoryItem(Item.arrow.shiftedIndex);
			} else {
				var8.doesArrowBelongToPlayer = false;
			}

			if(!var2.multiplayerWorld) {
				var2.spawnEntityInWorld(var8);
			}
		}

	}

	public ItemStack onFoodEaten(ItemStack var1, World var2, EntityPlayer var3) {
		return var1;
	}

	public int getMaxItemUseDuration(ItemStack var1) {
		return 72000;
	}

	public EnumAction getItemUseAction(ItemStack var1) {
		return EnumAction.bow;
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		if(var3.capabilities.depleteBuckets || var3.inventory.hasItem(Item.arrow.shiftedIndex)) {
			var3.setItemInUse(var1, this.getMaxItemUseDuration(var1));
		}

		return var1;
	}

	public int getItemEnchantability() {
		return 1;
	}
}
