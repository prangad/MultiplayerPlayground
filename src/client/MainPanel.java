package client;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import assets.World;
import entities.PlayerMP;
import net.GameClient;
import net.GameServer;
import net.packets.Packet00Connect;
import net.packets.Packet43UpdateWorld;
import net.packets.Packet99Disconnect;
import util.Config;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	//Instance Variables
	public static MainPanel thisClient;

	private final int FRAMERATE = Integer.parseInt(Config.cfg.getProperty("max-frame-rate"));
	private final int WORLD_REFRESH_RATE = Integer.parseInt(Config.cfg.getProperty("world-refresh-rate-ms"));

	public String username = null;
	
	private World world;
	private PlayerMP localPlayer;
	
	public GameClient socketClient;
	public GameServer socketServer;
	
	//Timers
	Timer animationTimer = new Timer((int)(1000/FRAMERATE), new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			world.update(localPlayer);
			repaint();
		}
	});
	
	Timer worldRefreshTimer = new Timer(WORLD_REFRESH_RATE, new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			Packet43UpdateWorld packet = new Packet43UpdateWorld();
			packet.writeData(socketClient);
		}
	});
	
	//Constructors
	public MainPanel()
	{
		thisClient = this;
		
		this.setBackground(Color.DARK_GRAY);
		
		String inputName = JOptionPane.showInputDialog(this, "Please enter a username.");
		if (inputName.length() > 20)
		{
			this.username = inputName.substring(0, 21);
		}
		else
		{
			this.username = inputName;
		}
		this.world = new World(2000, 2000);
		
		if (JOptionPane.showConfirmDialog(this, "Would you like to host the server?") == 0)
		{
			String port = JOptionPane.showInputDialog("What port would you like to host the server on?\nDefault: 1331");
			socketServer = new GameServer(world, port);
			socketServer.start();
			socketClient = new GameClient(this.world, "localhost");
			socketClient.start();
		}
		else
		{
			String serverAddress = JOptionPane.showInputDialog("Where would you like to connect? (i.e. 127.0.0.1:1331)\nDefault Port: 1331");
			socketClient = new GameClient(this.world, serverAddress);
			socketClient.start();
		}
		
		localPlayer = new PlayerMP(world, (int)(Math.random() * world.getWidth()) + 1, (int)(Math.random() * world.getHeight()) + 1, 6, username, null, -1, true);
		world.addEntity(localPlayer);
		
		Packet00Connect connectPacket = new Packet00Connect(localPlayer.username,
				localPlayer.worldPos.x,
				localPlayer.worldPos.y,
				localPlayer.health,
				localPlayer.score,
				localPlayer.kills,
				localPlayer.playerColor.getRed(),localPlayer.playerColor.getGreen(),localPlayer.playerColor.getBlue());
		if (socketServer != null)
		{
			socketServer.addConnection(localPlayer, connectPacket);
		}
		connectPacket.writeData(socketClient);
			
		while (world.getPlayerMP(username) == null){}
		this.localPlayer = world.getPlayerMP(username);
		
		this.addMouseMotionListener(this.localPlayer);
		this.addKeyListener(this.localPlayer);
		this.addMouseListener(this.localPlayer);
		
		animationTimer.start();
		if (socketServer != null)
		{
			worldRefreshTimer.start();
		}
	}
	
	//Methods
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		//g.setColor(Color.GREEN);
		//g.drawLine(0, HEIGHT/2, WIDTH, HEIGHT/2);
		//g.drawLine(WIDTH/2, 0, WIDTH/2, HEIGHT);
		
		world.draw(g);
	}
	
	public void disconnect()
	{
		Packet99Disconnect packet = new Packet99Disconnect(localPlayer.getUsername());
		packet.writeData(socketClient);
	}
	
}
