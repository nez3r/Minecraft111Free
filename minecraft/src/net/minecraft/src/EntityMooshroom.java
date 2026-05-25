package net.minecraft.src;

public class EntityMooshroom extends EntityCow {
	public EntityMooshroom(World var1) {
		super(var1);
		this.texture = "/mob/redcow.png";
		this.setSize(0.9F, 1.3F);
	}

	public boolean interact(EntityPlayer var1) {
		ItemStack var2 = var1.inventory.getCurrentItem();
		if(var2 != null && var2.itemID == Item.bowlEmpty.shiftedIndex && this.getDelay() >= 0) {
			var1.inventory.setInventorySlotContents(var1.inventory.currentItem, new ItemStack(Item.bowlSoup));
			return true;
		} else if(var2 != null && var2.itemID == Item.shears.shiftedIndex && this.getDelay() >= 0) {
			this.setEntityDead();
			EntityCow var3 = new EntityCow(this.worldObj);
			var3.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			var3.setEntityHealth(this.getEntityHealth());
			var3.renderYawOffset = this.renderYawOffset;
			this.worldObj.spawnEntityInWorld(var3);
			this.worldObj.spawnParticle("largeexplode", this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 0.0D, 0.0D, 0.0D);

			for(int var4 = 0; var4 < 5; ++var4) {
				this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY + (double)this.height, this.posZ, new ItemStack(Block.mushroomRed)));
			}

			return true;
		} else {
			return super.interact(var1);
		}
	}

	protected EntityAnimal spawnBabyAnimal(EntityAnimal var1) {
		return new EntityMooshroom(this.worldObj);
	}
}
