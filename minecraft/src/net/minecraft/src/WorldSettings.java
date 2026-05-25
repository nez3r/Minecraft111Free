package net.minecraft.src;

public final class WorldSettings {
	private final long seed;
	private final int gameType;
	private final boolean mapFeaturesEnabled;
	private final boolean hardcoreEnabled;
	private final EnumWorldType field_46108_e;

	public WorldSettings(long var1, int var3, boolean var4, boolean var5, EnumWorldType var6) {
		this.seed = var1;
		this.gameType = var3;
		this.mapFeaturesEnabled = var4;
		this.hardcoreEnabled = var5;
		this.field_46108_e = var6;
	}

	public long getSeed() {
		return this.seed;
	}

	public int getGameType() {
		return this.gameType;
	}

	public boolean getHardcoreEnabled() {
		return this.hardcoreEnabled;
	}

	public boolean isMapFeaturesEnabled() {
		return this.mapFeaturesEnabled;
	}

	public EnumWorldType func_46107_e() {
		return this.field_46108_e;
	}
}
