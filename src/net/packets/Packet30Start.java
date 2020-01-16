package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet30Start extends Packet{
	
	private String username;
	
	public Packet30Start(byte[] data) {
		super(30);
		this.username = readData(data);
	}
	
	public Packet30Start(String username) {
		super(30);
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
		return ("30" + this.username).getBytes();
	}
	
	public String getUsername()
	{
		return this.username;
	}
}
