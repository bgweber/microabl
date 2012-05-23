package chaseragent;

import microabl.wm.WME;

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
	 
	public static boolean greaterThanSum(Integer a, Integer b, Integer c) {
		return a > (b + c);
	}

	public static boolean lessThanDiff(Integer a, Integer b, Integer c) {
		return a < (b - c);
	}
	
	public String toString() {
		return "ChaserWME (" + x + ", " + y + ")";
	}
}
