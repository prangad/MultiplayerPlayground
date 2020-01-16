package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet41UpdateSquare extends Packet{

	private double PosX, PosY;
	private int size, health;
	
	public Packet41UpdateSquare(byte[] data) {
		super(41);
		String[] dataArray = readData(data).split(",");
		this.PosX = Double.parseDouble(dataArray[0]);
		this.PosY = Double.parseDouble(dataArray[1]);
		this.health = Integer.parseInt(dataArray[2]);
		this.size = Integer.parseInt(dataArray[3]);
	}
	
	public Packet41UpdateSquare(double PosX, double PosY, int health, int size) {
		super(41);
		this.PosX = PosX;
		this.PosY = PosY;
		this.health = health;
		this.size = size;
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
		return ("41" + this.PosX + "," + this.PosY + "," + this.health + "," + this.size).getBytes();
	}
	
	
	public double getPosX()
	{
		return this.PosX;
	}
	
	public double getPosY()
	{
		return this.PosY;
	}
	
	public int getHealth()
	{
		return this.health;
	}
	
	public int getSize()
	{
		return this.size;
	}
	
}
