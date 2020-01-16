package assets;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import entities.Bullet;
import entities.Entity;
import entities.Player;
import entities.PlayerMP;
import entities.Square;
import util.Config;
import util.DVector;

public class World {
	
	public static World thisWorld;
	
	private final int WIDTH = Integer.parseInt(Config.cfg.getProperty("window-width"));
	private final int HEIGHT = Integer.parseInt(Config.cfg.getProperty("window-height"));
	
	public Rectangle world;
	private DVector screenPos = new DVector(0, 0);
	
	//Scoreboard
	int maxWidth = 0;
	int fontHeight = 0;
	
	public boolean populated = false;
	
	private ArrayList<PlayerMP> players = new ArrayList<PlayerMP>();
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	
	public World(int width, int height)
	{
		thisWorld = this;
		this.populated = false;
		world = new Rectangle(width, height);
		//populate();
	}
	
	public void update(Player localPlayer)
	{
		//System.out.println("Entity Count: " + entities.size());
		//System.out.println("Player Count: " + players.size());
		
		for (int i = 0; i < entities.size(); i++)
		{
			if (entities.get(i) != null)
				entities.get(i).update();
		}
		
		for (int i = 0; i < players.size(); i++)
		{
			if (players.get(i) != null)
				players.get(i).update();
		}
		
		if (localPlayer != null)
		{
			this.screenPos = new DVector((WIDTH/2) - localPlayer.worldPos.getX(),
					(HEIGHT/2) - localPlayer.worldPos.getY());
		}
		
		checkCollisions();
	
		Collections.sort(players, new Comparator<PlayerMP>() {
		    @Override
		    public int compare(PlayerMP p1, PlayerMP p2) {
		        if (p1.score < p2.score)
		            return 1;
		        if (p1.score > p2.score)
		            return -1;
		        return 0;
		    }
		});
		
	}
	
	public void draw(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform oldTransform = g2d.getTransform();
		
		g2d.setColor(new Color(50,50,50));
		g2d.fillRect((int)this.screenPos.getX(), (int)this.screenPos.getY(), world.width, world.height);
		g2d.setColor(new Color(120,120,120));
		g2d.drawRect((int)this.screenPos.getX(), (int)this.screenPos.getY(), world.width, world.height);
		
		for (int i = 0; i < entities.size(); i++)
		{
			if (entities.get(i) != null)
				entities.get(i).draw(g);
		}

		g2d.setColor(new Color(150, 150, 150, 150));
		g2d.fillRect(WIDTH - maxWidth - 25, 10, maxWidth + 10, (players.size() * fontHeight));
		g2d.setColor(Color.WHITE);
		g2d.drawRect(WIDTH - maxWidth - 25, 10, maxWidth + 10, (players.size() * fontHeight));
		
		for (int i = 0; i < players.size(); i++)
		{
			if (players.get(i) != null)
			{
				PlayerMP player = players.get(i);
				player.draw(g);
				
				Font scoreFont = new Font("Helvetica", Font.BOLD, 14);
				g2d.setFont(scoreFont);
				FontMetrics metrics = g2d.getFontMetrics(scoreFont);
				fontHeight = metrics.getHeight();
				
				int entryLength = metrics.stringWidth((i+1) + ". " + player.username + ": " + player.score + "(" + player.kills + ")");
				if (entryLength > maxWidth)
					maxWidth = entryLength;
				
				g2d.setColor(player.playerColor);
				g2d.drawString((i+1) + ". " + player.username + ": " + player.score + "(" + player.kills + ")",
						WIDTH - maxWidth - 20,
						(i+1) * fontHeight + 5);
			}
			
		}
		
		g2d.setTransform(oldTransform);
		
		
		
	}
	
	public void addEntity(Entity entity)
	{
		//System.out.println("Adding Entity: " + entity);
		
		if (entity.getClass() == PlayerMP.class)
		{
			//System.out.println("ADDING PLAYER TO PLAYER LIST");
			players.add((PlayerMP)entity);
			//System.out.println("World Player Count: " + players.size());
		}
		else
		{
			entities.add(entity);
		}
	}
	
	public void removeEntity(Entity entityToRemove)
	{
		if (entityToRemove != null)
			entities.remove((Entity)entityToRemove);
	}
	
	public void removeEntity(String username)
	{
		for (int i = 0; i < players.size(); i++)
		{
			if (players.get(i).getUsername().equals(username))
			{
				players.remove(players.get(i));
				break;
			}
		}
	}
	
	public void populate()
	{
		this.populated = true;
		for (int i = 0; i < 100; i++)
		{
			addEntity(new Square(this, new DVector((int)(Math.random() * this.getWidth()) + 1,
							(int)(Math.random() * this.getHeight()) + 1), 25));
		}
	}
	
	private void checkCollisions()
	{
		for (int i = 0; i < entities.size(); i++)
		{
			if (entities.get(i) instanceof Square)
			{
				Square square = (Square)entities.get(i);
				for (int j = 0; j < entities.size(); j++)
				{
					if (entities.get(j) instanceof Bullet)
					{
						if (square.getHitbox().intersects(entities.get(j).getHitbox()))
						{
							square.takeDamage(20, ((Bullet)entities.get(j)).owner);
							removeEntity(entities.get(j));
						}
					}
				}
			}
			else if (entities.get(i) instanceof Bullet)
			{
				Bullet b1 = (Bullet)entities.get(i);
				{
					for (int j = 0; j < entities.size(); j++)
					{
						if (entities.get(j) instanceof Bullet)
						{
							Bullet b2 = (Bullet)entities.get(j);
							if (b1.getHitbox().intersects(b2.getHitbox()) && b1.owner != b2.owner)
							{
								removeEntity(b1);
								removeEntity(b2);
							}	
						}
					}
				}
			}
		}
		for (int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			for (int j = 0; j < entities.size(); j++)
			{
				if (entities.get(j) instanceof Bullet)
				{
					Bullet b = ((Bullet)entities.get(j));
					if (b.getHitbox().intersects(p.getHitbox()))
					{
						if (!(b.owner.equals(p)))
						{
							p.takeDamage(20, b.owner);
							removeEntity(b);
						}
					}
				}
			}
		}
	}
	
	//Get and Set Methods
	public DVector getPosition()
	{
		return this.screenPos;
	}
	
	public double getWidth()
	{
		return this.world.getWidth();
	}
	
	public double getHeight()
	{
		return this.world.getHeight();
	}
	
	public PlayerMP getPlayerMP(String username)
	{
		for (int i = 0; i < players.size(); i++)
		{
			if (players.get(i).username.equals(username))
			{
				return players.get(i);
			}
		}
		return null;
	}
	
//	private int getPlayerMPIndex(String username)
//	{
//		int index = 0;
//		for (PlayerMP p : players)
//		{
//			if (p.getUsername().equals(username))
//			{
//				break;
//			}
//			index++;
//		}
//		return index;
//	}
	
	public Square getSquare(DVector position)
	{
		for (int i = 0; i < entities.size(); i++)
		{
			if (entities.get(i) instanceof Square)
			{
				Square s = (Square)entities.get(i);
				if (s.worldPos.x == position.x && s.worldPos.y == position.y)
				{
					return ((Square)entities.get(i));
				}
			}
		}
		return null;
	}
	
	public void movePlayer(String username, double x, double y, double angle)
	{
		PlayerMP player = getPlayerMP(username);
		player.worldPos.x = x;
		player.worldPos.y = y;
		player.worldAngle = angle;
	}
	
	public void playerShoot(String username, DVector shootVector)
	{
		getPlayerMP(username).shoot(username, shootVector);
	}
	
	public ArrayList<Entity> getEntities()
	{
		return this.entities;
	}
}