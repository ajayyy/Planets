package app.ajay.planets.base;

public class Projectile extends PhysicsObject {
	
	/**
	 * The default strength a projectile will be
	 * The speed it moves
	 */
	public float projectileStrength = 400f;
	
	public Projectile(float x, float y, float angle) {
		super();
		
		this.x = x;
		this.y = y;
		
		xSpeed = (float) (Math.cos(angle) * projectileStrength);
		ySpeed = (float) (Math.sin(angle) * projectileStrength);
		
		radius = 3;
	}
	
	public void update(Level level) {
		super.update(level);
		
		if (collided) {
			//destroy this projectile
			level.projectiles.remove(this);
		}
	}
	
}
