package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import assets.Healthbar;
import assets.World;
import client.MainPanel;
import net.packets.Packet41UpdateSquare;
import net.packets.Packet42UpdatePlayer;
import util.Config;
import util.DVector;

public class Square extends Entity {
	
	private final int WORLD_REFRESH_RATE = Integer.parseInt(Config.cfg.getProperty("world-refresh-rate-ms"));
	
	//Instance Variables
	public Rectangle square;
	protected World world;
	public DVector worldPos;
	private double rotation;
	private boolean rotateDir;
	public int health;
	private int initialHealth;
	private long healthChange;
	Healthbar healthbar;
	public PlayerMP lastDamageDealer;
	private long lastServerUpdate = System.currentTimeMillis();

	public Square(World world, DVector pos, int size)
	{
		this.world = world;
		this.worldPos = pos;
		this.square = new Rectangle((int)this.worldPos.getX(), (int)this.worldPos.getY(), size, size);
		this.rotation = (Math.random() * 180);
		this.rotateDir = (Math.random() > .5);
		this.health = 100;
		this.initialHealth = this.health;
		this.healthbar = new Healthbar(this.worldPos, this.health, this.world);
		this.lastDamageDealer = null;
	}

	public void update()
	{
		this.healthbar.update(this.worldPos, this.health);
		
		if (this.rotateDir)
			this.rotation += .25;
		else
			this.rotation -= .25;
		
		if (this.health < this.initialHealth)
		{
			this.initialHealth = this.health;
			this.healthChange = System.currentTimeMillis();
		}
		else
		{
			this.initialHealth = this.health;
		}

		
		if (((System.currentTimeMillis() - this.healthChange) > 1000
				&& health < 100)
				&& (System.currentTimeMillis() - this.healthChange) % 10 == 0)
		{
			this.health++;
		}

		if (this.health <= 0)
		{
			respawn();
		}
		
		if (MainPanel.thisClient.socketServer == null)
		{	
			if ((System.currentTimeMillis() - this.lastServerUpdate) > WORLD_REFRESH_RATE + 1000)
			{
				System.out.println("[DESYNC] Removing Entity: " + this);
				world.removeEntity(this);
			}
		}
		
	}
	
	public void draw(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform oldTransform = g2d.getTransform();
		
		g2d.translate(this.world.getPosition().getX() + this.worldPos.getX(),
				this.world.getPosition().getY() + this.worldPos.getY());
		g2d.rotate(Math.toRadians(rotation));
		
		g2d.setColor(new Color(255, 255, 0));
		g2d.fillRect((int)(square.getWidth()/2 * -1), (int)(square.getHeight()/2 * -1), (int)square.getWidth(), (int)square.getHeight());
		g2d.setColor(new Color(153, 102, 0));
		g2d.drawRect((int)(square.getWidth()/2 * -1), (int)(square.getHeight()/2 * -1), (int)square.getWidth(), (int)square.getHeight());
		
		g2d.setTransform(oldTransform);
		
		this.healthbar.draw(g);
	}
	
	public void takeDamage(int damage, PlayerMP damageDealer)
	{
		//System.out.println("Square damaged at: " + this.worldPos.x + "," + this.worldPos.y);
		this.lastDamageDealer = damageDealer;
		this.health -= damage;
	}
	
	private void respawn()
	{
		world.removeEntity(this);
		if (this.lastDamageDealer != null)
		{
			this.lastDamageDealer.addScore((int)(Math.random() * 100) + 50);
			Packet42UpdatePlayer packetU = new Packet42UpdatePlayer(this.lastDamageDealer.username, this.lastDamageDealer.health, this.lastDamageDealer.kills, this.lastDamageDealer.score,
					this.lastDamageDealer.playerColor.getRed(),this.lastDamageDealer.playerColor.getGreen(), this.lastDamageDealer.playerColor.getBlue());
			packetU.writeData(MainPanel.thisClient.socketClient);
		}
		
		if (MainPanel.thisClient.socketServer != null)
		{
			Packet41UpdateSquare packet = new Packet41UpdateSquare((int)(Math.random() * world.getWidth()) + 1,
					(int)(Math.random() * world.getHeight()) + 1,
					100,
					25);
			packet.writeData(MainPanel.thisClient.socketClient);
		}
	}
	
	public Rectangle getHitbox()
	{
		//Compensate for drawing offset
		return new Rectangle(square.x - (square.width/2), square.y - (square.height/2), square.width, square.height);
	}

	public void serverUpdate()
	{
		this.lastServerUpdate = System.currentTimeMillis();
	}
	
}
