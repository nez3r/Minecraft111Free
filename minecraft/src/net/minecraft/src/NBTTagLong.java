package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTBase {
	public long longValue;

	public NBTTagLong(String var1) {
		super(var1);
	}

	public NBTTagLong(String var1, long var2) {
		super(var1);
		this.longValue = var2;
	}

	void writeTagContents(DataOutput var1) throws IOException {
		var1.writeLong(this.longValue);
	}

	void readTagContents(DataInput var1) throws IOException {
		this.longValue = var1.readLong();
	}

	public byte getType() {
		return (byte)4;
	}

	public String toString() {
		return "" + this.longValue;
	}

	public NBTBase cloneTag() {
		return new NBTTagLong(this.getKey(), this.longValue);
	}

	public boolean equals(Object var1) {
		if(super.equals(var1)) {
			NBTTagLong var2 = (NBTTagLong)var1;
			return this.longValue == var2.longValue;
		} else {
			return false;
		}
	}
}
