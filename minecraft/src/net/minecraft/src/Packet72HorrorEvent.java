package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Server-authoritative horror event notification.
 */
public class Packet72HorrorEvent extends Packet {
	public int eventId;
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int parameter;
	public int stage = -1;

	public Packet72HorrorEvent() {
	}

	static {
		try {
			Packet.addIdClassMapping(72, true, false, Packet72HorrorEvent.class);
		} catch (IllegalArgumentException alreadyRegistered) {
			// The client map registers this packet during Packet initialization.
		}
	}

	public Packet72HorrorEvent(int eventId, double x, double y, double z, int parameter) {
		this(eventId, x, y, z, parameter, -1);
	}

	public Packet72HorrorEvent(int eventId, double x, double y, double z, int parameter, int stage) {
		this.eventId = eventId;
		this.xPosition = MathHelper.floor_double(x * 32.0D);
		this.yPosition = MathHelper.floor_double(y * 32.0D);
		this.zPosition = MathHelper.floor_double(z * 32.0D);
		this.parameter = parameter;
		this.stage = stage;
	}

	public void readPacketData(DataInputStream input) throws IOException {
		this.eventId = input.readByte();
		this.xPosition = input.readInt();
		this.yPosition = input.readInt();
		this.zPosition = input.readInt();
		this.parameter = input.readInt();
		this.stage = input.readByte();
	}

	public void writePacketData(DataOutputStream output) throws IOException {
		output.writeByte(this.eventId);
		output.writeInt(this.xPosition);
		output.writeInt(this.yPosition);
		output.writeInt(this.zPosition);
		output.writeInt(this.parameter);
		output.writeByte(this.stage);
	}

	public void processPacket(NetHandler handler) {
		try {
			handler.getClass().getMethod("handleHorrorEvent", Packet72HorrorEvent.class)
					.invoke(handler, this);
		} catch (Exception exception) {
			handler.registerPacket(this);
		}
	}

	public int getPacketSize() {
		return 18;
	}
}
