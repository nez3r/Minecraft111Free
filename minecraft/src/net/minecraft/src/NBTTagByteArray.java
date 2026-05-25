package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByteArray extends NBTBase {
	public byte[] byteArray;

	public NBTTagByteArray(String var1) {
		super(var1);
	}

	public NBTTagByteArray(String var1, byte[] var2) {
		super(var1);
		this.byteArray = var2;
	}

	void writeTagContents(DataOutput var1) throws IOException {
		var1.writeInt(this.byteArray.length);
		var1.write(this.byteArray);
	}

	void readTagContents(DataInput var1) throws IOException {
		int var2 = var1.readInt();
		this.byteArray = new byte[var2];
		var1.readFully(this.byteArray);
	}

	public byte getType() {
		return (byte)7;
	}

	public String toString() {
		return "[" + this.byteArray.length + " bytes]";
	}

	public boolean equals(Object var1) {
		if(!super.equals(var1)) {
			return false;
		} else {
			NBTTagByteArray var2 = (NBTTagByteArray)var1;
			return this.byteArray == null && var2.byteArray == null || this.byteArray != null && this.byteArray.equals(var2.byteArray);
		}
	}

	public NBTBase cloneTag() {
		byte[] var1 = new byte[this.byteArray.length];
		System.arraycopy(this.byteArray, 0, var1, 0, this.byteArray.length);
		return new NBTTagByteArray(this.getKey(), var1);
	}
}
