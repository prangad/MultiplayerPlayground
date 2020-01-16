package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet20Shoot extends Packet{

	private String username;
	private double shootVecX, shootVecY;
	
	public Packet20Shoot(byte[] data) {
		super(20);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.shootVecX = Double.parseDouble(dataArray[1]);
		this.shootVecY = Double.parseDouble(dataArray[2]);
	}
	
	public Packet20Shoot(String username, double shootVecX, double shootVecY) {
		super(20);
		this.username = username;
		this.shootVecX = shootVecX;
		this.shootVecY = shootVecY;
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
		return ("20" + this.username + "," + this.shootVecX + "," + this.shootVecY).getBytes();
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public double getShootVecX()
	{
		return this.shootVecX;
	}
	
	public double getShootVecY()
	{
		return this.shootVecY;
	}
	
}
