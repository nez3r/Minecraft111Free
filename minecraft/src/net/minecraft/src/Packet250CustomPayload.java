package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet250CustomPayload extends Packet {
	public String field_44012_a;
	public int field_44010_b;
	public byte[] field_44011_c;

	public void readPacketData(DataInputStream var1) throws IOException {
		this.field_44012_a = readString(var1, 16);
		this.field_44010_b = var1.readShort();
		if(this.field_44010_b > 0 && this.field_44010_b < Short.MAX_VALUE) {
			this.field_44011_c = new byte[this.field_44010_b];
			var1.read(this.field_44011_c);
		}

	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		writeString(this.field_44012_a, var1);
		var1.writeShort((short)this.field_44010_b);
		if(this.field_44011_c != null) {
			var1.write(this.field_44011_c);
		}

	}

	public void processPacket(NetHandler var1) {
		var1.func_44028_a(this);
	}

	public int getPacketSize() {
		return 2 + this.field_44012_a.length() * 2 + 2 + this.field_44010_b;
	}
}
