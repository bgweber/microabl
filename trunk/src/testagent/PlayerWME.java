package testagent;

import microabl.wm.WME;

public class PlayerWME extends WME {
 
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
		return "PlayerWME (" + x + ", " + y + ")";
	}
	
	public String getName() {
		return "Player"; 
	}
}
