package app.ajay.planets.base;

public class Projectile extends PhysicsObject {
	
	/**
	 * The default strength a projectile will be
	 * The speed it moves
	 */
	public float projectileStrength = 400f;
	
	public Projectile(float x, float y, float xLaunchDirection, float yLaunchDirection) {
		super();
		
		radius = 3;
		
		//move this out of the player to not be colliding
		this.x = (float) (x + radius * 2 * xLaunchDirection);
		this.y = (float) (y + radius * 2 * yLaunchDirection);
		
		xSpeed = (float) (xLaunchDirection * projectileStrength);
		ySpeed = (float) (yLaunchDirection * projectileStrength);
		
	}
	
	public void update(Level level) {
		super.update(level);
		
		if (collided) {
			//destroy this projectile
			level.projectiles.remove(this);
		}
	}
	
}
