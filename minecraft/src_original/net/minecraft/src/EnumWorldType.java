package net.minecraft.src;

public enum EnumWorldType {
	DEFAULT("default"),
	FLAT("flat");

	private String field_46139_c;

	private EnumWorldType(String var3) {
		this.field_46139_c = var3;
	}

	public String func_46136_a() {
		return "generator." + this.field_46139_c;
	}

	public static EnumWorldType func_46135_a(String var0) {
		EnumWorldType[] var1 = values();
		int var2 = var1.length;

		for(int var3 = 0; var3 < var2; ++var3) {
			EnumWorldType var4 = var1[var3];
			if(var4.name().equals(var0)) {
				return var4;
			}
		}

		return null;
	}
}
