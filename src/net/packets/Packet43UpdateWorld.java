package net.packets;
import net.GameClient;
import net.GameServer;

public class Packet43UpdateWorld extends Packet{
	
	public Packet43UpdateWorld(byte[] data) {
		super(43);
	}
	
	public Packet43UpdateWorld() {
		super(43);
	}
	
	public void writeData(GameClient client)
	{
		client.sendData(getData());
	}
	
	public void writeData(GameServer server)
	{
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("43").getBytes();
	}
}
