package net.packets;

import java.awt.Color;

import net.GameClient;
import net.GameServer;

public class Packet42UpdatePlayer extends Packet{

	private String username;
	private int health, kills, score;
	private Color color;
	
	public Packet42UpdatePlayer(byte[] data) {
		super(42);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.health = Integer.parseInt(dataArray[1]);
		this.kills = Integer.parseInt(dataArray[2]);
		this.score = Integer.parseInt(dataArray[3]);
		this.color = new Color(Integer.parseInt(dataArray[4]),
				Integer.parseInt(dataArray[5]),
				Integer.parseInt(dataArray[6]));
	}
	
	public Packet42UpdatePlayer(String username, int health, int kills, int score, int r, int g, int b) {
		super(42);
		this.username = username;
		this.health = health;
		this.kills = kills;
		this.score = score;
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
		return ("42" + this.username + "," + this.health + ","
				+ this.kills + "," + this.score + ","
				+ this.color.getRed() + "," + this.color.getGreen() + "," + this.color.getBlue()).getBytes();
	}
	
	public int getHealth()
	{
		return this.health;
	}
	
	public int getKills()
	{
		return this.kills;
	}
	
	public int getScore()
	{
		return this.score;
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	public String getUsername()
	{
		return this.username;
	}
}
