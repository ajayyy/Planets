package app.ajay.planets.base;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Player extends PhysicsObject {
	
	public boolean right, left;
	
	public boolean alive;
	
	public float movementSpeed = 300;
	
	public Player(float x, float y) {
		super();
		
		this.x = x;
		this.y = y;
		
		radius = 25;
		canBounce = true;
	}
	
	public void update(Level level) {
		super.update(level);
		
		if (right || left) {
			//moving
			
			//find nearest planet
			//this planet will be used to move against
			Planet closestPlanet = getClosestPlanet(level.planets, x, y);
			
			//find angle from the object
			float planetAngle = (float) Math.atan2(y - closestPlanet.y, x - closestPlanet.x);
			
			//change speeds based on movement
			if (right) {
				float movementAngle = (float) (planetAngle - Math.PI / 2);
				
				xSpeed += Math.cos(movementAngle) * movementSpeed * level.deltaTime;
				ySpeed += Math.sin(movementAngle) * movementSpeed * level.deltaTime;
			} else if (left) {
				float movementAngle = (float) (planetAngle + Math.PI / 2);
				
				xSpeed += Math.cos(movementAngle) * movementSpeed * level.deltaTime;
				ySpeed += Math.sin(movementAngle) * movementSpeed * level.deltaTime;
			}
		}
	}
	
	/**
	 * 
	 * Launch a projectile at this angle. Takes what projectile class to create a new instance of
	 * 
	 * @param projectileClass The class to make a new instance of. Used to create ClientProjectiles when needed.
	 * @param level
	 * @param launchAngle
	 */
	public void launchProjectile(Class<? extends Projectile> projectileClass, Level level, float launchAngle) {
		//place it right at the edge of the player
		float projectileX = (float) (x + radius * Math.cos(launchAngle));
		float projectileY = (float) (y + radius * Math.sin(launchAngle));
		
		Projectile projectile = null;
		try {
			projectile = (Projectile) projectileClass.getDeclaredConstructor(float.class, float.class, float.class).newInstance(projectileX, projectileY, launchAngle);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		//add the opposite force to the player
		xSpeed += (float) (Math.cos(launchAngle + Math.PI) * projectile.projectileStrength * 0.3f);
		ySpeed += (float) (Math.sin(launchAngle + Math.PI) * projectile.projectileStrength * 0.3f);
		
		level.projectiles.add(projectile);
	}
	
}
