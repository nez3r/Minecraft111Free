package net.minecraft.src;

public class PlayerCapabilities {
	public boolean disableDamage = false;
	public boolean isFlying = false;
	public boolean allowFlying = false;
	public boolean depleteBuckets = false;

	public void writeCapabilitiesToNBT(NBTTagCompound var1) {
		NBTTagCompound var2 = new NBTTagCompound();
		var2.setBoolean("invulnerable", this.disableDamage);
		var2.setBoolean("flying", this.disableDamage);
		var2.setBoolean("mayfly", this.allowFlying);
		var2.setBoolean("instabuild", this.depleteBuckets);
		var1.setTag("abilities", var2);
	}

	public void readCapabilitiesFromNBT(NBTTagCompound var1) {
		if(var1.hasKey("abilities")) {
			NBTTagCompound var2 = var1.getCompoundTag("abilities");
			this.disableDamage = var2.getBoolean("invulnerable");
			this.isFlying = var2.getBoolean("flying");
			this.allowFlying = var2.getBoolean("mayfly");
			this.depleteBuckets = var2.getBoolean("instabuild");
		}

	}
}
