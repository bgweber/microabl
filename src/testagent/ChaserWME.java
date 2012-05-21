package testagent;

import abllite.wm.WME;

public class ChaserWME extends WME {
 
	private int x = 0;
	
	private int y = 0;
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}	
	 
	public String toString() {
		return "ChaserWME (" + x + ", " + y + ")";
	}
	
	public String getName() {
		return "Chaser";  
	} 
 	  
	public static void doStuff(Integer x) {
		System.err.println("Do stuff invoked! " + x);
	}
	
	public static boolean validTrajectory(String message, Integer x) {
		System.err.println("I was called!: " + message + "  " + x);
		return true; 
	}
}
