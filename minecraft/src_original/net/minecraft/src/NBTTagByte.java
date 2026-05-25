package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase {
	public byte byteValue;

	public NBTTagByte(String var1) {
		super(var1);
	}

	public NBTTagByte(String var1, byte var2) {
		super(var1);
		this.byteValue = var2;
	}

	void writeTagContents(DataOutput var1) throws IOException {
		var1.writeByte(this.byteValue);
	}

	void readTagContents(DataInput var1) throws IOException {
		this.byteValue = var1.readByte();
	}

	public byte getType() {
		return (byte)1;
	}

	public String toString() {
		return "" + this.byteValue;
	}

	public boolean equals(Object var1) {
		if(super.equals(var1)) {
			NBTTagByte var2 = (NBTTagByte)var1;
			return this.byteValue == var2.byteValue;
		} else {
			return false;
		}
	}

	public NBTBase cloneTag() {
		return new NBTTagByte(this.getKey(), this.byteValue);
	}
}
