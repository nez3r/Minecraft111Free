package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase {
	public String stringValue;

	public NBTTagString(String var1) {
		super(var1);
	}

	public NBTTagString(String var1, String var2) {
		super(var1);
		this.stringValue = var2;
		if(var2 == null) {
			throw new IllegalArgumentException("Empty string not allowed");
		}
	}

	void writeTagContents(DataOutput var1) throws IOException {
		var1.writeUTF(this.stringValue);
	}

	void readTagContents(DataInput var1) throws IOException {
		this.stringValue = var1.readUTF();
	}

	public byte getType() {
		return (byte)8;
	}

	public String toString() {
		return "" + this.stringValue;
	}

	public NBTBase cloneTag() {
		return new NBTTagString(this.getKey(), this.stringValue);
	}

	public boolean equals(Object var1) {
		if(!super.equals(var1)) {
			return false;
		} else {
			NBTTagString var2 = (NBTTagString)var1;
			return this.stringValue == null && var2.stringValue == null || this.stringValue != null && this.stringValue.equals(var2.stringValue);
		}
	}
}
