package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import assets.World;
import client.MainFrame;
import entities.PlayerMP;
import entities.Square;
import net.packets.Packet;
import net.packets.Packet.PacketTypes;
import net.packets.Packet00Connect;
import net.packets.Packet10Move;
import net.packets.Packet11Rotate;
import net.packets.Packet20Shoot;
import net.packets.Packet30Start;
import net.packets.Packet41UpdateSquare;
import net.packets.Packet42UpdatePlayer;
import net.packets.Packet99Disconnect;

public class GameServer extends Thread { 

	private DatagramSocket socket;
	private List<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();
	private World world;
	
	public GameServer(World world, String port)
	{
		try
		{
			this.socket = new DatagramSocket(Integer.parseInt(port));
		}
		catch (NumberFormatException ex)
		{
			//REDUNDANCY WOOOO!
			try {
				this.socket = new DatagramSocket(1331);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		
		this.world = world;
	}
	
	public void run()
	{
		while (true)
		{
			byte[] data = new byte[Integer.parseInt(MainFrame.cfg.getProperty("server-max-buffer-mb"))];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try
			{
				socket.receive(packet);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}
	
	private void parsePacket(byte[] data, InetAddress address, int port)
	{
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet;
		
		switch (type)
		{
		case INVALID:
			break;
		case CONNECT:
			packet = new Packet00Connect(data);
			System.out.println("[SERVER][CONNECT] " + ((Packet00Connect)packet).getUsername() + "(" + address.getHostAddress() + ":" + port + ") connected.");
			PlayerMP player = new PlayerMP(world, 100, 100, 6, ((Packet00Connect)packet).getUsername(), address, port);
			addConnection(player, ((Packet00Connect)packet));
			break;
		case DISCONNECT:
			packet = new Packet99Disconnect(data);
			System.out.println("[SERVER][DISCONNECTED] " + ((Packet99Disconnect)packet).getUsername() + "(" + address.getHostAddress() + ":" + port + ") disconnected.");
			removeConnection((Packet99Disconnect)packet);
			break;
		case MOVE:
			packet = new Packet10Move(data);
			this.handleMove(((Packet10Move)packet));
			break;
		case ROTATE:
			packet = new Packet11Rotate(data);
			packet.writeData(this);
			break;
		case SHOOT:
			packet = new Packet20Shoot(data);
			this.handleShoot(((Packet20Shoot)packet));
			break;
		case START:
			packet = new Packet30Start(data);
			PlayerMP p = getPlayerMP(packet.readData(data));
			System.out.println("[SERVER][START] " + p.username + "(" + p.ipAddress.getHostAddress() + ":" + p.port +") started the game.");
			
			world.populate();
			
			for (int i = 0; i < world.getEntities().size(); i++)
			{
				if (world.getEntities().get(i) instanceof Square)
				{
					Square s = (Square)world.getEntities().get(i);
					
					packet = new Packet41UpdateSquare(s.worldPos.x, s.worldPos.y, s.health, 25);
					packet.writeData(this);
				}
			}
			break;
		case UPDATEPLAYER:
			packet = new Packet42UpdatePlayer(data);
			packet.writeData(this);
			break;
		case UPDATESQUARE:
			packet = new Packet41UpdateSquare(data);
			packet.writeData(this);
		case UPDATEWORLD:
			for (int i = 0; i < world.getEntities().size(); i++)
			{
				if (world.getEntities().get(i) instanceof Square)
				{
					Square s = (Square)world.getEntities().get(i);
					packet = new Packet41UpdateSquare(s.worldPos.x, s.worldPos.y, s.health, s.square.width);
					packet.writeData(this);
				}
			}
			break;
		default:
			break;
		}
		
	}
	
	public void addConnection(PlayerMP player, Packet00Connect packet)
	{
		boolean alreadyConnected = false;
		for (PlayerMP p : this.connectedPlayers)
		{
			if (player.getUsername().equalsIgnoreCase(p.getUsername()))
			{
				if (p.ipAddress == null)
				{
					p.ipAddress = player.ipAddress;
				}
				if (p.port == -1)
				{
					p.port = player.port;
				}
				alreadyConnected = true;
			}
			else
			{
				//System.out.println("SENDING " + p.getUsername() + " to " + player.getUsername());
				sendData(new Packet00Connect(p.username,
						p.worldPos.x,
						p.worldPos.y,
						p.health,
						p.score,
						p.kills,
						p.playerColor.getRed(),p.playerColor.getGreen(),p.playerColor.getBlue()).getData(), player.ipAddress, player.port);
			}
		}
		
		for (PlayerMP p : this.connectedPlayers)
		{
			if (!(p.getUsername().equals(player.getUsername())))
			{
				System.out.println("SENDING " + packet.getUsername() + " to " + p.getUsername());
				sendData(packet.getData(), p.ipAddress, p.port);
			}
		}
		
		if (!alreadyConnected)
		{
			this.connectedPlayers.add(player);
		}
	}
	
	public void removeConnection(Packet99Disconnect packet)
	{
		this.connectedPlayers.remove(getPlayerMPIndex(packet.getUsername()));
		
		packet.writeData(this);
	}
	
	public PlayerMP getPlayerMP(String username)
	{
		for (PlayerMP p : this.connectedPlayers)
		{
			if (p.getUsername().equals(username))
				return p;
		}
		return null;
	}
	
	public int getPlayerMPIndex(String username)
	{
		int index = 0;
		for (PlayerMP p : this.connectedPlayers)
		{
			if (p.getUsername().equals(username))
			{
				break;
			}
			index++;
		}
		return index;
	}
	
	public void sendData(byte[] data, InetAddress ipAddress, int port)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try
		{
			socket.send(packet);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void sendDataToAllClients(byte[] data) {
		for (PlayerMP p : connectedPlayers)
		{
			sendData(data, p.ipAddress, p.port);
		}
	}
	
	private void handleMove(Packet10Move packet)
	{
		if (getPlayerMP(packet.getUsername()) != null)
		{
			int index = getPlayerMPIndex(packet.getUsername());
			this.connectedPlayers.get(index).worldPos.x = packet.getX();
			this.connectedPlayers.get(index).worldPos.y = packet.getY();
			this.connectedPlayers.get(index).worldAngle = packet.getAngle();
			packet.writeData(this);
		}
	}
	
	private void handleShoot(Packet20Shoot packet)
	{
		if (getPlayerMP(packet.getUsername()) != null)
		{
			//int index = getPlayerMPIndex(packet.getUsername());
			//this.connectedPlayers.get(index).shoot(packet.getUsername(), new DVector(packet.getShootVecX(), packet.getShootVecY()));
			packet.writeData(this);
		}
	}
}
