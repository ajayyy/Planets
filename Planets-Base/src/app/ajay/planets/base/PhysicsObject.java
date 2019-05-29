package app.ajay.planets.base;

public class PhysicsObject {
	
	public float x, y;
	public float xSpeed, ySpeed;
	
	public void update(Level level) {
		//change by speeds
		x += xSpeed * level.deltaTime;
		y += ySpeed * level.deltaTime;
	}
}
