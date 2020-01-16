package entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.net.InetAddress;

import assets.World;
import client.MainFrame;
import client.MainPanel;
import net.packets.Packet10Move;
import net.packets.Packet11Rotate;
import net.packets.Packet20Shoot;
import net.packets.Packet41UpdateSquare;
import net.packets.Packet42UpdatePlayer;
import util.DVector;

public class PlayerMP extends Player {
	public InetAddress ipAddress;
	public int port;
	public String username;
	public boolean localPlayer = false;
	
	private int WIDTH = Integer.parseInt(MainFrame.cfg.getProperty("window-width"));
	private int HEIGHT = Integer.parseInt(MainFrame.cfg.getProperty("window-height"));
	
	private long lastRotation = System.currentTimeMillis();
	private long lastMovement = System.currentTimeMillis();
	
	public PlayerMP(World world, double x, double y, int size, String username, InetAddress ipAddress, int port) {
		super(world, x, y, size);
		this.ipAddress = ipAddress;
		this.port = port;
		this.username = username;
		this.localPlayer = false;
	}
	
	public PlayerMP(World world, double x, double y, int size, String username, InetAddress ipAddress, int port, boolean localPlayer) {
		super(world, x, y, size);
		this.ipAddress = ipAddress;
		this.port = port;
		this.username = username;
		this.localPlayer = localPlayer;
	}
	
	public void update()
	{
		super.update();
		if (this.isMoving)
		{
			if (System.currentTimeMillis() - lastMovement >= Integer.parseInt(MainFrame.cfg.getProperty("max-client-movement-ms")))
			{
				Packet10Move packet = new Packet10Move(this.username, this.worldPos.getX(), this.worldPos.getY(), this.angle, false);
				packet.writeData(MainPanel.thisClient.socketClient);
			}
		}
		
		this.healthbar.update(this.worldPos.add(new DVector(0, 45)), this.health);
	}
	
	public void draw(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform oldTransform = g2d.getTransform();
		
		Font nameFont = new Font("Helvetica", Font.BOLD, 12);
		
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.setFont(nameFont);
		FontMetrics metrics = g2d.getFontMetrics(nameFont);
		
		if (this.localPlayer)
		{
			super.draw(g);
			healthbar.draw(g2d);
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.drawString(this.username,
					(int)(this.world.getPosition().getX() + this.worldPos.getX() - metrics.stringWidth(username)/2),
					(int)(this.world.getPosition().getY() + this.worldPos.getY() + 45));
		}
		else
		{	
			healthbar.draw(g2d);
			g2d.translate(this.world.getPosition().getX() + this.worldPos.getX(),
					this.world.getPosition().getY() + this.worldPos.getY());
			
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.drawString(this.username,
					(int)(0 - metrics.stringWidth(username)/2),
					(int)(0 + 45));
			
			g2d.rotate(this.worldAngle);
		
			g2d.setColor(this.playerColor);
			g2d.fillPolygon(xPoints, yPoints, 4);
		
			g2d.setColor(Color.WHITE);
			g2d.drawPolygon(xPoints, yPoints, 4);
		}
		
		g2d.setTransform(oldTransform);
		if (this.localPlayer)
		{
			nameFont = new Font("Helvetica", Font.PLAIN, 20);
			g2d.setFont(nameFont);
			metrics = g2d.getFontMetrics(nameFont);
		
			g2d.setColor(new Color(150, 150, 255));
			g2d.drawString("Kills: " + this.kills, WIDTH - metrics.stringWidth("Kills: " + this.kills) - 20, HEIGHT - 20);
			g2d.drawString("Score: " + this.score, 20, HEIGHT - 20);
		
			nameFont = new Font("Helvetica", Font.ITALIC, 16);
			g2d.setFont(nameFont);
			metrics = g2d.getFontMetrics(nameFont);
		
			if (outOfBounds)
			{	
				int timeRemaining = (int)(5 - (System.currentTimeMillis() - this.oobTimer)/1000);
			
				g2d.setColor(Color.RED);
				g2d.drawString("OUT OF BOUNDS", WIDTH/2 - metrics.stringWidth("OUT OF BOUNDS")/2, 25);
				if (timeRemaining > 0)
					g2d.drawString("You have " + timeRemaining + " second(s) to return to the game area.", WIDTH/2 - metrics.stringWidth("You have " + timeRemaining + " second(s) to return to the game area.")/2, 50);
				else
					g2d.drawString("Taking damage...", WIDTH/2 - metrics.stringWidth("Taking damage...")/2, 50);
			}
		}
	}
	
	public void takeDamage(int damage, PlayerMP player)
	{
		super.takeDamage(damage, player);
		
		if (this.health <= 0)
		{
			if (this.lastDamageDealer != null)
			{
				this.lastDamageDealer.killPlayer(this);
			}
			
			this.respawn();
		}
		
		Packet42UpdatePlayer packetU = new Packet42UpdatePlayer(this.username, this.health, this.kills, this.score,
				this.playerColor.getRed(),this.playerColor.getGreen(), this.playerColor.getBlue());
		packetU.writeData(MainPanel.thisClient.socketClient);
	}
	
	protected void updatePlayer()
	{
		Packet42UpdatePlayer packet = new Packet42UpdatePlayer(this.username, this.health, this.kills, this.score,
				this.playerColor.getRed(), this.playerColor.getGreen(), this.playerColor.getBlue());
		packet.writeData(MainPanel.thisClient.socketClient);
	}
	
	public void setHealth(int health)
	{
		this.health = health;
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public void destroySquare()
	{
		this.addScore((int)(Math.random() * 100) + 50);
		
		Packet42UpdatePlayer packetU = new Packet42UpdatePlayer(this.username, this.health, this.kills, this.score,
				this.playerColor.getRed(),this.playerColor.getGreen(), this.playerColor.getBlue());
		packetU.writeData(MainPanel.thisClient.socketClient);
		
		Packet41UpdateSquare packet = new Packet41UpdateSquare((int)(Math.random() * world.getWidth()) + 1,
				(int)(Math.random() * world.getHeight()) + 1,
				100,
				25);
		packet.writeData(MainPanel.thisClient.socketClient);
	}
	
	public void killPlayer(PlayerMP player)
	{
		super.addScore((player.score)/2 + (int)(Math.random() * 150) + 50);
		this.kills++;
		
		Packet42UpdatePlayer packetU = new Packet42UpdatePlayer(this.username, this.health, this.kills, this.score,
				this.playerColor.getRed(),this.playerColor.getGreen(), this.playerColor.getBlue());
		packetU.writeData(MainPanel.thisClient.socketClient);
	}
	
	private void respawn()
	{
		super.score = 0;
		super.kills = 0;
		super.vel = new DVector(0,0);
		super.worldPos = new DVector(Math.random() * world.getWidth() + 1, Math.random() * world.getHeight() + 1);
		super.health = 1000;
		super.isMoving = false;
		
		Packet10Move packetM = new Packet10Move(this.username, worldPos.x, worldPos.y, this.worldAngle, true);
		packetM.writeData(MainPanel.thisClient.socketClient);
	}
	
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e);
		if (System.currentTimeMillis() - lastRotation >= Integer.parseInt(MainFrame.cfg.getProperty("max-client-rotations-ms")))
		{
			lastRotation = System.currentTimeMillis();
			Packet11Rotate packet = new Packet11Rotate(this.username, this.angle);
			packet.writeData(MainPanel.thisClient.socketClient);
		}
	}
	
	public void mouseDragged(MouseEvent e)
	{
		super.mouseMoved(e);
		if (System.currentTimeMillis() - lastRotation >= Integer.parseInt(MainFrame.cfg.getProperty("max-client-rotations-ms")))
		{
			lastRotation = System.currentTimeMillis();
			Packet11Rotate packet = new Packet11Rotate(this.username, this.angle);
			packet.writeData(MainPanel.thisClient.socketClient);
		}
	}
	
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		performAttack();
	}
	
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		performAttack();
	}

	int fireFourRotation = 0;
	
	private void performAttack()
	{
		DVector shootVector = new DVector(Math.sin(this.angle), Math.cos(this.angle) * -1);
		shootVector = shootVector.mult(8);
		shootVector = shootVector.add(this.vel.div(2));
		Packet20Shoot packet;
		
		switch(super.fireMode)
		{
		case 0:
			packet = new Packet20Shoot(this.username, shootVector.x, shootVector.y);
			packet.writeData(MainPanel.thisClient.socketClient);
			break;
		case 1:
			for (int i = 0; i < 5; i++)
			{
				int angleOfRotation = -20 + (i * 10);
				DVector modifiedVector = shootVector.rotateDegrees(angleOfRotation);
				packet = new Packet20Shoot(this.username, modifiedVector.x, modifiedVector.y);
				packet.writeData(MainPanel.thisClient.socketClient);
			}
			break;
		case 2:
			if (this.username.equals("Death"))
			{
				for (int i = 0; i < 18; i++)
				{
					int angleOfRotation = (i * 20);
					DVector modifiedVector = shootVector.rotateDegrees(angleOfRotation);
					packet = new Packet20Shoot(this.username, modifiedVector.x, modifiedVector.y);
					packet.writeData(MainPanel.thisClient.socketClient);
				}
			}
			break;
		case 3:
			if (this.username.equals("Death"))
			{
				for (int i = 0; i < 36; i++)
				{
					int angleOfRotation = (i * 10);
					DVector modifiedVector = shootVector.rotateDegrees(angleOfRotation);
					packet = new Packet20Shoot(this.username, modifiedVector.x, modifiedVector.y);
					packet.writeData(MainPanel.thisClient.socketClient);
				}
			}
			break;
		case 4:
			if (this.username.equals("Death"))
			{
				DVector newVector = shootVector.rotateDegrees((Math.random() * 41) - 20);
				packet = new Packet20Shoot(this.username, newVector.x, newVector.y);
				packet.writeData(MainPanel.thisClient.socketClient);
			}
			break;
		case 5:
			if (this.username.equals("Death"))
			{
				DVector modifiedVector = shootVector.rotateDegrees(fireFourRotation);
				packet = new Packet20Shoot(this.username, modifiedVector.x, modifiedVector.y);
				packet.writeData(MainPanel.thisClient.socketClient);
				fireFourRotation += 9;
			}
			break;
		}
	}
	
}

//	public void mouseMoved(MouseEvent e){}
//	public void mouseDragged(MouseEvent e){}
//	public void keyPressed(KeyEvent e){}
//	public void keyReleased(KeyEvent e){}
//	public void keyTyped(KeyEvent e){}
//	public void mousePressed(MouseEvent e){}
//	public void mouseReleased(MouseEvent e){}
//	public void mouseClicked(MouseEvent e){}
//	public void mouseEntered(MouseEvent e){}
//	public void mouseExited(MouseEvent e){}
//	public void actionPerformed(ActionEvent e){}
