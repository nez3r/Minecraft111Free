package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBTBase {
	public float floatValue;

	public NBTTagFloat(String var1) {
		super(var1);
	}

	public NBTTagFloat(String var1, float var2) {
		super(var1);
		this.floatValue = var2;
	}

	void writeTagContents(DataOutput var1) throws IOException {
		var1.writeFloat(this.floatValue);
	}

	void readTagContents(DataInput var1) throws IOException {
		this.floatValue = var1.readFloat();
	}

	public byte getType() {
		return (byte)5;
	}

	public String toString() {
		return "" + this.floatValue;
	}

	public NBTBase cloneTag() {
		return new NBTTagFloat(this.getKey(), this.floatValue);
	}

	public boolean equals(Object var1) {
		if(super.equals(var1)) {
			NBTTagFloat var2 = (NBTTagFloat)var1;
			return this.floatValue == var2.floatValue;
		} else {
			return false;
		}
	}
}
