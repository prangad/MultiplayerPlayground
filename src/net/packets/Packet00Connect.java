package net.packets;

import java.awt.Color;

import net.GameClient;
import net.GameServer;
import util.DVector;

public class Packet00Connect extends Packet{

	private String username;
	private DVector pos;
	private int health, score, kills;
	private Color color;
	
	
	public Packet00Connect(byte[] data) {
		super(00);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.pos = new DVector(Double.parseDouble(dataArray[1]), Double.parseDouble(dataArray[2]));
		this.health = Integer.parseInt(dataArray[3]);
		this.score = Integer.parseInt(dataArray[4]);
		this.kills = Integer.parseInt(dataArray[5]);
		this.color = new Color(Integer.parseInt(dataArray[6]), Integer.parseInt(dataArray[7]), Integer.parseInt(dataArray[8]));
		
	}
	
	public Packet00Connect(String username, double x, double y, int health, int score, int kills, int r, int g, int b) {
		super(00);
		this.username = username;
		this.pos = new DVector(x, y);
		this.health = health;
		this.score = score;
		this.kills = kills;
		this.color = new Color(r, g, b);
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
		return ("00" + this.username + "," + this.pos.x + "," + this.pos.y + "," +
				this.health + "," + this.score + "," + this.kills + "," +
				this.color.getRed() + "," + this.color.getGreen() + "," + this.color.getBlue()).getBytes();
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public double getX()
	{
		return this.pos.x;
	}
	
	public double getY()
	{
		return this.pos.y;
	}
	
	public int getHealth()
	{
		return this.health;
	}
	
	public int getScore()
	{
		return this.score;
	}
	
	public int getKills()
	{
		return this.kills;
	}
	
	public Color getColor()
	{
		return this.color;
	}
}
