package net.minecraft.src;

class SlotEnchantment extends Slot {
	final ContainerEnchantment field_40443_a;

	SlotEnchantment(ContainerEnchantment var1, IInventory var2, int var3, int var4, int var5) {
		super(var2, var3, var4, var5);
		this.field_40443_a = var1;
	}

	public boolean isItemValid(ItemStack var1) {
		return true;
	}
}
