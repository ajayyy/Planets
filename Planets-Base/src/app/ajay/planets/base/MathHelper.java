package app.ajay.planets.base;

public class MathHelper {

	public static double getDotProduct(double x1, double y1, double x2, double y2){
		return x1 * x2 + y1 * y2;
	}
	
	public static double getDist(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	public static double getDist(double x, double y) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	/**
	 * Quick circle collision detection
	 * 
	 * @param x1
	 * @param y1
	 * @return
	 */
	public static boolean isColliding(float x1, float y1, float x2, float y2, float radius1, float radius2) {
		return getDist(x1, y1, x2, y2) < radius1 + radius2;
	}
}