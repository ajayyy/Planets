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
	
	public void resimulateFrames(Level level, long fromFrame) {
		long framesToSimulate = level.frame - fromFrame;
		
		//reset everything to this frame
		//TODO deal with projectiles as well
		for (Player player: level.players) {
			//this frame's old state
			PlayerOldState fromFrameOldState = player.getOldStateAtFrame(fromFrame);
			
			fromFrameOldState.makePlayerThisState(player);
		}
		
		//set the level frame to the correct frame
		level.frame = fromFrame;
		
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
