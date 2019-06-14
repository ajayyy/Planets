package app.ajay.planets.base;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Player extends PhysicsObject {
	
	public boolean right, left;
	
	public boolean alive;
	
	public float movementSpeed = 300;
	
	//the id of this user, -1 if a client
	public int id = -1;
	
	/** 
	 * A list of the most recent old states of this player.
	 * This is a list of the states of this player at every frame.
	 */
	List<PlayerOldState> playerOldStates = new ArrayList<PlayerOldState>();
	
	/**
	 * Used during frames. If a projectile has been launched this frame.
	 */
	public boolean projectileLaunched = false;
	public float projectileAngle;
	
	/**
	 * The class to launch for projectiles.
	 * Will be set to ClientProjectile on the client.
	 */
	public Class<? extends Projectile> projectileClass = Projectile.class;
	
	public Player(int id, float x, float y) {
		super();
		
		this.id = id;
		this.x = x;
		this.y = y;
		
		radius = 25;
		canBounce = true;
	}
	
	public void update(Level level) {
		//reset projectile information
		projectileLaunched = false;
		projectileAngle = -1;
		
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
	 * 	Happens after update, mainly just saving the old state and dealing with projectiles
	 */
	public void postUpdate(Level level) {
		if (projectileLaunched) {
			//there has been a projectile launch queued up
			launchProjectile(level, projectileAngle);
		}
		
		//save an old state of this frame
		saveOldState(level);
	}
	
	/**
	 * 
	 * Launch a projectile at this angle. Takes what projectile class to create a new instance of
	 * 
	 * @param projectileClass The class to make a new instance of. Used to create ClientProjectiles when needed.
	 * @param level
	 * @param launchAngle
	 */
	public void launchProjectile(Level level, float launchAngle) {
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
		
		projectileLaunched = true;
		projectileAngle = launchAngle;
	}
	
	/**
	 * Saves an old state of this frame at this current time.
	 */
	public void saveOldState(Level level) {
		playerOldStates.add(new PlayerOldState(level.frame, x, y, xSpeed, ySpeed, left, right, alive, projectileLaunched, projectileAngle));
		
		if (playerOldStates.size() > 300) {
			playerOldStates.remove(0);
		}
	}
	
	/**
	 * Gets the old state for that frame of this player.
	 * 
	 * If none are found, returns null
	 * 
	 * @param frame The frame of the old state
	 * @return The old state at that frame
	 */
	public PlayerOldState getOldStateAtFrame(long frame) {
		for (PlayerOldState oldState: playerOldStates) {
			if (oldState.frame == frame) {
				return oldState;
			}
		}
		
		return null;
	}
	
}
