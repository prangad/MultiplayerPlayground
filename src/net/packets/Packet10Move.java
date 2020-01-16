package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet10Move extends Packet{

	private String username;
	private double x, y, angle;
	private boolean respawn;
	
	public Packet10Move(byte[] data) {
		super(10);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.x = Double.parseDouble(dataArray[1]);
		this.y = Double.parseDouble(dataArray[2]);
		this.angle = Double.parseDouble(dataArray[3]);
		this.respawn = Boolean.parseBoolean(dataArray[4]);
	}
	
	public Packet10Move(String username, double x, double y, double angle, boolean respawn) {
		super(10);
		this.username = username;
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.respawn = respawn;
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
		return ("10" + this.username + "," + this.x + "," + this.y + "," + this.angle + "," + this.respawn).getBytes();
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public double getX()
	{
		return this.x;
	}
	
	public double getY()
	{
		return this.y;
	}
	
	public double getAngle()
	{
		return this.angle;
	}
	
	public boolean isRespawn()
	{
		return this.respawn;
	}
}
