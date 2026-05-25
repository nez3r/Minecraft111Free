package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet9Respawn extends Packet {
	public long mapSeed;
	public int respawnDimension;
	public int difficulty;
	public int worldHeight;
	public int creativeMode;
	public EnumWorldType field_46031_f;

	public Packet9Respawn() {
	}

	public Packet9Respawn(byte var1, byte var2, long var3, EnumWorldType var5, int var6, int var7) {
		this.respawnDimension = var1;
		this.difficulty = var2;
		this.mapSeed = var3;
		this.worldHeight = var6;
		this.creativeMode = var7;
		this.field_46031_f = var5;
	}

	public void processPacket(NetHandler var1) {
		var1.handleRespawn(this);
	}

	public void readPacketData(DataInputStream var1) throws IOException {
		this.respawnDimension = var1.readByte();
		this.difficulty = var1.readByte();
		this.creativeMode = var1.readByte();
		this.worldHeight = var1.readShort();
		this.mapSeed = var1.readLong();
		String var2 = readString(var1, 16);
		this.field_46031_f = EnumWorldType.func_46135_a(var2);
		if(this.field_46031_f == null) {
			this.field_46031_f = EnumWorldType.DEFAULT;
		}

	}

	public void writePacketData(DataOutputStream var1) throws IOException {
		var1.writeByte(this.respawnDimension);
		var1.writeByte(this.difficulty);
		var1.writeByte(this.creativeMode);
		var1.writeShort(this.worldHeight);
		var1.writeLong(this.mapSeed);
		writeString(this.field_46031_f.name(), var1);
	}

	public int getPacketSize() {
		return 13 + this.field_46031_f.name().length();
	}
}
