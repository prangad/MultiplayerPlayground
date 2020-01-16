package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;

import javax.swing.JColorChooser;
import javax.swing.Timer;

import assets.Healthbar;
import assets.World;
import client.MainPanel;
import net.packets.Packet30Start;
import util.Config;
import util.DVector;

public class Player extends Entity implements MouseMotionListener, KeyListener, MouseListener, ActionListener {
	
	//Config
	private final int WIDTH = Integer.parseInt(Config.cfg.getProperty("window-width"));
	private final int HEIGHT = Integer.parseInt(Config.cfg.getProperty("window-height"));
	
	//Entity Variables
	public DVector worldPos;
	protected DVector vel;
	protected DVector acc;
	protected World world;
	public int health;
	protected Healthbar healthbar;
	
	protected int fireMode = 0;
	protected int fireRate = 100;
	
	public int kills;
	public int score;
	
	//Drawing Variables
	protected int size;
	protected int[] xPoints = new int[4];
	protected int[] yPoints = new int[4];
	protected double angle = 0.0;
	public double worldAngle;
	protected Point mouseLocation = new Point();
	public Color playerColor;
	
	//Movement Variables
	private boolean UP = false;
	private boolean DOWN = false;
	private boolean LEFT = false;
	private boolean RIGHT = false;
	protected boolean isMoving = false;
	
	protected boolean outOfBounds = false;
	protected long oobTimer = System.currentTimeMillis();
	
	protected PlayerMP lastDamageDealer;
	
	private Timer shootTimer = new Timer(fireRate, this);
	
	public Player(World world, double x, double y, int size)
	{
		this.world = world;
		this.worldPos = new DVector(x, y);
		this.vel = new DVector(0, 0);
		this.acc = new DVector(0, 0);
		this.size = size;
		this.health = 1000;
		this.healthbar = new Healthbar(this.worldPos, this.health, world);
		this.playerColor = new Color(100, 0 , 0);
		this.kills = 0;
		this.score = 0;
		this.lastDamageDealer = null;
		this.fireMode = 0;
		this.fireRate = 100;
		
		xPoints[0] = 0 - (3 * this.size);
		xPoints[1] = 0 - (3 * this.size) + (3 * this.size);
		xPoints[2] = 0 - (3 * this.size) + (6 * this.size);
		xPoints[3] = 0 - (3 * this.size) + (3 * this.size);
		yPoints[0] = 0 - (5 * this.size) + (8 * this.size);
		yPoints[1] = 0 - (5 * this.size);
		yPoints[2] = 0 - (5 * this.size) + (8 * this.size);
		yPoints[3] = 0 - (5 * this.size) + (6 * this.size);
		
	}
	
	public void update()
	{
		if (this.UP)
			this.vel = this.vel.add(new DVector(0, -.15));
		if (this.LEFT)
			this.vel = this.vel.add(new DVector(-.15, 0));
		if (this.DOWN)
			this.vel = this.vel.add(new DVector(0, .15));
		if (this.RIGHT)
			this.vel = this.vel.add(new DVector(.15, 0));
		
		this.worldPos = this.worldPos.add(this.vel);
		this.vel = this.vel.add(this.acc);

		double xBuff = 0.0;
		double yBuff = 0.0;
		
		if (this.vel.getX() > 0.02)
			xBuff = this.vel.getX() * 0.02;
		else if (this.vel.getX() < -0.02)
			xBuff = this.vel.getX() * 0.02;
		
		if (this.vel.getY() > 0.02)
			yBuff = this.vel.getY() * 0.02;
		else if (this.vel.getY() < -0.02)
			yBuff = this.vel.getY() * 0.02;
		
		if (!(xBuff == 0.0 && yBuff == 0.0))
			this.vel = this.vel.sub(new DVector(xBuff, yBuff));
		else
			this.vel = new DVector(0, 0);
		
		this.angle = Math.atan2(this.mouseLocation.getY() - (0 + HEIGHT/2),
				this.mouseLocation.getX() - (0 + WIDTH/2)) + (Math.PI/2);
		
		if ((this.vel.x != 0) || this.vel.y != 0)
			this.isMoving = true;
		else
			this.isMoving = false;
		
		if (!world.world.intersects(this.getHitbox()))
		{
			if (!this.outOfBounds)
				this.oobTimer = System.currentTimeMillis();
			
			this.outOfBounds = true;
			
			if ((System.currentTimeMillis() - this.oobTimer) >= 5000)
				this.takeDamage((int)(System.currentTimeMillis() - this.oobTimer)/5000, null);
		}
		else
		{
			this.outOfBounds = false;
		}
	}
	
	public void draw(Graphics g)
	{	
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform oldTransform = g2d.getTransform();
		
		g2d.translate(WIDTH/2, HEIGHT/2);
		g2d.rotate(angle);
		
		g2d.setColor(this.playerColor);
		g2d.fillPolygon(xPoints, yPoints, 4);
		
		g2d.setColor(Color.WHITE);
		g2d.drawPolygon(xPoints, yPoints, 4);
		
		g2d.setTransform(oldTransform);
	}
	
	public Rectangle getHitbox()
	{
		return new Rectangle((int)this.worldPos.getX() - ((8 * size)/2), (int)this.worldPos.getY() - ((8 * size)/2), 8 * size, 8 * size);
	}
	
	public void shoot(String username, DVector shootVector)
	{	
		world.addEntity(new Bullet(world, world.getPlayerMP(username), shootVector, 12, System.currentTimeMillis()));
	}
	
	public void takeDamage(int damage, PlayerMP player)
	{
		this.lastDamageDealer = player;
		this.health -= damage;
	}
	
	protected void updatePlayer()
	{
		
	}
	
	public void addScore(int score)
	{
		this.score += score;
	}
	
	//Listener Methods
	public void mouseMoved(MouseEvent e)
	{
		mouseLocation = e.getPoint();
	}
	
	public void mouseDragged(MouseEvent e)
	{
		mouseLocation = e.getPoint();
	}

	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_W:
			UP = true;
			break;
		case KeyEvent.VK_A:
			LEFT = true;
			break;
		case KeyEvent.VK_S:
			DOWN = true;
			break;
		case KeyEvent.VK_D:
			RIGHT = true;
			break;
		case KeyEvent.VK_F1:
			if (MainPanel.thisClient.socketServer != null && world.populated == false)
			{
				Packet30Start packet = new Packet30Start(MainPanel.thisClient.username);
				packet.writeData(MainPanel.thisClient.socketClient);
			}
			break;
		case 192:
			Color userInput = JColorChooser.showDialog(null, "What color would you like to be?", this.playerColor);
			if (userInput != null)
			{
				this.playerColor = userInput;
			}
			updatePlayer();
			break;
		case KeyEvent.VK_1:
			this.fireRate = 100;
			this.shootTimer.setDelay(fireRate);
			this.fireMode = 0;
			break;
		case KeyEvent.VK_2:
			this.fireRate = 100;
			this.shootTimer.setDelay(fireRate);
			this.fireMode = 1;
			break;
		case KeyEvent.VK_3:
			this.fireRate = 100;
			this.shootTimer.setDelay(fireRate);
			this.fireMode = 2;
			break;
		case KeyEvent.VK_4:
			this.fireRate = 100;
			this.shootTimer.setDelay(fireRate);
			this.fireMode = 3;
			break;
		case KeyEvent.VK_5:
			this.fireRate = 6;
			this.shootTimer.setDelay(fireRate);
			this.fireMode = 4;
			break;
		case KeyEvent.VK_6:
			this.fireRate = 10;
			this.shootTimer.setDelay(fireRate);
			this.fireMode = 5;
			break;
		case KeyEvent.VK_7:
			this.fireRate = 1000;
			this.shootTimer.setDelay(fireRate);
			this.fireMode = 6;
			break;
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_W:
			UP = false;
			break;
		case KeyEvent.VK_A:
			LEFT = false;
			break;
		case KeyEvent.VK_S:
			DOWN = false;
			break;
		case KeyEvent.VK_D:
			RIGHT = false;
			break;
		}
	}
	public void keyTyped(KeyEvent e) {}

	public void mousePressed(MouseEvent e)
	{
		shootTimer.start();
	}
	
	public void mouseReleased(MouseEvent e)
	{
		shootTimer.stop();
	}
	
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void actionPerformed(ActionEvent e){}
}