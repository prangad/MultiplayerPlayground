package util;
public class DVector {
    public double x;
    public double y;

    public DVector(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public DVector add(DVector other)
    {
        return new DVector(x + other.x, y + other.y);
    }

    public DVector sub(DVector other)
    {
        return new DVector(x - other.x, y - other.y);
    }
    
    public DVector mult(DVector other)
    {
    	return new DVector(x * other.x, y * other.y);
    }
    
    public DVector mult(double scale)
    {
    	return new DVector(x * scale, y * scale);
    }
    
    public DVector div(double num)
    {
    	return new DVector(x / num, y / num);
    }
    
    public double getX()
    {
    	return this.x;
    }
    
    public double getY()
    {
    	return this.y;
    }
    
    public double getDistanceSquared(DVector other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return dx*dx+dy*dy;
    }
    
    public double getDirection()
    {
    	return Math.toDegrees(Math.atan2(y,x));
    }
    
    public double getMagnitude()
    {
    	return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
    
    public void setMagnitude(double magnitude)
    {
    	this.x = (magnitude * Math.cos(Math.toRadians(this.getDirection())));
    	this.y = (magnitude * Math.sin(Math.toRadians(this.getDirection())));
    }
    
    public DVector rotateDegrees(double rotation)
    {
    	double angle = this.getDirection() + rotation;
    	double magnitude = this.getMagnitude();
    	
    	double tx = (magnitude) * (Math.cos(Math.toRadians(angle)));
    	double ty = (magnitude) * (Math.sin(Math.toRadians(angle)));
    	
    	return new DVector(tx, ty);
    }
}