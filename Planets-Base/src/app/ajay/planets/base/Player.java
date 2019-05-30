package app.ajay.planets.base;

public class Player extends PhysicsObject {
	
	public boolean right, left;
	
	public boolean alive;
	
	public float movementSpeed = 500;
	
	public float radius = 25;
	
	public Player(float x, float y) {
		super();
		
		this.x = x;
		this.y = y;
	}
	
	public void update(Level level) {
		super.update(level);
		
		//change speeds based on movement
		if (right) {
			xSpeed += movementSpeed * level.deltaTime; 
		} else if (left) {
			xSpeed -= movementSpeed * level.deltaTime; 
		}
	}
	
}
