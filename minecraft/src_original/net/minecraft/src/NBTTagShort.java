package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTBase {
	public short shortValue;

	public NBTTagShort(String var1) {
		super(var1);
	}

	public NBTTagShort(String var1, short var2) {
		super(var1);
		this.shortValue = var2;
	}

	void writeTagContents(DataOutput var1) throws IOException {
		var1.writeShort(this.shortValue);
	}

	void readTagContents(DataInput var1) throws IOException {
		this.shortValue = var1.readShort();
	}

	public byte getType() {
		return (byte)2;
	}

	public String toString() {
		return "" + this.shortValue;
	}

	public NBTBase cloneTag() {
		return new NBTTagShort(this.getKey(), this.shortValue);
	}

	public boolean equals(Object var1) {
		if(super.equals(var1)) {
			NBTTagShort var2 = (NBTTagShort)var1;
			return this.shortValue == var2.shortValue;
		} else {
			return false;
		}
	}
}
