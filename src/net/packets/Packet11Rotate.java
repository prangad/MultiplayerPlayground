package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet11Rotate extends Packet{

	private String username;
	private double angle;
	
	public Packet11Rotate(byte[] data) {
		super(11);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.angle = Double.parseDouble(dataArray[1]);
	}
	
	public Packet11Rotate(String username, double angle) {
		super(11);
		this.username = username;
		this.angle = angle;
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
		return ("11" + this.username + "," + this.angle).getBytes();
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public double getAngle()
	{
		return this.angle;
	}
}
