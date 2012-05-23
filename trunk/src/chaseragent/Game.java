package chaseragent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
/**
 *  Simple "game" for showing how to interface an ABL agent. 
 */
public class Game extends JPanel implements KeyListener {

	/** scene dimensions */
	private Point dimensions = new Point(640, 480);
	
	/** location of the player character */
//	private Point playerLocation = new Point((int)(dimensions.x*Math.random()), (int)(dimensions.y*Math.random()));
	private Point playerLocation = new Point(100, 100);

	/** trajectory of the player character */
	private Point playerTrajectory = new Point(0, 0);
	
	/** location of the chaser */
//	private Point chaserLocation = new Point((int)(dimensions.x*Math.random()), (int)(dimensions.y*Math.random()));
	private Point chaserLocation = new Point(200, 300);
	
	/** trajectory of the chaser */
	private Point chaserTrajectory = new Point(0, 0);

	/** size of the player character */
	private static final int playerSize = 10;

	/** size of the bullets */
	private static final int bulletSize = 4;

	/** speed of the player character */
	private static final int PlayerSpeed = 4;

	/** speed of the player character */
	public static final int ChaserSpeed = 2;

	/** keys held down */
	private boolean[] keyPresses = new boolean[256];

	/** did the player fire a bullet */
	private boolean spawnBullet = false;
	
	/** bullets which have been fired by both players */
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	
	/** spawn a chaser bullet? */
	private boolean spawnChaserBullet = false;

	/** source position of the chaser bullet */
	private Point chaserBulletSource; 
	
	/** target position of the chaser bullet */
	private Point chaserBulletTarget; 
	
	/** the ABL agent */
	private ChaserAgent agent; 

	/** 
	 * Starts the game.
	 */ 
	public static void main(String[] args) {
		new Game();
	}

	/**
	 * Instantiates the game and places the instance in a JFrame. 
	 */
	private Game() {
		agent = new ChaserAgent(this);
		
		// spawn an update thread
		new Thread() {
			public void run() {
				setName("Game Update Thread");
				
				while (true) {
					try {
						agent.update();
						updateLocations();
						updateBullets();
						repaint();
						Thread.sleep(50);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			} 
		}.start();		
		 
		// display the game 
		setPreferredSize(new Dimension(dimensions.x, dimensions.y));
		JFrame frame = new JFrame("ABL Chaser");
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(this);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	/**
	 * Updates the positions of objects, and draws the scene.
	 */
	public void paint(Graphics g) {
		super.paint(g); 

		g.setColor(Color.BLUE);
		g.fillRect(playerLocation.x, playerLocation.y, playerSize, playerSize);
		 
		g.setColor(Color.RED);
		g.fillRect(chaserLocation.x, chaserLocation.y, playerSize, playerSize);

		g.setColor(Color.BLACK);
		for (Bullet bullet : bullets) {
			g.fillRect(bullet.getX() + (playerSize - bulletSize)/2, bullet.getY() + (playerSize - bulletSize)/2, bulletSize, bulletSize);
		}
	}

	/** 
	 * Updates bullet locations and spawns new bullets.
	 */
	public void updateBullets() {

		// update bullet positions
		for (Bullet bullet : bullets) {
			bullet.update();
		}
		
		// check for out of boundary bullets
		ArrayList<Bullet> remove = new ArrayList<Bullet>();
		for (Bullet bullet : bullets) {
			if (bullet.x < 0) {
				remove.add(bullet);
			}
			else if (bullet.x > dimensions.x) {
				remove.add(bullet);
			}
			else if (bullet.y < 0) {
				remove.add(bullet);
			}
			else if (bullet.y > dimensions.y) {
				remove.add(bullet);
			}
		}		
		bullets.removeAll(remove);

		// spawn player bullets
		if (spawnBullet) {
			spawnBullet = false;
			
			Bullet bullet = new Bullet(playerLocation, chaserLocation);
			if (!bullet.isIdle()) {
				bullets.add(bullet);
			}
		}
		
		// spawn chaser bullets
		if (spawnChaserBullet) {
			spawnChaserBullet = false;
			
			Bullet bullet = new Bullet(chaserBulletSource, chaserBulletTarget);
			if (!bullet.isIdle()) {
				bullets.add(bullet);
			}
		}
	}
	
	/**
	 * Updates the positions of objects in the scene based on their trajectories and the dimensions of the scene.
	 */
	public void updateLocations() {
		
		// compute the player trajectory
		int dx = 0;
		int dy = 0;
		
		if (keyPresses[KeyEvent.VK_LEFT]) {
			dx -= PlayerSpeed;
		}
		if (keyPresses[KeyEvent.VK_RIGHT]) {
			dx += PlayerSpeed;
		}
		if (keyPresses[KeyEvent.VK_UP]) {
			dy -= PlayerSpeed;
		}
		if (keyPresses[KeyEvent.VK_DOWN]) {
			dy += PlayerSpeed;
		}
		
		playerTrajectory = new Point(dx, dy);
		
		// update player location
		int playerX = playerLocation.x + playerTrajectory.x;
		playerX = Math.max(0, playerX);
		playerX = Math.min(dimensions.x, playerX);
		
		int playerY = playerLocation.y + playerTrajectory.y;
		playerY = Math.max(0, playerY);
		playerY = Math.min(dimensions.y, playerY);
		
		playerLocation = new Point(playerX, playerY);
		
		// update chaser location
		int chaserX = chaserLocation.x + chaserTrajectory.x;
		chaserX = Math.max(0, chaserX);
		chaserX = Math.min(dimensions.x, chaserX);
		
		int chaserY = chaserLocation.y + chaserTrajectory.y;
		chaserY = Math.max(0, chaserY);
		chaserY = Math.min(dimensions.y, chaserY);
		
		chaserLocation = new Point(chaserX, chaserY);
	}

	/**
	 * Tells the chaser to stop. 
	 * 
	 * Note: This is invoked via ABL physical acts
	 */
	public void stopChaser() {
		chaserTrajectory = new Point(0,0);
	}
 
	/** 
	 * Tells the chaser to move left. 
	 * 
	 * Note: This is invoked via ABL physical acts
	 */
	public void moveChaserLeft() {
		chaserTrajectory = new Point(-ChaserSpeed, 0);
	}

	/**
	 * Tells the chaser to move right. 
	 * 
	 * Note: This is invoked via ABL physical acts
	 */
	public void moveChaserRight() {
		chaserTrajectory = new Point(ChaserSpeed, 0);
	}

	/**
	 * Tells the chaser to move up. 
	 * 
	 * Note: This is invoked via ABL physical acts
	 */
	public void moveChaserUp() {
		chaserTrajectory = new Point(0, -ChaserSpeed);
	}

	/**
	 * Tells the chaser to move down. 
	 * 
	 * Note: This is invoked via ABL physical acts
	 */
	public void moveChaserDown() {
		chaserTrajectory = new Point(0, ChaserSpeed);
	}

	/**
	 * Fires a bullet from the chaser at the player.
	 */
	public void fireChaserBullet(Point source, Point target) {
		chaserBulletSource = source;
		chaserBulletTarget = target;		
		spawnChaserBullet = true;
	}

	/**
	 * Returns the location of the player.
	 */
	public Point getPlayerLocation() {
		return playerLocation;
	}

	/**
	 * Returns the trajectory of the player.
	 */
	public Point getPlayerTrajectory() {
		return playerTrajectory;
	}

	/**
	 * Returns the location of the chaser.
	 */
	public Point getChaserLocation() {
		return chaserLocation;
	}
 
	/**
	 * Returns the trajectory of the chaser.
	 */
	public Point getChaserTrajectory() {
		return chaserTrajectory;
	}

	/**
	 * Records keystate.
	 * 
	 * Note: tracks presses and releases with a boolean value to avoid duplicate key presses.
	 */
	public void keyPressed(KeyEvent e) {		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}		
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE && keyPresses[KeyEvent.VK_SPACE] == false) {
			spawnBullet = true;
		}
		
		if (e.getKeyCode() < keyPresses.length) {
			keyPresses[e.getKeyCode()] = true;
		}
	}

	/**
	 * Release key state. 
	 */
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() < keyPresses.length) {
			keyPresses[e.getKeyCode()] = false;
		}
	}

	public void keyTyped(KeyEvent e) {}
}
