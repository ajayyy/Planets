package app.ajay.planets.base;

public class Planet extends WorldObject {
	
	public float radius;
	
	public Planet(float x, float y, float radius) {
		super();
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	public void update(Level level) {
		super.update(level);
	}
}
