package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet99Disconnect extends Packet{

	private String username;
	
	public Packet99Disconnect(byte[] data) {
		super(99);
		this.username = readData(data);
	}
	
	public Packet99Disconnect(String username) {
		super(99);
		this.username = username;
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
		return ("99" + this.username).getBytes();
	}
	
	public String getUsername()
	{
		return this.username;
	}
}
