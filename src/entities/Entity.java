package entities;

import java.awt.Graphics;
import java.awt.Rectangle;

import assets.World;
import util.DVector;

@SuppressWarnings("unused")
public abstract class Entity {
	private DVector worldPos, vel, acc;
	protected World world;
	
	public final void init(World world)
	{
		this.world = world;
	}
	
	public abstract void update();
	public abstract void draw(Graphics g);
	public abstract Rectangle getHitbox();
}
