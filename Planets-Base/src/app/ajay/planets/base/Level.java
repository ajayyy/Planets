package app.ajay.planets.base;

import java.util.ArrayList;

public class Level {
	public ArrayList<Planet> planets = new ArrayList<>();
	
	public ArrayList<Player> players = new ArrayList<>();
	
	public ArrayList<Projectile> projectiles = new ArrayList<>();
	
	/**
	 * Set to 30 since it has to be the same on all clients
	 * This means 30 frames per second
	 */
	public int physicsFrameRate = 60;
	
	/**
	 * The amount of time the last frame took. Used to make all movement smooth towards the frame rate.
	 */
	public float deltaTime = 1f/physicsFrameRate;
	
	/** the update frame this program is currently on */
	public long frame = 0;
	
	public void update() {
		
		//update all players
		for (Player player : new ArrayList<>(players)) {
			player.update(this);
			player.postUpdate(this);
		}
		
		//update all planets
		for (Planet planet : planets) {
			planet.update(this);
		}
		
		//update all projectiles
		for (Projectile projectile : new ArrayList<>(projectiles)) {
			projectile.update(this);
		}
	}
	
	/**
	 * Launches projectile as if the game were still at an old frame.
	 * Rolls back to that frame, launches the projectile, then resimulates the frames up to present
	 * 
	 * @param level
	 * @param oldFrame The frame that this event should have happened
	 * @param player
	 * @param projectileAngle
	 */
	public void launchProjectileAtFrame(Level level, long oldFrame, Player player, float projectileAngle) {
		long framesToSimulate = level.frame - oldFrame;
		System.out.println("framesToSimulate: " + framesToSimulate);
		
		//only simulate if necessary
		if (framesToSimulate != 0) {
			rollBackToFrame(level, oldFrame);
		}
		
		//launch the projectile at this frame
		player.projectileLaunched = true;
		player.projectileAngle = projectileAngle;
		
		//only simulate if necessary
		if (framesToSimulate != 0) {
			simulateFrames(level, framesToSimulate);
		}
	}
	
	/**
	 * Rolls back the whole game to a certain frame
	 * 
	 * @param level
	 * @param oldFrame The frame to roll back to
	 */
	public void rollBackToFrame(Level level, long oldFrame) {
		//reset everything to this frame
		for (Player player: level.players) {
			//this frame's old state
			PlayerOldState fromFrameOldState = player.getOldStateAtFrame(oldFrame);
			
			fromFrameOldState.makePlayerThisState(player);
		}
		
		//set the level frame to the correct frame
		level.frame = oldFrame;
	}
	
	/**
	 * @param level
	 * @param framesToSimulate Simulates this many frames happening
	 */
	public void simulateFrames(Level level, long framesToSimulate) {
		for (int i = 0; i < framesToSimulate; i++) {
			//simulate the frames
			
			level.update();
		}
	}
	
	public Player getPlayerById(int id) {
		for (Player player : new ArrayList<>(players)) {
			if (player.id == id) {
				return player;
			}
		}
		
		//no player was found
		return null;
	}
}
