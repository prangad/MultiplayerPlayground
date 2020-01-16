package net.packets;

import net.GameClient;
import net.GameServer;

public abstract class Packet {

	public static enum PacketTypes
	{
		INVALID(-1),
		CONNECT(00),
		MOVE(10),
		ROTATE(11),
		SHOOT(20),
		START(30),
		UPDATE(40),
		UPDATESQUARE(41),
		UPDATEPLAYER(42),
		UPDATEWORLD(43),
		DISCONNECT(99);
		
		private int packetID;
		private PacketTypes(int packetID)
		{
			this.packetID = packetID;
		}
		
		public int getID()
		{
			return this.packetID;
		}
	}
	
	public byte packetID;
	
	public Packet(int packetID)
	{
		this.packetID = (byte)packetID;
	}
	
	public abstract void writeData(GameClient client);
	public abstract void writeData(GameServer server);
	public abstract byte[] getData();
	
	public String readData(byte[] data)
	{
		String message = new String(data).trim();
		return message.substring(2);
	}
	
	public static PacketTypes lookupPacket(String packetID)
	{
		try
		{
			return lookupPacket(Integer.parseInt(packetID));
		}
		catch (NumberFormatException ex)
		{
			return PacketTypes.INVALID;
		}
	}
	
	public static PacketTypes lookupPacket(int id)
	{
		for (PacketTypes p : PacketTypes.values())
		{
			if (p.getID() == id)
			{
				return p;
			}
		}
		return PacketTypes.INVALID;
	}
	
}
