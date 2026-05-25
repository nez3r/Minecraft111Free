package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTBase {
	public int intValue;

	public NBTTagInt(String var1) {
		super(var1);
	}

	public NBTTagInt(String var1, int var2) {
		super(var1);
		this.intValue = var2;
	}

	void writeTagContents(DataOutput var1) throws IOException {
		var1.writeInt(this.intValue);
	}

	void readTagContents(DataInput var1) throws IOException {
		this.intValue = var1.readInt();
	}

	public byte getType() {
		return (byte)3;
	}

	public String toString() {
		return "" + this.intValue;
	}

	public NBTBase cloneTag() {
		return new NBTTagInt(this.getKey(), this.intValue);
	}

	public boolean equals(Object var1) {
		if(super.equals(var1)) {
			NBTTagInt var2 = (NBTTagInt)var1;
			return this.intValue == var2.intValue;
		} else {
			return false;
		}
	}
}
