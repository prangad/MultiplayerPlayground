package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import assets.World;
import util.DVector;

public class Bullet extends Entity {
	private boolean disposed = false;
	
	private DVector worldPos, vel;
	private double size;
	public long creationTime;
	public PlayerMP owner;
	
	
	public Bullet(World world, PlayerMP player, DVector vel, double size, long creationTime)
	{
		this.world = world;
		this.worldPos = player.worldPos;
		this.owner = player;
		this.vel = vel;
		this.size = size;
		this.creationTime = creationTime;
	}
	
	public void update()
	{
		this.worldPos = this.worldPos.add(this.vel);
		if (System.currentTimeMillis() - this.creationTime >= 2000 && !disposed)
		{
			dispose();
		}
	}
	
	public void draw(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform oldTransform = g2d.getTransform();
		
		g2d.translate(this.world.getPosition().getX() + this.worldPos.getX(),
				this.world.getPosition().getY() + this.worldPos.getY());
		
		g2d.setColor(new Color(255, 0, 0));
		g2d.fillOval((int)(this.size/2) * -1, (int)(this.size/2) * -1, (int)this.size, (int)this.size);
		g2d.setColor(new Color(150, 0, 0));
		g2d.drawOval((int)(this.size/2) * -1, (int)(this.size/2) * -1, (int)this.size, (int)this.size);
			
		g2d.setTransform(oldTransform);
	}
	
	public DVector getPosition()
	{
		return this.worldPos;
	}
	
	public Rectangle getHitbox()
	{
		return new Rectangle((int)this.worldPos.getX(), (int)this.worldPos.getY(), (int)this.size, (int)this.size);
	}
	
	private void dispose()
	{
		disposed = true;
		world.removeEntity(this);
	}
	
}
