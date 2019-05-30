package app.ajay.planets.base;

public class PhysicsObject extends WorldObject {
	
	public float xSpeed, ySpeed;
	
	public void update(Level level) {
		super.update(level);
		
		//change by speeds
		x += xSpeed * level.deltaTime;
		y += ySpeed * level.deltaTime;
	}
}
