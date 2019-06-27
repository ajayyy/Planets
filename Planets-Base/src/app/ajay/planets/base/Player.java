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
	 * These are the player old states that were just last used.
	 * This is used after rolling back frames to be able to resimulate up to the correct
	 * frame and still know what controls the player would have clicked in the future (if they have).
	 */
	List<PlayerOldState> previousPlayerOldStates = new ArrayList<PlayerOldState>();
	
	/**
	 * Used during frames. If a projectile has been launched this frame.
	 */
	public boolean projectileLaunched = false;
	public float projectileXLaunchDirection, projectileYLaunchDirection;
	
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
		super.update(level);
		
		if (right || left) {
			//moving
			
			//find nearest planet
			//this planet will be used to move against
			Planet closestPlanet = getClosestPlanet(level.planets, x, y);
			
			//find the components of the objects angle with the planet
			double xDistanceToPlanet = x - closestPlanet.x;
			double yDistanceToPlanet = y - closestPlanet.y;
			double magnitudeDistanceToPlanet = MathHelper.getDist(xDistanceToPlanet, yDistanceToPlanet);
			
			//change speeds based on movement
			if (right) {
				//the actual movement that is needed, is the perpendicular slope, which is the negative reciprocal
				xSpeed -= (-yDistanceToPlanet / magnitudeDistanceToPlanet) * movementSpeed * level.deltaTime;
				ySpeed -= (xDistanceToPlanet / magnitudeDistanceToPlanet) * movementSpeed * level.deltaTime;
			} else if (left) {
				//the actual movement that is needed, is the perpendicular slope, which is the negative reciprocal
				xSpeed += (-yDistanceToPlanet / magnitudeDistanceToPlanet) * movementSpeed * level.deltaTime;
				ySpeed += (xDistanceToPlanet / magnitudeDistanceToPlanet) * movementSpeed * level.deltaTime;
			}
		}
	}
	
	/**
	 * 	Happens after update, mainly just saving the old state and dealing with projectiles
	 */
	public void postUpdate(Level level) {
		if (projectileLaunched) {
			//there has been a projectile launch queued up
			launchProjectile(level, projectileXLaunchDirection, projectileYLaunchDirection);
			
			//reset projectile information
			projectileLaunched = false;
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
	public void launchProjectile(Level level, float xLaunchDirection, float yLaunchDirection) {
		//place it right at the edge of the player
		float projectileX = (float) (x + radius * xLaunchDirection);
		float projectileY = (float) (y + radius * yLaunchDirection);
		
		Projectile projectile = null;
		try {
			projectile = (Projectile) projectileClass.getDeclaredConstructor(float.class, float.class, float.class, float.class).newInstance(projectileX, projectileY, xLaunchDirection, yLaunchDirection);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		//add the opposite force to the player
		xSpeed += (float) ((-xLaunchDirection) * projectile.projectileStrength * 0.3f);
		ySpeed += (float) ((-yLaunchDirection) * projectile.projectileStrength * 0.3f);
		
		level.projectiles.add(projectile);
		
		projectileXLaunchDirection = xLaunchDirection;
		projectileYLaunchDirection = yLaunchDirection;
	}
	
	/**
	 * Saves an old state of this frame at this current time.
	 */
	public void saveOldState(Level level) {
		playerOldStates.add(new PlayerOldState(level.frame, x, y, xSpeed, ySpeed, left, right, alive, projectileLaunched, projectileXLaunchDirection, projectileYLaunchDirection));
		
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
	
	/**
	 * Will remove the player old states from this frame and higher.
	 * 
	 * @param minFrame The lowest frame to be removed
	 */
	public void removePlayerOldStates(long minFrame) {
		for (PlayerOldState playerOldState: new ArrayList<>(playerOldStates)) {
			if (playerOldState.frame >= minFrame) {
				//it's too recent
				playerOldStates.remove(playerOldState);
			}
		}
	}
	
	/**
	 * Gets the previous old state for that frame of this player.
	 * 
	 * If none are found, returns null.
	 * 
	 * This gets it from the previousPlayerOldStates list instead of the playerOldStates
	 * 
	 * @param frame The frame of the old state
	 * @return The old state at that frame
	 */
	public PlayerOldState getPreviousOldStateAtFrame(long frame) {
		for (PlayerOldState oldState: previousPlayerOldStates) {
			if (oldState.frame == frame) {
				return oldState;
			}
		}
		
		return null;
	}
	
}
