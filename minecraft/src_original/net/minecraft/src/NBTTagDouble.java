package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagDouble extends NBTBase {
	public double doubleValue;

	public NBTTagDouble(String var1) {
		super(var1);
	}

	public NBTTagDouble(String var1, double var2) {
		super(var1);
		this.doubleValue = var2;
	}

	void writeTagContents(DataOutput var1) throws IOException {
		var1.writeDouble(this.doubleValue);
	}

	void readTagContents(DataInput var1) throws IOException {
		this.doubleValue = var1.readDouble();
	}

	public byte getType() {
		return (byte)6;
	}

	public String toString() {
		return "" + this.doubleValue;
	}

	public NBTBase cloneTag() {
		return new NBTTagDouble(this.getKey(), this.doubleValue);
	}

	public boolean equals(Object var1) {
		if(super.equals(var1)) {
			NBTTagDouble var2 = (NBTTagDouble)var1;
			return this.doubleValue == var2.doubleValue;
		} else {
			return false;
		}
	}
}
