package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import assets.World;
import client.MainFrame;
import client.MainPanel;
import entities.PlayerMP;
import entities.Square;
import net.packets.Packet;
import net.packets.Packet.PacketTypes;
import net.packets.Packet00Connect;
import net.packets.Packet10Move;
import net.packets.Packet11Rotate;
import net.packets.Packet20Shoot;
import net.packets.Packet41UpdateSquare;
import net.packets.Packet42UpdatePlayer;
import net.packets.Packet99Disconnect;
import util.DVector;

public class GameClient extends Thread {
	
	private InetAddress serverAddress;
	private int port;
	private DatagramSocket socket;
	private World world;
	
	public GameClient(World world, String ipAddress)
	{
		try
		{
			String[] ipComponents = ipAddress.split(":");
			
			this.world = world;
		    this.socket = new DatagramSocket();
			this.serverAddress = InetAddress.getByName(ipComponents[0]);
			if (ipComponents.length > 1)
			{
				this.port = Integer.parseInt(ipComponents[1]);
			}
			else
			{
				this.port = 1331;
			}
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		while (true)
		{
			byte[] data = new byte[Integer.parseInt(MainFrame.cfg.getProperty("client-max-buffer-mb"))];
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
			System.out.println("[CONNECT] " + ((Packet00Connect)packet).getUsername() + " has joined the game.");
			PlayerMP player = null;
			
			Packet00Connect p = (Packet00Connect)packet;
			
			if (MainPanel.thisClient.socketServer != null)
			{
				player = new PlayerMP(world, p.getX(), p.getY(), 6, p.getUsername(), null, -1);
			}
			else
			{
				player = new PlayerMP(world, p.getX(), p.getY(), 6, p.getUsername(), address, port);
			}

			if (player != null)
			{
				player.health = p.getHealth();
				player.kills = p.getKills();
				player.score = p.getScore();
				player.playerColor = p.getColor();
				this.world.addEntity(player);
			}
			break;
		case DISCONNECT:
			packet = new Packet99Disconnect(data);
			System.out.println("[DISCONNECTED] " + ((Packet99Disconnect)packet).getUsername() + " has left the game.");
			world.removeEntity(((Packet99Disconnect)packet).getUsername());
			break;
		case MOVE:
			packet = new Packet10Move(data);
			handleMove((Packet10Move)packet);
			break;
		case SHOOT:
			packet = new Packet20Shoot(data);
			handleShoot((Packet20Shoot)packet);
			break;
		case UPDATESQUARE:
			packet = new Packet41UpdateSquare(data);
			handleUpdateSquare((Packet41UpdateSquare)packet);
			break;
		case ROTATE:
			packet = new Packet11Rotate(data);
			handleRotate((Packet11Rotate)packet);
			break;
		case UPDATEPLAYER:
			packet = new Packet42UpdatePlayer(data);
			handleUpdatePlayer((Packet42UpdatePlayer)packet);
			break;
		default:
			break;
		}
		
	}
	
	public void sendData(byte[] data)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, port);
		try
		{
			socket.send(packet);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleMove(Packet10Move packet)
	{
		if (!world.getPlayerMP(packet.getUsername()).localPlayer || packet.isRespawn())
		{
			world.movePlayer(packet.getUsername(), packet.getX(), packet.getY(), packet.getAngle());
		}
	}
	
	private void handleShoot(Packet20Shoot packet)
	{
		world.playerShoot(packet.getUsername(), new DVector(packet.getShootVecX(), packet.getShootVecY()));
	}
	
	private void handleUpdateSquare(Packet41UpdateSquare packet)
	{
		if (world.getSquare(new DVector(packet.getPosX(), packet.getPosY())) == null)
		{
			world.addEntity(new Square(world, new DVector(packet.getPosX(), packet.getPosY()), 25));
		}
		else
		{
			Square s = world.getSquare(new DVector(packet.getPosX(), packet.getPosY()));
			if (s != null)
			{
				s.health = packet.getHealth();
				s.serverUpdate();
			}
		}
	}
	
	private void handleRotate(Packet11Rotate packet)
	{
		PlayerMP player = world.getPlayerMP(packet.getUsername());
		
		if (player != null)
		{
			player.worldAngle = packet.getAngle();
		}
	}
	
	private void handleUpdatePlayer(Packet42UpdatePlayer packet)
	{
		PlayerMP player = world.getPlayerMP(packet.getUsername());
		
		if (player != null)
		{
			player.setHealth(packet.getHealth());
			player.kills = packet.getKills();
			player.score = packet.getScore();
			player.playerColor = packet.getColor();
		}
	}
}