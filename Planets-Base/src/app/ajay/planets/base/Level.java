package app.ajay.planets.base;

import java.util.ArrayList;

public class Level {
	public ArrayList<Planet> planets = new ArrayList<>();
	
	public ArrayList<Player> players = new ArrayList<>();
	
	/**
	 * the amount of time the last frame took. Used to make all movement smooth towards the frame rate
	 */
	public float deltaTime = 1;
	
	public void update() {
		
		//update all players
		for (Player player : players) {
			player.update(this);
		}
		
		//update all planets
		for (Planet planet : planets) {
			planet.update(this);
		}
	}
	
}
