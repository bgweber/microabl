package chaseragent;

import java.awt.Point;
/**
 * Records the location and trajectory of a bullet.
 * 
 * @author Ben Weber 3-7-11
 */
public class Bullet {

	/** position of the bullet */
	double x;
	double y;

	/** trajectory of the bullet */
	double dx;
	double dy;
	
	/** is the bullet motionless? */
	boolean idle = false;

	/** bullet speed */
	public static final double BulletSpeed = 10.0;
	
	/**
	 * Creates a bullet that will move towards the target location. 
	 */
	public Bullet(Point source, Point target) {
		x = source.x;
		y = source.y;
		
		dx = target.x - source.x;
		dy = target.y - source.y;
		double magnitude = Math.sqrt(dx*dx + dy*dy);
		
		if (magnitude > 0) {
			dx = BulletSpeed*dx/magnitude;
			dy = BulletSpeed*dy/magnitude;
		}
		else {
			idle = true;
		}
	}

	/**
	 * Updates the position of the bullet;
	 */
	public void update() {
		x += dx;
		y += dy;
	}
	
	/**
	 * Is the bullet motionless?
	 */
	public boolean isIdle() {
		return idle;
	}
	
	/**
	 * Returns the x location of the bullet (in pixels).
	 */
	public int getX() {
		return (int)x;
	}
	
	/**
	 * Returns the y location of the bullet (in pixels).
	 */
	public int getY() {
		return (int)y;
	}
}
