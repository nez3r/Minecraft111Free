package net.minecraft.src;

public class EntityDamageSourceIndirect extends EntityDamageSource {
	private Entity damageSourceProjectile;

	public EntityDamageSourceIndirect(String var1, Entity var2, Entity var3) {
		super(var1, var2);
		this.damageSourceProjectile = var3;
	}

	public Entity getSourceOfDamage() {
		return this.damageSourceEntity;
	}

	public Entity getEntity() {
		return this.damageSourceProjectile;
	}
}
