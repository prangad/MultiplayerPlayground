package assets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import util.DVector;

public class Healthbar {
	
	private DVector ownerPos;
	private World world;
	private double opacity;
	private int initialHealth;
	private long healthChange;
	private int altHealth;
	private int health;
	
	public Healthbar(DVector ownerPos, int health, World world)
	{
		this.ownerPos = ownerPos;
		this.initialHealth = health;
		this.health = health;
		this.altHealth = health;
		this.opacity = 0;
		this.world = world;
	}
	
	public void update(DVector ownerPosition, int health)
	{
		this.ownerPos = ownerPosition;
		this.health = health;
		
		if (this.health <= 0)
		{
			this.health = 0;
		}
		
		if (this.health != this.altHealth)
		{
			this.altHealth = this.health;
			this.healthChange = System.currentTimeMillis();
		}
		
		if ((System.currentTimeMillis() - this.healthChange) < 1000)
			this.opacity = 255;
		
		if ((System.currentTimeMillis() - this.healthChange) > 1000)
			this.opacity -= 1;
		
		if (this.opacity <= 0)
			this.opacity = 0;
	}
	
	public void draw(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform oldTransform = g2d.getTransform();
		
		g2d.translate(this.world.getPosition().getX() + ownerPos.getX(),
				this.world.getPosition().getY() + ownerPos.getY());
		
		g.setColor(new Color(255, 0, 0, (int)this.opacity));
		g.fillRect(-25, -20, 50, 4);
		g.setColor(new Color(0, 255, 0, (int)this.opacity));
		g.fillRect(-25, -20, (int)(50 * ((double)this.health/(double)this.initialHealth)), 4);
		g.setColor(new Color(200, 200, 200, (int)this.opacity));
		g.drawRect(-25, -20, 50, 4);
		
		g2d.setTransform(oldTransform);
	}
	
}
